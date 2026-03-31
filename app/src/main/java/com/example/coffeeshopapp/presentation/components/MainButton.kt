package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor

@Composable
fun MainButton(
    text: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = LabelColor,
            contentColor = BackgroundColor
        ),

        modifier = modifier
            .fillMaxWidth(),

        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}