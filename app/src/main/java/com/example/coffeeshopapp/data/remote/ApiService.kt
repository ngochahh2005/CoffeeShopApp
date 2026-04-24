package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.DashboardOverviewDto
import com.example.coffeeshopapp.data.model.dto.FavoriteStatusDto
import com.example.coffeeshopapp.data.model.dto.LoginRequestDto
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.PromotionDto
import com.example.coffeeshopapp.data.model.dto.PromotionRequestDto
import com.example.coffeeshopapp.data.model.dto.RegisterRequestDto
import com.example.coffeeshopapp.data.model.dto.RevenuePointDto
import com.example.coffeeshopapp.data.model.dto.ReviewDto
import com.example.coffeeshopapp.data.model.dto.RoleAdminDto
import com.example.coffeeshopapp.data.model.dto.TopProductDto
import com.example.coffeeshopapp.data.model.dto.UserCreateRequestDto
import com.example.coffeeshopapp.data.model.dto.UserResponseDto
import com.example.coffeeshopapp.data.model.dto.UserUpdateRequestDto
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
import retrofit2.http.Query


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
    // ─── Products & Categories (existing) ───
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

    @Multipart
    @POST("api/v1/admin/products")
    suspend fun createProduct(
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): ApiResponseDto<ProductDto>

    @Multipart
    @PUT("api/v1/admin/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): ApiResponseDto<ProductDto>

    @DELETE("api/v1/admin/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): ApiResponseDto<Any>

    @GET("api/v1/products/filter")
    suspend fun filterProducts(@Query("categoryId") categoryId: Long): ApiResponseDto<List<ProductDto>>

    @GET("api/v1/products/{id}")
    suspend fun getProductById(@Path("id") id: Long): ApiResponseDto<ProductDto>

    @POST("/api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): ApiResponseDto<AuthResponseDto>

    @GET("api/v1/users/me")
    suspend fun getMyInfo(): ApiResponseDto<UserDto>

    // ─── Dashboard ───
    @GET("api/v1/admin/dashboard/overview")
    suspend fun getDashboardOverview(): ApiResponseDto<DashboardOverviewDto>

    @GET("api/v1/admin/dashboard/revenue")
    suspend fun getRevenue(
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("groupBy") groupBy: String? = null
    ): ApiResponseDto<List<RevenuePointDto>>

    @GET("api/v1/admin/dashboard/top-products")
    suspend fun getTopProducts(): ApiResponseDto<List<TopProductDto>>

    @GET("api/v1/admin/dashboard/recent-orders")
    suspend fun getRecentOrders(): ApiResponseDto<List<OrderDto>>

    @GET("api/v1/admin/dashboard/recent-reviews")
    suspend fun getRecentReviews(): ApiResponseDto<List<ReviewDto>>

    // ─── Admin Users ───
    @GET("api/v1/admin/users")
    suspend fun getAdminUsers(): ApiResponseDto<List<UserResponseDto>>

    @GET("api/v1/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): ApiResponseDto<UserResponseDto>

    @POST("api/v1/admin/users")
    suspend fun createUser(@Body request: UserCreateRequestDto): ApiResponseDto<UserResponseDto>

    @PUT("api/v1/admin/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UserUpdateRequestDto
    ): ApiResponseDto<UserResponseDto>

    @DELETE("api/v1/admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): ApiResponseDto<Any>

    // â”€â”€â”€ Admin Roles â”€â”€â”€
    @GET("api/v1/admin/roles")
    suspend fun getAdminRoles(): ApiResponseDto<List<RoleAdminDto>>

    // ─── Admin Orders ───
    @GET("api/v1/admin/orders")
    suspend fun getAdminOrders(@Query("status") status: String): ApiResponseDto<List<OrderDto>>

    @GET("api/v1/orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Long): ApiResponseDto<OrderDto>

    @PUT("api/v1/admin/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Long,
        @Query("status") status: String
    ): ApiResponseDto<OrderDto>

    // ─── Admin Promotions ───
    @GET("api/v1/promotions")
    suspend fun getAllPromotions(): ApiResponseDto<List<PromotionDto>>

    @GET("api/v1/promotions/{id}")
    suspend fun getPromotionById(@Path("id") id: Long): ApiResponseDto<PromotionDto>

    @POST("api/v1/admin/promotions")
    suspend fun createPromotion(@Body request: PromotionRequestDto): ApiResponseDto<PromotionDto>

    @PUT("api/v1/admin/promotions/{id}")
    suspend fun updatePromotion(
        @Path("id") id: Long,
        @Body request: PromotionRequestDto
    ): ApiResponseDto<PromotionDto>

    @DELETE("api/v1/admin/promotions/{id}")
    suspend fun deletePromotion(@Path("id") id: Long): ApiResponseDto<Any>

    // ─── Admin Reviews ───
    @GET("api/v1/admin/reviews")
    suspend fun getAdminReviews(): ApiResponseDto<List<ReviewDto>>

    @DELETE("api/v1/admin/reviews/{reviewId}")
    suspend fun deleteReview(@Path("reviewId") reviewId: Long): ApiResponseDto<Any>
}
