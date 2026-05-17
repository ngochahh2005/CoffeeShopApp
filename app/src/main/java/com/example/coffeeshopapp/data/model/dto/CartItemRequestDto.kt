package com.example.coffeeshopapp.data.model.dto

data class CartItemRequestDto(
    val productId: Long,
    val size: SizeTypeDto,
    val quantity: Int,
    val note: String? = null,
    val toppingIds: List<Long>? = null
)
