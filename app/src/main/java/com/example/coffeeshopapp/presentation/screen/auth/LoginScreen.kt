package com.example.coffeeshopapp.presentation.screen.auth

import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.coffeeshopapp.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.local.AuthDataStore
import com.example.coffeeshopapp.data.TokenProvider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.dto.LoginRequestDto
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
import org.json.JSONObject
import com.example.coffeeshopapp.presentation.theme.rememberScreenInfo
import com.example.coffeeshopapp.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    openHomeScreen: () -> Unit,
    openRegisterScreen: () -> Unit,
    openResetPasswordScreen: () -> Unit
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
                            AuthDataStore.setToken(context, token)
                            TokenProvider.token = token
                            // Fetch current user info to get roles and save them
                            try {
                                val meResp = NetworkClient.api.getMyInfo()
                                val roles = meResp.result?.roles?.mapNotNull { it.name } ?: emptyList()
                                AuthDataStore.setRoles(context, roles)
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
        AuthScreenLogo(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 30.dp)
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

            CommonSpace(32.dp)

            MainButton(
                text = "Login",
                onClick = { handleLogin() }
            )
            
            if (isLogging) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            CommonSpace(20.dp)

            SubButton(
                text = "Create an account",
                onClick = {
                    openRegisterScreen()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            // Forgot Password
            TextButton(
                onClick = { openResetPasswordScreen() },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Forgot your password?",
                    color = PlaceHolderColor,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }
        }

    }
}

private fun extractRolesFromJwt(token: String): List<String> {
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return emptyList()

        val payload = String(
            Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING),
            Charsets.UTF_8
        )
        val json = JSONObject(payload)
        val roles = json.optJSONArray("roles") ?: return emptyList()

        buildList {
            for (i in 0 until roles.length()) {
                val role = roles.optString(i)
                if (!role.isNullOrBlank()) {
                    add(normalizeRole(role))
                }
            }
        }.distinct()
    } catch (_: Exception) {
        emptyList()
    }
}

private fun normalizeRole(raw: String): String {
    return raw.removePrefix("ROLE_").uppercase()
}

//@Composable
//@Preview(name = "Login Screen", showSystemUi = true)
//fun LoginScreenPreview() {
//    CoffeeShopAppTheme {
//        LoginScreen()
//    }
//}
@Composable
@Preview(name = "Login Screen", showSystemUi = true)
fun LoginScreenPreview() {
    CoffeeShopAppTheme {
        LoginScreen(openRegisterScreen = {}, openHomeScreen = {}, openResetPasswordScreen = {})
    }
}
