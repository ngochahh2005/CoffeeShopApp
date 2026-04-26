package com.example.coffeeshopapp.data.repository

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.ToppingDto
import com.example.coffeeshopapp.data.model.dto.ToppingRequestDto
import com.example.coffeeshopapp.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ToppingRepository(private val apiService: ApiService) {

    suspend fun getToppings(): ApiResponseDto<List<ToppingDto>> = apiService.getToppings()

    suspend fun getToppingById(id: Long): ApiResponseDto<ToppingDto> = apiService.getToppingById(id)

    suspend fun createTopping(dto: ToppingRequestDto, image: MultipartBody.Part?): ApiResponseDto<ToppingDto> {
        val json = com.google.gson.Gson().toJson(dto)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        return apiService.createTopping(body, image)
    }

    suspend fun updateTopping(id: Long, dto: ToppingRequestDto, image: MultipartBody.Part?): ApiResponseDto<ToppingDto> {
        val json = com.google.gson.Gson().toJson(dto)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        return apiService.updateTopping(id, body, image)
    }

    suspend fun deleteTopping(id: Long): ApiResponseDto<Any> = apiService.deleteTopping(id)
}
