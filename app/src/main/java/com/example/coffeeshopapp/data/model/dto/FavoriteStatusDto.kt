package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class FavoriteStatusDto(
    @SerializedName("productId") val productId: Long,
    @SerializedName("favorited") val favorited: Boolean
)