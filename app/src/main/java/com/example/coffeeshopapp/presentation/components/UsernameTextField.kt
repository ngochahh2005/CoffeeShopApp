package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TextColor

@Composable
fun UsernameTextField(
    username: String,
    onValueChange: (String) -> Unit,
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    TextField(
        value = username,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = true,

        placeholder = {
            Text(
                text = "example123@gmail.com",
                style = MaterialTheme.typography.bodyMedium
            )
        },

        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = null, tint = LabelColor)
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

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),

        keyboardActions = KeyboardActions(
            onNext = { onAction() }
        ),

        singleLine = true
    )
}