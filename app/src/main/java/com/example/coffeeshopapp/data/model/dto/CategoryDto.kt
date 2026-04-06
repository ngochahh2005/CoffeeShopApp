package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("displayOrder") val displayOrder: Int = 0,
    @SerializedName("isActive") val isActive: Boolean = true
)