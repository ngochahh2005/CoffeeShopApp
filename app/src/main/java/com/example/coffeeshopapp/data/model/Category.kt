package com.example.coffeeshopapp.data.model

import androidx.annotation.DrawableRes

data class Category(
    val id: Int,
    @DrawableRes val icon: Int,
    val name: String
)