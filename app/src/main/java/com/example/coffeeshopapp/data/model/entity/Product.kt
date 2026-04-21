package com.example.coffeeshopapp.data.model.entity

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val description: String = "",
    @SerializedName("image_url") val imageUrl: String? = null,
    val rating: Double = 0.0,
    val reviewers: Int = 0,
    var isFavorite: Boolean = false
) {
    fun getPrice(): String {
        return try {
            val formatter = java.text.DecimalFormat("#,###")
            "${formatter.format(price)} ₫"
        } catch (e: Exception) {
            "$price ₫"
        }
    }
}