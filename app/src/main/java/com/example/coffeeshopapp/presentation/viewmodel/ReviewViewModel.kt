package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.ReviewDataStore
import com.example.coffeeshopapp.data.model.dto.OrderItemDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.ReviewRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.repository.UserRepository
import com.example.coffeeshopapp.utils.getErrorMessage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

data class UserReviewUiState(
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(NetworkClient.api)
    private val _uiState = MutableStateFlow(UserReviewUiState())
    val uiState: StateFlow<UserReviewUiState> = _uiState.asStateFlow()

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products.asStateFlow()

    init {
        fetchAllProducts()
    }

    private fun fetchAllProducts() {
        viewModelScope.launch {
            try {
                val response = NetworkClient.api.getProduct()
                if (response.result != null) {
                    _products.value = response.result
                }
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Fetch products failed", e)
            }
        }
    }

    fun submitMutipleReviews(
        orderId: Long,
        reviewItems: List<OrderItemDto>,
        ratings: Map<Int, Int>,
        comments: Map<Int, String>,
        images: Map<Int, Uri?>
    ) {
        if (ratings.isEmpty()) {
            _uiState.update { it.copy(error = "Không có sản phẩm nào được đánh giá") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }

            try {
                // Ensure we have products for name lookup
                val currentProducts = if (_products.value.isEmpty()) {
                    try {
                        val resp = NetworkClient.api.getProduct()
                        resp.result ?: emptyList()
                    } catch (_: Exception) { emptyList() }
                } else {
                    _products.value
                }
                _products.value = currentProducts

                var lastErrorMessage: String? = null
                supervisorScope {
                    val deferredRequests = ratings.map { (index, rating) ->
                        async {
                            val item = reviewItems.getOrNull(index) ?: return@async null
                            
                            var productId = item.productId
                            if (productId == 0L) {
                                productId = currentProducts.find { 
                                    it.name.trim().equals(item.productName.trim(), ignoreCase = true) 
                                }?.id ?: 0L
                            }

                            if (productId == 0L) {
                                lastErrorMessage = "Không tìm thấy ID cho sản phẩm: ${item.productName}"
                                return@async null
                            }

                            // Create JSON for ReviewRequest
                            val reviewRequest = ReviewRequestDto(
                                orderId = orderId,
                                productId = productId,
                                rating = rating,
                                // Backend @NotBlank requires non-empty string
                                comment = if (comments[index].isNullOrBlank()) "Đánh giá tuyệt vời!" else comments[index]!!
                            )
                            val gson = GsonBuilder().serializeNulls().create()
                            val jsonString = gson.toJson(reviewRequest)
                            
                            // Important: Use application/json as Content-Type for the request part
                            val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                            var imagePart: MultipartBody.Part? = null
                            images[index]?.let { uri ->
                                val file = uriToFile(uri, productId)
                                if (file != null) {
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                                }
                            }

                            try {
                                val resp = userRepository.createReview(requestBody, imagePart)
                                if (resp.result != null) {
                                    resp 
                                } else {
                                    lastErrorMessage = resp.message ?: "Lỗi khi đánh giá ${item.productName}"
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e("ReviewViewModel", "Server error for product $productId: ${e.message}")
                                lastErrorMessage = e.getErrorMessage()
                                null
                            }
                        }
                    }

                    val results = deferredRequests.awaitAll().filterNotNull()

                    if (results.isNotEmpty()) {
                        if (orderId != 0L) {
                            ReviewDataStore.markOrderAsReviewed(getApplication(), orderId)
                        }
                        _uiState.update { it.copy(isSubmitting = false, isSuccess = true, error = null) }
                    } else {
                        _uiState.update { it.copy(isSubmitting = false, error = lastErrorMessage ?: "Gửi đánh giá thất bại") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun uriToFile(uri: Uri, productId: Long): File? {
        val context = getApplication<Application>()
        return try {
            val tempFile = File(context.cacheDir, "temp_review_${productId}_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
