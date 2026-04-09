package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.FavoriteStatusDto
import com.example.coffeeshopapp.data.model.dto.LoginRequestDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.RegisterRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


data class RoleDto(
    val name: String? = null,
    val code: String? = null
)

data class AuthResponseDto(
    val accessToken: String,
    val accessTokenExp: Long,
    val refreshToken: String?,
    val refreshTokenExp: Long?,
    val roles: List<RoleDto>? = null
)

data class UserDto(
    val id: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val roles: List<RoleDto>? = null
)

interface ApiService {
    @GET("api/v1/products")
    suspend fun getProduct(): ApiResponseDto<List<ProductDto>>

    @GET("api/v1/categories")
    suspend fun getCategories(): ApiResponseDto<List<CategoryDto>>

    @POST("api/v1/favorites/{productId}/toggle")
    suspend fun toggleFavorite(@Path("productId") productId: Long): ApiResponseDto<FavoriteStatusDto>

    @GET("api/v1/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): ApiResponseDto<CategoryDto>

    @Multipart
    @POST("api/v1/admin/categories")
    suspend fun createCategory(
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): ApiResponseDto<CategoryDto>

    @Multipart
    @PUT("api/v1/admin/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Long,
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): ApiResponseDto<CategoryDto>

    @DELETE("api/v1/admin/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): ApiResponseDto<Any>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): ApiResponseDto<AuthResponseDto>

    @GET("api/v1/users/me")
    suspend fun getMyInfo(): ApiResponseDto<UserDto>
}
