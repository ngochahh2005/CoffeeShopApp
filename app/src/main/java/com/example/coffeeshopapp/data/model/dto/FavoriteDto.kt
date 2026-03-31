package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class FavoriteDto(
    @SerializedName("id") val id: Long,
    @SerializedName("productId") val productId: Long,
    @SerializedName("productName") val productName: String,
    @SerializedName("imageURL") val imageURL: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("productAvailable") val productAvailable: Boolean,
    @SerializedName("createdAt") val createdAt: String
)