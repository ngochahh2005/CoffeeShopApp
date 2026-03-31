package com.example.coffeeshopapp.presentation.screen.auth

import android.widget.Toast
import androidx.compose.runtime.getValue
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.coffeeshopapp.presentation.components.UsernameTextField
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.TitleColor

@Composable
fun RegisterScreen(
    openLoginScreen: () -> Unit
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
                .padding(start = 30.dp, end = 30.dp, top = 325.dp)
        ) {
            Text(
                text = "Nice To\nMeet You!",
                color = TitleColor,
                style = MaterialTheme.typography.titleLarge
            )

            CommonSpace()

            var username by remember {
                mutableStateOf("")
            }
//
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

            CommonSpace()

            Text(
                text = "Confirm password",
                color = LabelColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )

            var passwordConfirm by remember {
                mutableStateOf("")
            }

            PasswordTextField(
                password = passwordConfirm,
                onValueChange = {passwordConfirm = it},
                modifier = Modifier.fillMaxWidth()
            )

            CommonSpace(32.dp)

            val context = LocalContext.current

            Spacer(modifier = Modifier.weight(1f))
            MainButton(
                text = "Register",
                onClick = {
                    openLoginScreen()
                    Toast.makeText(context, "Register Successful!", Toast.LENGTH_SHORT).show()
                }
            )

            CommonSpace(20.dp)
        }
    }

}

//@Composable
//@Preview(name = "Register Screen", showSystemUi = true)
//fun RegisterScreenPreview() {
//    CoffeeShopAppTheme {
//        RegisterScreen()
//    }
//}