package com.example.coffeeshopapp.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.R

val lightlime = FontFamily(
    Font(R.font.limelight_regular, FontWeight.Normal)
)

val k2d = FontFamily(
    Font(R.font.k2d_regular, FontWeight.Normal),
    Font(R.font.k2d_bold, FontWeight.Bold)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = lightlime,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 36.sp,
        letterSpacing = 1.sp
    ),

    titleMedium = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
    ),

    titleSmall = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
    ),

    bodySmall = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    ),

    labelLarge = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),

    labelMedium = TextStyle(
        fontFamily = k2d,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
)