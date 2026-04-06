package com.example.coffeeshopapp.data.repository

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.remote.ApiService

class CategoryRepository(private val apiService: ApiService) {

    suspend fun getCategories(): ApiResponseDto<List<CategoryDto>> {
        return apiService.getCategories()
    }

    suspend fun getCategoryById(id: Long): ApiResponseDto<CategoryDto> {
        return apiService.getCategoryById(id)
    }

    suspend fun createCategory(dto: CategoryDto): ApiResponseDto<CategoryDto> {
        return apiService.createCategory(dto)
    }

    suspend fun updateCategory(id: Long, dto: CategoryDto): ApiResponseDto<CategoryDto> {
        return apiService.updateCategory(id, dto)
    }

    suspend fun deleteCategory(id: Long): ApiResponseDto<Any> {
        return apiService.deleteCategory(id)
    }
}
