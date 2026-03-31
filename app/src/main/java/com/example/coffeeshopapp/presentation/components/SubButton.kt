package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor

@Composable
fun SubButton(
    text: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = BackgroundColor,
            contentColor = LabelColor
        ),
        border = BorderStroke(2.dp, LabelColor),

        modifier = modifier
            .fillMaxWidth(),

        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}