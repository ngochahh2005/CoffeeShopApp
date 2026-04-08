package com.example.coffeeshopapp.presentation.screen.auth

import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.coffeeshopapp.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.coffeeshopapp.data.remote.LoginRequestDto
import com.example.coffeeshopapp.data.local.AuthDataStore
import com.example.coffeeshopapp.data.TokenProvider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.MainButton
import com.example.coffeeshopapp.presentation.components.PasswordTextField
import com.example.coffeeshopapp.presentation.components.SubButton
import com.example.coffeeshopapp.presentation.components.UsernameTextField
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import org.json.JSONObject

@Composable
fun LoginScreen(
    openHomeScreen: () -> Unit,
    openRegisterScreen: () -> Unit,
    openResetPasswordScreen: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.login_background),
                contentDescription = null,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.FillHeight
            )
            Image(
                painter = painterResource(R.drawable.icon_login),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopCenter)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, top = 350.dp)
        ) {
            Text(
                text = "Welcome\nBack!",
                color = TitleColor,
                style = MaterialTheme.typography.titleLarge
            )

            CommonSpace()

            var username by remember {
                mutableStateOf("")
            }

            Column {
                Text(
                    text = "Email",
                    color = LabelColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                UsernameTextField(
                    username = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CommonSpace()

            Text(
                text = "Password",
                color = LabelColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )

            var password by remember {
                mutableStateOf("")
            }

            PasswordTextField(
                password = password,
                onValueChange = {password = it},
                modifier = Modifier.fillMaxWidth()
            )

            CommonSpace(32.dp)

            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            var isLogging by remember { mutableStateOf(false) }

            MainButton(
                text = "Login",
                onClick = {
                    if (isLogging) return@MainButton
                    isLogging = true
                    coroutineScope.launch {
                        try {
                            val resp = NetworkClient.api.login(LoginRequestDto(username, password))
                            if (resp.result != null) {
                                val token = resp.result.accessToken
                                val responseRoles = resp.result.roles
                                    ?.mapNotNull { role -> role.name ?: role.code }
                                    ?.map { normalizeRole(it) }
                                    ?: emptyList()
                                val jwtRoles = extractRolesFromJwt(token)
                                val roles = (responseRoles + jwtRoles).distinct()
                                // persist token and set provider
                                AuthDataStore.setToken(context, token)
                                AuthDataStore.setRoles(context, roles)
                                TokenProvider.token = token
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                openHomeScreen()
                            } else {
                                Toast.makeText(context, "Login failed: ${resp.message}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Login error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLogging = false
                        }
                    }
                }
            )
            if (isLogging) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            CommonSpace(20.dp)

            SubButton(
                text = "Create an account",
                onClick = {
                        openRegisterScreen()
//                        Toast.makeText(context, "Register successful", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                        openResetPasswordScreen()
//                        Toast.makeText(context, "Forgot Password successful", Toast.LENGTH_SHORT).show()
                },
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
