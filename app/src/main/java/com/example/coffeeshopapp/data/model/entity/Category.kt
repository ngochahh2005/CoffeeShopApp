package com.example.coffeeshopapp.data.model.entity

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Long,
    val name: String,
    @SerializedName("image_url") val imageUrl: String? = null,
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)