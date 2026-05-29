package com.example.coffeeshopapp.data.model.dto

data class ReviewRequestDto(
    val orderId: Long,
    val productId: Long,
    val rating: Int,
    val comment: String,
    val imageUrl: String? = null
)
