package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class ToppingDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("price") val price: Double,
    @SerializedName(value = "isActive", alternate = ["active", "is_active"]) val isActive: Boolean? = null
)

