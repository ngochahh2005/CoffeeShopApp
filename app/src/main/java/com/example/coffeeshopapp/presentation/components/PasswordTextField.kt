package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TextColor
import com.example.coffeeshopapp.presentation.viewmodel.AuthViewModel

@Composable
fun PasswordTextField(
    password: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction,
    isShowPassword: Boolean,
    onShowPasswordChange: () -> Unit,
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier
){
    TextField(
        value = password,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,

        placeholder = {
            Text(
                text = "* * * * * * * *",
                color = PlaceHolderColor
            )
        },

        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = LabelColor)
        },

        trailingIcon = {
            IconButton(
                onClick = onShowPasswordChange,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = LabelColor
                )
            ) {
                if (isShowPassword) Icon(Icons.Filled.Visibility, contentDescription = null)
                else Icon(Icons.Filled.VisibilityOff, contentDescription = null)
            }
        },

        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundColor,
            unfocusedContainerColor = BackgroundColor,
            disabledContainerColor = BackgroundColor,

            focusedIndicatorColor = LabelColor,
            unfocusedIndicatorColor = LabelColor,
            disabledIndicatorColor = LabelColor,

            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor,
            disabledTextColor = TextColor,

            focusedPlaceholderColor = PlaceHolderColor,
            unfocusedPlaceholderColor = PlaceHolderColor,
            disabledPlaceholderColor = PlaceHolderColor,
        ),

        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),

        keyboardActions = KeyboardActions(
            onDone = { onAction() },
            onNext = { onAction() }
        ),
    )
}