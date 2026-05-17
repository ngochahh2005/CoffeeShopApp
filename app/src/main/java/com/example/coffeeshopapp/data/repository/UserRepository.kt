package com.example.coffeeshopapp.data.repository

import com.example.coffeeshopapp.data.model.dto.*
import com.example.coffeeshopapp.data.remote.ApiService
import com.example.coffeeshopapp.data.remote.ChangePasswordRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val api: ApiService) {
    suspend fun getMyInfoFull() = api.getMyInfoFull()
    
    suspend fun updateMyInfo(
        firstName: RequestBody?,
        lastName: RequestBody?,
        phoneNumber: RequestBody?,
        dob: RequestBody?,
        image: MultipartBody.Part?
    ) = api.updateMyInfo(firstName, lastName, phoneNumber, dob, image)

    suspend fun changePassword(request: ChangePasswordRequestDto) = api.changePassword(request)

    suspend fun getMyOrders() = api.getMyOrders()

    suspend fun cancelOrder(orderId: Long) = api.cancelOrder(orderId)

    suspend fun createReview(
        request: RequestBody,
        image: MultipartBody.Part?
    ) = api.createReview(request, image)
}
