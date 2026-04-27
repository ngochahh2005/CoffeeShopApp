package com.example.coffeeshopapp.data.model.dto

data class ProductSizeRequestDto(
    val sizeName: String, // "S", "M", "L"
    val priceExtra: Long
)