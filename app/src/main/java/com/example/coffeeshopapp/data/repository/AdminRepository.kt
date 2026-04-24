package com.example.coffeeshopapp.data.repository

import com.example.coffeeshopapp.data.model.dto.*
import com.example.coffeeshopapp.data.remote.ApiService

class AdminRepository(private val api: ApiService) {

    // ─── Dashboard ───
    suspend fun getOverview() = api.getDashboardOverview()
    suspend fun getRevenue(from: String?, to: String?, groupBy: String?) = api.getRevenue(from, to, groupBy)
    suspend fun getTopProducts() = api.getTopProducts()
    suspend fun getRecentOrders() = api.getRecentOrders()
    suspend fun getRecentReviews() = api.getRecentReviews()

    // ─── Users ───
    suspend fun getUsers() = api.getAdminUsers()
    suspend fun getUserById(id: Long) = api.getUserById(id)
    suspend fun createUser(request: UserCreateRequestDto) = api.createUser(request)
    suspend fun updateUser(id: Long, request: UserUpdateRequestDto) = api.updateUser(id, request)
    suspend fun deleteUser(id: Long) = api.deleteUser(id)
    suspend fun getRoles() = api.getAdminRoles()

    // ─── Orders ───
    suspend fun getOrders(status: String) = api.getAdminOrders(status)
    suspend fun getOrderById(id: Long) = api.getOrderById(id)
    suspend fun updateOrderStatus(orderId: Long, status: String) = api.updateOrderStatus(orderId, status)

    // ─── Promotions ───
    suspend fun getPromotions() = api.getAllPromotions()
    suspend fun getPromotionById(id: Long) = api.getPromotionById(id)
    suspend fun createPromotion(request: PromotionRequestDto) = api.createPromotion(request)
    suspend fun updatePromotion(id: Long, request: PromotionRequestDto) = api.updatePromotion(id, request)
    suspend fun deletePromotion(id: Long) = api.deletePromotion(id)

    // ─── Reviews ───
    suspend fun getReviews() = api.getAdminReviews()
    suspend fun deleteReview(id: Long) = api.deleteReview(id)
}
