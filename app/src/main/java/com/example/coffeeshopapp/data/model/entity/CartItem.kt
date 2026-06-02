package com.example.coffeeshopapp.data.model.entity

data class CartItem(
    val lineId: String,
    val productId: String,
    val nameAtAdd: String,
    val priceAtAdd: Long,
    val imageUrlAtAdd: String? = null,
    val quantity: Int = 1,
    val selectedSizeName: String? = null,
    val sizePriceExtra: Long = 0,
    val toppings: List<CartItemTopping> = emptyList(),
    val lastModified: Long = 0L
)
