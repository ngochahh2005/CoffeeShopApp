package com.example.coffeeshopapp.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScreenInfo(
    val width: Dp,
    val height: Dp,
    val logoHeight: Dp
)

@Composable
fun rememberScreenInfo(): ScreenInfo {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    return ScreenInfo(
        width = screenWidth,
        height = screenHeight,
        logoHeight = screenHeight * 0.4f
    )
}