package com.example.coffeeshopapp.presentation.screen.auth

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.local.AuthDataStore
import com.example.coffeeshopapp.data.model.dto.LoginRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.TokenProvider
import com.example.coffeeshopapp.presentation.components.AuthScreenLogo
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.MainButton
import com.example.coffeeshopapp.presentation.components.PasswordTextField
import com.example.coffeeshopapp.presentation.components.SubButton
import com.example.coffeeshopapp.presentation.components.UsernameTextField
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.rememberScreenInfo
import com.example.coffeeshopapp.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    openHomeScreen: () -> Unit,
    openRegisterScreen: () -> Unit,
    openResetPasswordScreen: () -> Unit,
    onGoogleLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isLogging by remember { mutableStateOf(false) }

    val handleLogin = {
        if (viewModel.username.isEmpty() || viewModel.password.isEmpty()) {
            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
        } else {
            if (!isLogging) {
                isLogging = true
                keyboardController?.hide()

                coroutineScope.launch {
                    try {
                        val resp = NetworkClient.api.login(
                            LoginRequestDto(
                                viewModel.username,
                                viewModel.password
                            )
                        )

                        if (resp.result != null) {
                            val token = resp.result.accessToken
                            val refreshToken = resp.result.refreshToken
                            AuthDataStore.setToken(context, token, refreshToken)
                            TokenProvider.token = token
                            TokenProvider.refreshToken = refreshToken
                            // Fetch current user info to get roles and save them
                            try {
                                val meResp = NetworkClient.api.getMyInfo()
                                val roles = meResp.result?.roles?.mapNotNull { it.name } ?: emptyList()
                                AuthDataStore.setRoles(context, roles)
                                AuthDataStore.setProvider(context, meResp.result?.provider ?: "LOCAL")
                            } catch (_: Exception) {
                                // ignore role fetch error; user will default to USER
                            }
                            Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT)
                                .show()
                            openHomeScreen()
                        } else {
                            Toast.makeText(
                                context,
                                "Đăng nhập thất bại: ${resp.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    } finally {
                        isLogging = false
                    }
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        AuthScreenLogo(modifier = Modifier.fillMaxWidth())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .heightIn(min = (rememberScreenInfo().height * 0.58f))
        ) {
            // Title
            Text(
                text = "Welcome\nBack!",
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

            // Password
            Text(
                text = "Password",
                color = LabelColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )

            PasswordTextField(
                password = viewModel.password,
                onValueChange = {viewModel.onPasswordChange(it)},
                imeAction = ImeAction.Done,
                isShowPassword = viewModel.isShowPassword,
                onShowPasswordChange = { viewModel.onShowPasswordChange() },
                onAction = { handleLogin() },
                modifier = Modifier.fillMaxWidth()
            )

            // Forgot Password - nhỏ gọn ngay dưới ô mật khẩu
            TextButton(
                onClick = { openResetPasswordScreen() },
                modifier = Modifier.align(alignment = Alignment.End)
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    color = PlaceHolderColor,
                    fontSize = 13.sp,
                )
            }

            CommonSpace(16.dp)

            MainButton(
                text = "Đăng nhập",
                onClick = { handleLogin() }
            )

            if (isLogging) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            CommonSpace(16.dp)

            // Divider with "hoặc"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = PlaceHolderColor.copy(alpha = 0.4f))
                Text(
                    text = "  hoặc  ",
                    color = PlaceHolderColor,
                    fontSize = 13.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = PlaceHolderColor.copy(alpha = 0.4f))
            }

            CommonSpace(16.dp)

            // Google Login button
            Button(
                onClick = { onGoogleLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "G",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF4285F4)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng nhập với Google",
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            }

            CommonSpace(16.dp)

            SubButton(
                text = "Tạo tài khoản mới",
                onClick = {
                    openRegisterScreen()
                }
            )

            CommonSpace(18.dp)
        }

    }
}

@Composable
@Preview(name = "Login Screen", showSystemUi = true)
fun LoginScreenPreview() {
    CoffeeShopAppTheme {
        LoginScreen(openRegisterScreen = {}, openHomeScreen = {}, openResetPasswordScreen = {})
    }
}

@Composable
@Preview(name = "Login Screen", showSystemUi = true, device = Devices.PIXEL_9_PRO_FOLD)
fun LoginScreenPreview2() {
    CoffeeShopAppTheme {
        LoginScreen(openRegisterScreen = {}, openHomeScreen = {}, openResetPasswordScreen = {})
    }
}