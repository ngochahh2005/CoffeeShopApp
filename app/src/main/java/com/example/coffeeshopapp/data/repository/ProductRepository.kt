package com.example.coffeeshopapp.data.repository

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.remote.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductRepository(private val apiService: ApiService) {

    suspend fun getProducts(): ApiResponseDto<List<ProductDto>> {
        return apiService.getProduct()
    }

    suspend fun getProductById(id: Long): ApiResponseDto<ProductDto> {
        return apiService.getProductById(id)
    }

    suspend fun createProduct(
        request: RequestBody,
        image: MultipartBody.Part?
    ): ApiResponseDto<ProductDto> {
        return apiService.createProduct(request, image)
    }

    suspend fun updateProduct(
        id: Long,
        request: RequestBody,
        image: MultipartBody.Part?
    ): ApiResponseDto<ProductDto> {
        return apiService.updateProduct(id, request, image)
    }

    suspend fun deleteProduct(id: Long): ApiResponseDto<Any> {
        return apiService.deleteProduct(id)
    }

    suspend fun getProductsByCategory(categoryId: Long): ApiResponseDto<List<ProductDto>> {
        return apiService.filterProducts(categoryId)
    }
}
