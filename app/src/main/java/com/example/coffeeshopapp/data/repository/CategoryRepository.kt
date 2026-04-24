package com.example.coffeeshopapp.data.repository

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.CategoryRequestDto
import com.example.coffeeshopapp.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CategoryRepository(private val apiService: ApiService) {

    suspend fun getCategories(): ApiResponseDto<List<CategoryDto>> {
        return apiService.getCategories()
    }

    suspend fun getCategoryById(id: Long): ApiResponseDto<CategoryDto> {
        return apiService.getCategoryById(id)
    }

    suspend fun createCategory(dto: CategoryRequestDto, image: MultipartBody.Part?): ApiResponseDto<CategoryDto> {
        val json = com.google.gson.Gson().toJson(dto)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        return apiService.createCategory(body, image)
    }

    suspend fun updateCategory(id: Long, dto: CategoryRequestDto, image: MultipartBody.Part?): ApiResponseDto<CategoryDto> {
        val json = com.google.gson.Gson().toJson(dto)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        return apiService.updateCategory(id, body, image)
    }

    suspend fun deleteCategory(id: Long): ApiResponseDto<Any> {
        return apiService.deleteCategory(id)
    }
}
