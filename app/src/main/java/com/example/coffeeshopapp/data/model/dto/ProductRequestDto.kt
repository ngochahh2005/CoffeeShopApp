package com.example.coffeeshopapp.data.model.dto

data class ProductRequestDto(
    val name: String,
    val description: String?,
    val basePrice: Long,
    val categoryId: Long,
    val sizes: List<ProductSizeRequestDto>
)


