package com.example.coffeeshopapp.data.model.entity

data class CartItem(
    val productId: String,
    val name: String,
    val price: Long,
    val imageUrl: String? = null,
    val quantity: Int = 1
)
