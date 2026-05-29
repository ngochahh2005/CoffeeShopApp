package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class PromotionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String = "",
    @SerializedName(value = "promotionCode", alternate = ["promotion_code", "code"]) val promotionCode: String = "",
    @SerializedName(value = "autoApply", alternate = ["auto_apply"]) val autoApply: Boolean = false,
    @SerializedName(value = "discountType", alternate = ["discount_type"]) val discountType: String = "",
    @SerializedName(value = "discountValue", alternate = ["discount_value"]) val discountValue: Double = 0.0,
    @SerializedName(value = "minOrderValue", alternate = ["min_order_value", "minimumOrderValue", "minimum_order_value"]) val minOrderValue: Double = 0.0,
    @SerializedName(value = "promotionType", alternate = ["promotion_type"]) val promotionType: String? = null,
    @SerializedName(value = "startDate", alternate = ["start_date"]) val startDate: String? = null,
    @SerializedName(value = "endDate", alternate = ["end_date"]) val endDate: String? = null,
    @SerializedName(value = "timeStart", alternate = ["time_start"]) val timeStart: String? = null,
    @SerializedName(value = "timeEnd", alternate = ["time_end"]) val timeEnd: String? = null,
    @SerializedName(value = "usageLimitTotal", alternate = ["usage_limit_total"]) val usageLimitTotal: Int? = null,
    @SerializedName(value = "usageLimitPerUserTotal", alternate = ["usage_limit_per_user_total"]) val usageLimitPerUserTotal: Int? = null,
    @SerializedName(value = "usageLimitPerUserPerDay", alternate = ["usage_limit_per_user_per_day"]) val usageLimitPerUserPerDay: Int? = null,
    @SerializedName("status") val status: String = "ACTIVE",
    @SerializedName(value = "createdAt", alternate = ["created_at"]) val createdAt: String? = null,
    @SerializedName(value = "updatedAt", alternate = ["updated_at"]) val updatedAt: String? = null
) {
    fun requiredOrderAmount(): Long {
        val rawValue = minOrderValue.toLong()
        return when {
            rawValue <= 0L -> 100_000L
            rawValue in 1L..999L -> rawValue * 1000L
            else -> rawValue
        }
    }
}


