package com.example.coffeeshopapp.data.model.dto

data class ProductRequestDto(
    val name: String,
    val description: String?,
    val basePrice: Double,
    val categoryId: Long,
    val sizes: List<ProductSizeRequestDto>
)

data class ProductSizeRequestDto(
    val sizeName: String, // "S", "M", "L"
    val priceExtra: Double
)
