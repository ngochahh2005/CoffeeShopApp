package com.example.coffeeshopapp.data.model.dto

data class ReviewDto(
    val id: Long = 0,
    val userId: Long? = null,
    val username: String? = null,
    val productId: Long? = null,
    val productName: String? = null,
    val rating: Int = 0,
    val comment: String? = null,
    val imageUrl: String? = null,
    val createdAt: String? = null
)
