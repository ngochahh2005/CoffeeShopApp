package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class OrderItemDto(
    @SerializedName(value = "productId", alternate = ["product_id"]) val productId: Long = 0,
    @SerializedName(value = "productName", alternate = ["product_name", "name"]) val productName: String = "",
    val size: String? = null,
    val unitPrice: BigDecimal = BigDecimal.ZERO,
    val quantity: Int = 0,
    val note: String? = null,
    val toppings: List<OrderItemToppingDto>? = null,
    @SerializedName(value = "isReviewed", alternate = ["is_reviewed", "reviewed"])
    val isReviewed: Boolean = false
)
