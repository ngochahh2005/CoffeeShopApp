package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val desc: String?,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("sizes") val size: List<ProductSizeDto>
)