package com.example.coffeeshopapp.data.model.entity

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val totalQuantity: Int
        get() = items.sumOf { it.quantity }

    val totalAmount: Long
        get() = items.sumOf { it.price * it.quantity }
}
