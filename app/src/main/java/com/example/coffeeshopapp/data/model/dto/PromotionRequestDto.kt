package com.example.coffeeshopapp.data.model.dto

data class PromotionRequestDto(
    val name: String = "",
    val promotionCode: String = "",
    val autoApply: Boolean = false,
    val discountType: String = "PERCENTAGE",
    val discountValue: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val promotionType: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val timeStart: String? = null,
    val timeEnd: String? = null,
    val usageLimitTotal: Int? = null,
    val usageLimitPerUserTotal: Int? = null,
    val usageLimitPerUserPerDay: Int? = null,
    val status: String = "ACTIVE"
)