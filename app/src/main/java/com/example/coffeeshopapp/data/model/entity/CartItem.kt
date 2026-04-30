package com.example.coffeeshopapp.data.model.entity

data class CartItem(
    val productId: String,
    val nameAtAdd: String,
    val priceAtAdd: Long,
    val imageUrlAtAdd: String? = null,
    val quantity: Int = 1
)
