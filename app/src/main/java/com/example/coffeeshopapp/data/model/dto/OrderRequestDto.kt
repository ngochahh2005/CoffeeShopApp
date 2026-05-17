package com.example.coffeeshopapp.data.model.dto

data class OrderRequestDto(
    val deliveryAddress: String,
    val note: String? = null,
    val promotionCode: String? = null,
    val paymentMethod: PaymentMethodDto,
    val bankCode: String? = null
)
