package com.example.coffeeshopapp.data.model.dto

import java.math.BigDecimal

data class OrderDto(
    val id: Long = 0,
    val user: UserResponseDto? = null,
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    val orderSubTotal: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val promotion: PromotionDto? = null,
    val deliveryAddress: String? = null,
    val status: String = "",
    val note: String? = null,
    val createdAt: String? = null,
    val payment: PaymentDto? = null,
    val orderItems: List<OrderItemDto>? = null
)
