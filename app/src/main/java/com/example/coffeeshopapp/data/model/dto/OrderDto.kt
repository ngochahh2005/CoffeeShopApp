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

data class OrderItemDto(
    val productName: String = "",
    val size: String? = null,
    val unitPrice: BigDecimal = BigDecimal.ZERO,
    val quantity: Int = 0,
    val note: String? = null,
    val toppings: List<OrderItemToppingDto>? = null
)

data class OrderItemToppingDto(
    val toppingName: String = "",
    val price: BigDecimal = BigDecimal.ZERO
)

data class PaymentDto(
    val id: Long? = null,
    val method: String? = null,
    val status: String? = null,
    val amount: BigDecimal? = null,
    val transactionId: String? = null,
    val paymentUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
