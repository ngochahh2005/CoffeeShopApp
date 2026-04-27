package com.example.coffeeshopapp.presentation.screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.remote.ForgotPasswordRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.remote.ResetPasswordRequestDto
import com.example.coffeeshopapp.data.remote.VerifyOtpRequestDto
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.launch

private enum class ForgotStep { EMAIL, OTP, RESET_PASSWORD }

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var currentStep by remember { mutableStateOf(ForgotStep.EMAIL) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun goBackOneStep() {
        when (currentStep) {
            ForgotStep.EMAIL -> onBack()
            ForgotStep.OTP -> currentStep = ForgotStep.EMAIL
            ForgotStep.RESET_PASSWORD -> currentStep = ForgotStep.OTP
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { goBackOneStep() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TitleColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("🔐", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (currentStep) {
                ForgotStep.EMAIL -> "Quên mật khẩu"
                ForgotStep.OTP -> "Xác nhận OTP"
                ForgotStep.RESET_PASSWORD -> "Đặt lại mật khẩu"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TitleColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (currentStep) {
            ForgotStep.EMAIL -> {
                Text(
                    text = "Nhập email đã đăng ký để nhận mã OTP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LabelColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(context, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        keyboardController?.hide()
                        coroutineScope.launch {
                            try {
                                val resp = NetworkClient.api.forgotPassword(ForgotPasswordRequestDto(email))
                                if (resp.code == 1000) {
                                    Toast.makeText(context, "Đã gửi mã OTP về email!", Toast.LENGTH_SHORT).show()
                                    currentStep = ForgotStep.OTP
                                } else {
                                    Toast.makeText(context, resp.message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LabelColor, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Gửi mã OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            ForgotStep.OTP -> {
                Text(
                    text = "Nhập mã OTP đã gửi đến",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LabelColor
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                OtpBoxes(
                    otp = otp,
                    onOtpChange = { otp = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otp.length != 6) {
                            Toast.makeText(context, "Vui lòng nhập đủ 6 số OTP!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        keyboardController?.hide()
                        coroutineScope.launch {
                            try {
                                val resp = NetworkClient.api.verifyResetPasswordOtp(
                                    VerifyOtpRequestDto(email = email, otp = otp)
                                )
                                if (resp.code == 1000) {
                                    currentStep = ForgotStep.RESET_PASSWORD
                                } else {
                                    Toast.makeText(context, resp.message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && otp.length == 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LabelColor, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Xác nhận OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Mã OTP có hiệu lực trong 5 phút",
                    style = MaterialTheme.typography.bodySmall,
                    color = LabelColor
                )
            }

            ForgotStep.RESET_PASSWORD -> {
                Text(
                    text = "OTP đã đúng. Bây giờ bạn đặt mật khẩu mới nhé.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LabelColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Mật khẩu mới") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu mới") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (newPassword.length < 6) {
                            Toast.makeText(context, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        keyboardController?.hide()
                        coroutineScope.launch {
                            try {
                                val resp = NetworkClient.api.resetPassword(
                                    ResetPasswordRequestDto(
                                        email = email,
                                        otp = otp,
                                        newPassword = newPassword
                                    )
                                )
                                if (resp.code == 1000) {
                                    Toast.makeText(context, "Đổi mật khẩu thành công! Hãy đăng nhập lại.", Toast.LENGTH_LONG).show()
                                    onSuccess()
                                } else {
                                    Toast.makeText(context, resp.message, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LabelColor, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Đặt lại mật khẩu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OtpBoxes(
    otp: String,
    onOtpChange: (String) -> Unit
) {
    BasicTextField(
        value = otp,
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                onOtpChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(6) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""
                    val isFocused = otp.length == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) LabelColor else Color(0xFFD0D0D0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = TitleColor
                            )
                        )
                    }
                }
            }
        }
    )
}
