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
import com.example.coffeeshopapp.data.model.dto.ToppingDto
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
    val provider: String? = null,
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

    @GET("api/v1/admin/roles")
    suspend fun getAdminRoles(): ApiResponseDto<List<RoleAdminDto>>

    @GET("api/v1/admin/orders")
    suspend fun getAdminOrders(@Query("status") status: String? = null): ApiResponseDto<List<OrderDto>>

    @GET("api/v1/orders/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Long): ApiResponseDto<OrderDto>

    @PUT("api/v1/admin/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Long,
        @Query("status") status: String
    ): ApiResponseDto<OrderDto>

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

    @GET("api/v1/toppings")
    suspend fun getToppings(): ApiResponseDto<List<ToppingDto>>

    @GET("api/v1/toppings/{id}")
    suspend fun getToppingById(@Path("id") id: Long): ApiResponseDto<ToppingDto>

    @Multipart
    @POST("api/v1/admin/toppings")
    suspend fun createTopping(
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): ApiResponseDto<ToppingDto>

    @Multipart
    @PUT("api/v1/admin/toppings/{id}")
    suspend fun updateTopping(
        @Path("id") id: Long,
        @Part("request") request: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): ApiResponseDto<ToppingDto>

    @DELETE("api/v1/admin/toppings/{id}")
    suspend fun deleteTopping(@Path("id") id: Long): ApiResponseDto<Any>

    @GET("api/v1/admin/reviews")
    suspend fun getAdminReviews(): ApiResponseDto<List<ReviewDto>>

    @DELETE("api/v1/admin/reviews/{reviewId}")
    suspend fun deleteReview(@Path("reviewId") reviewId: Long): ApiResponseDto<Any>

    // ─── OTP, Auth & Refresh ───
    @POST("api/v1/auth/verify-email")
    suspend fun verifyEmailOtp(@Body request: VerifyOtpRequestDto): ApiResponseDto<Any>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): ApiResponseDto<Any>

    @POST("api/v1/auth/verify-reset-password-otp")
    suspend fun verifyResetPasswordOtp(@Body request: VerifyOtpRequestDto): ApiResponseDto<Any>

    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): ApiResponseDto<Any>

    @POST("api/v1/auth/google")
    suspend fun googleLogin(@Body request: GoogleAuthRequestDto): ApiResponseDto<AuthResponseDto>

    @POST("api/v1/auth/refresh")
    fun refreshToken(@Body request: RefreshRequestDto): retrofit2.Call<ApiResponseDto<AuthResponseDto>>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: LogoutRequestDto): ApiResponseDto<Any>

    // ─── User Profile ───
    @GET("api/v1/users/me")
    suspend fun getMyInfoFull(): ApiResponseDto<UserResponseDto>

    @Multipart
    @PUT("api/v1/users/me")
    suspend fun updateMyInfo(
        @Part("firstName") firstName: RequestBody? = null,
        @Part("lastName") lastName: RequestBody? = null,
        @Part("phoneNumber") phoneNumber: RequestBody? = null,
        @Part("dob") dob: RequestBody? = null,
        @Part multipartFile: MultipartBody.Part? = null
    ): ApiResponseDto<UserResponseDto>

    @POST("api/v1/users/me/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): ApiResponseDto<Any>
}

data class VerifyOtpRequestDto(
    val email: String,
    val otp: String
)

data class ForgotPasswordRequestDto(
    val email: String
)

data class ResetPasswordRequestDto(
    val email: String,
    val otp: String,
    val newPassword: String
)

data class GoogleAuthRequestDto(
    val idToken: String
)

data class RefreshRequestDto(
    val refreshToken: String
)

data class LogoutRequestDto(
    val token: String
)

data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String
)
