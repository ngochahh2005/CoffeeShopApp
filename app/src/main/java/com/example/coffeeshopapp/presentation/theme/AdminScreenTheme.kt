package com.example.coffeeshopapp.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun AdminScreenTheme(content: @Composable () -> Unit) {
    val base = MaterialTheme.typography
    val adminTypography = base.copy(
        titleLarge = base.titleLarge.copy(
            fontFamily = base.bodyLarge.fontFamily ?: FontFamily.Default,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = base.titleMedium.copy(
            fontFamily = base.bodyLarge.fontFamily ?: FontFamily.Default,
            fontSize = 18.sp,
            lineHeight = 22.sp
        ),
        titleSmall = base.titleSmall.copy(
            fontFamily = base.bodyLarge.fontFamily ?: FontFamily.Default,
            fontSize = 16.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = base.bodyLarge.copy(fontSize = 16.sp, lineHeight = 20.sp),
        bodyMedium = base.bodyMedium.copy(fontSize = 14.sp, lineHeight = 18.sp),
        bodySmall = base.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
        labelLarge = base.labelLarge.copy(fontSize = 14.sp, lineHeight = 18.sp)
    )

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = adminTypography,
        content = content
    )
}
