package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.FavoriteStatusDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/products")
    suspend fun getProduct(): ApiResponseDto<List<ProductDto>>

    @POST("api/v1/favorites/{productId}/toggle")
    suspend fun toggleFavorite(@Path("productId") productId: Long): ApiResponseDto<FavoriteStatusDto>

    // Category Endpoints
    @GET("api/categories")
    suspend fun getCategories(): ApiResponseDto<List<CategoryDto>>

    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): ApiResponseDto<CategoryDto>

    @POST("api/categories")
    suspend fun createCategory(@retrofit2.http.Body dto: CategoryDto): ApiResponseDto<CategoryDto>

    @retrofit2.http.PUT("api/categories/{id}")
    suspend fun updateCategory(@Path("id") id: Long, @retrofit2.http.Body dto: CategoryDto): ApiResponseDto<CategoryDto>

    @retrofit2.http.DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): ApiResponseDto<Any>
}