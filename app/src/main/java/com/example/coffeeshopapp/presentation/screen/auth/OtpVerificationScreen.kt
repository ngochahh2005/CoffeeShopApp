package com.example.coffeeshopapp.presentation.screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.remote.VerifyOtpRequestDto
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    email: String,
    onVerified: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TitleColor)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Icon
        Text("✉️", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Xác thực Email",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TitleColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Chúng tôi đã gửi mã 6 số đến",
            style = MaterialTheme.typography.bodyMedium,
            color = LabelColor
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = TitleColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OTP Input - 6 boxes
        BasicTextField(
            value = otpValue,
            onValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                    otpValue = newValue
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.focusRequester(focusRequester),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(6) { index ->
                        val char = otpValue.getOrNull(index)?.toString() ?: ""
                        val isFocused = otpValue.length == index

                        Box(
                            modifier = Modifier
                                .size(52.dp)
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
                                    fontSize = 24.sp,
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

        Spacer(modifier = Modifier.height(32.dp))

        // Verify button
        Button(
            onClick = {
                if (otpValue.length != 6) {
                    Toast.makeText(context, "Vui lòng nhập đủ 6 số!", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isLoading = true
                keyboardController?.hide()
                coroutineScope.launch {
                    try {
                        val resp = NetworkClient.api.verifyEmailOtp(
                            VerifyOtpRequestDto(email = email, otp = otpValue)
                        )
                        if (resp.code == 1000) {
                            Toast.makeText(context, "Xác thực thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show()
                            onVerified()
                        } else {
                            Toast.makeText(context, "Lỗi: ${resp.message}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && otpValue.length == 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LabelColor, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Xác thực", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Mã có hiệu lực trong 5 phút",
            style = MaterialTheme.typography.bodySmall,
            color = LabelColor
        )
    }
}
