package com.example.coffeeshopapp.data.model.dto

import java.math.BigDecimal

data class OrderItemToppingDto(
    val toppingName: String = "",
    val price: BigDecimal = BigDecimal.ZERO
)