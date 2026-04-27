package com.example.coffeeshopapp.data.model.dto

import java.math.BigDecimal

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