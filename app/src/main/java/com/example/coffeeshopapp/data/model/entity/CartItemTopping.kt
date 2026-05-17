package com.example.coffeeshopapp.data.model.entity

data class CartItemTopping(
    val id: Long,
    val name: String,
    val price: Long,
    val imageUrl: String? = null
)