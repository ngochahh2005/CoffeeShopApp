package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class OrderItemToppingDto(
    @SerializedName("toppingName")
    val toppingName: String = "",
    @SerializedName("toppingPrice")
    val price: BigDecimal = BigDecimal.ZERO
)
