package com.example.coffeeshopapp.data.model.dto

import java.math.BigDecimal

data class OrderItemDto(
    val productName: String = "",
    val size: String? = null,
    val unitPrice: BigDecimal = BigDecimal.ZERO,
    val quantity: Int = 0,
    val note: String? = null,
    val toppings: List<OrderItemToppingDto>? = null
)