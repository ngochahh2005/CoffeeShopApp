package com.example.coffeeshopapp.presentation.screen.auth

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.dto.RegisterRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.presentation.components.AuthScreenLogo
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.MainButton
import com.example.coffeeshopapp.presentation.components.PasswordTextField
import com.example.coffeeshopapp.presentation.components.UsernameTextField
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.viewmodel.AuthViewModel
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    openLoginScreen: () -> Unit,
    openOtpScreen: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var isRegistering by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }

    val handleRegister = {
        if (viewModel.username.isEmpty() || viewModel.password.isEmpty() || viewModel.confirmPassword.isEmpty() || email.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
        } else if (viewModel.password != viewModel.confirmPassword) {
            Toast.makeText(context, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Email không đúng định dạng!", Toast.LENGTH_SHORT).show()
        } else {
            if (!isRegistering) {
                isRegistering = true
                keyboardController?.hide()

                coroutineScope.launch {
                    try {
                        val resp = NetworkClient.api.register(
                            RegisterRequestDto(
                                viewModel.username,
                                viewModel.password,
                                viewModel.confirmPassword,
                                email
                            )
                        )

                        if (resp.result != null) {
                            Toast.makeText(context, "Đăng ký thành công! Vui lòng nhập mã OTP.", Toast.LENGTH_SHORT).show()
                            openOtpScreen(email)
                        } else {
                            Toast.makeText(context, "Lỗi: ${resp.message}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, e.getErrorMessage(), Toast.LENGTH_SHORT).show()
                    } finally {
                        isRegistering = false
                    }
                }
            }
        }
    }

    var scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        AuthScreenLogo()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
        ) {
            Text(
                text = "Nice To\nMeet You!",
                color = TitleColor,
                style = MaterialTheme.typography.titleLarge
            )

            CommonSpace()

            // Username
            Column {
                Text(
                    text = "Username",
                    color = LabelColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                UsernameTextField(
                    username = viewModel.username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    onAction = { focusManager.moveFocus(FocusDirection.Down) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CommonSpace()

            // Email
            Column {
                Text(
                    text = "Email",
                    color = LabelColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                UsernameTextField(
                    username = email,
                    onValueChange = { email = it },
                    onAction = { focusManager.moveFocus(FocusDirection.Down) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CommonSpace()

            // Password
            Text(
                text = "Password",
                color = LabelColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )

            PasswordTextField(
                password = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                imeAction = ImeAction.Next,
                isShowPassword = viewModel.isShowPassword,
                onShowPasswordChange = { viewModel.onShowPasswordChange() },
                onAction = { focusManager.moveFocus(FocusDirection.Down) },
                modifier = Modifier.fillMaxWidth()
            )

            CommonSpace()

            // Confirm password
            Text(
                text = "Confirm password",
                color = LabelColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )

            PasswordTextField(
                password = viewModel.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                imeAction = ImeAction.Done,
                isShowPassword = viewModel.isShowConfirmPassword,
                onShowPasswordChange = { viewModel.onShowConfirmPasswordChange() },
                onAction = { handleRegister() },
                modifier = Modifier.fillMaxWidth()
            )

            CommonSpace(32.dp)

            // Nút register
            MainButton(
                text = "Register",
                onClick = { handleRegister() },
                modifier = Modifier.padding(bottom = 18.dp)
            )
        }
    }

}

@Composable
@Preview(name = "Register Screen", showSystemUi = true)
fun RegisterScreenPreview() {
    CoffeeShopAppTheme {
        RegisterScreen(openLoginScreen = {}, openOtpScreen = {})
    }
}