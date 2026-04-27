package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class ToppingRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("price") val price: Long
)
