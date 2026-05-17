package com.example.coffeeshopapp.data.model.entity

import com.example.coffeeshopapp.data.model.dto.ProductSizeDto
import com.example.coffeeshopapp.data.model.dto.ToppingDto
import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val description: String = "",
    @SerializedName("image_url") val imageUrl: String? = null,
    val rating: Double = 0.0,
    val reviewers: Int = 0,
    var isFavorite: Boolean = false,
    var isTrending: Boolean = false,
    val categoryId: Long = 0,
    val sizes: List<ProductSizeDto> = emptyList(),
    val selectedSizeName: String? = null,
    val selectedSizePriceExtra: Long = 0,
    val selectedToppings: List<ToppingDto> = emptyList(),
) {
    fun getPrice(): String {
        return try {
            val formatter = java.text.DecimalFormat("#,###")
            "${formatter.format(price)}₫"
        } catch (e: Exception) {
            "$price₫"
        }
    }
}
