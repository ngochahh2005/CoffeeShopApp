package com.example.coffeeshopapp.data.model.dto

data class PromotionDto(
    val id: Long? = null,
    val name: String = "",
    val promotionCode: String = "",
    val autoApply: Boolean = false,
    val discountType: String = "",
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
    val status: String = "ACTIVE",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

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
