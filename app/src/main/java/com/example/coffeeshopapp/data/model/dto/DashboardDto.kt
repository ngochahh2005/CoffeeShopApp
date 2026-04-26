package com.example.coffeeshopapp.data.model.dto

import java.math.BigDecimal

data class DashboardOverviewDto(
    val totalUsers: Long = 0,
    val totalProducts: Long = 0,
    val totalOrders: Long = 0,
    val completedOrders: Long = 0,
    val pendingOrders: Long = 0,
    val cancelledOrders: Long = 0,
    val todayRevenue: BigDecimal = BigDecimal.ZERO,
    val monthRevenue: BigDecimal = BigDecimal.ZERO
)

data class RevenuePointDto(
    val label: String = "",
    val revenue: BigDecimal = BigDecimal.ZERO
)

data class TopProductDto(
    val productId: Long = 0,
    val productName: String = "",
    val imageUrl: String? = null,
    val totalSold: Long = 0
)
