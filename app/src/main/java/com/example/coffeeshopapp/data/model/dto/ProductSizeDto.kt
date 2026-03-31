package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class ProductSizeDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sizeName") val sizeName: String,
    @SerializedName("priceExtra") val priceExtra: Double
)