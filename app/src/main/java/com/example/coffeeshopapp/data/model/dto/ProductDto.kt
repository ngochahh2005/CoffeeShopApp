package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val desc: String?,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName(value = "isActive", alternate = ["active", "is_active"]) val isActive: Boolean?,
    @SerializedName(value = "isDeleted", alternate = ["deleted", "is_deleted"]) val isDeleted: Boolean? = null,
    @SerializedName("sizes") val size: List<ProductSizeDto>
)
