package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("displayOrder") val displayOrder: Int = 0,
    @SerializedName(value = "isActive", alternate = ["active", "is_active"]) val isActive: Boolean? = null
)

data class CategoryRequestDto(
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("displayOrder") val displayOrder: Int = 0,
    @SerializedName("isActive") val isActive: Boolean = true
)
