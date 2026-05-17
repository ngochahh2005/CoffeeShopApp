package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.ReviewRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.repository.UserRepository
import com.example.coffeeshopapp.utils.getErrorMessage
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun submitReview(
        productId: Long,
        rating: Int,
        comment: String,
        imageUri: Uri? = null
    ) {
        if (productId <= 0) {
            _uiState.update { it.copy(error = "ID sản phẩm không hợp lệ") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            try {
                // Tạo ReviewRequestDto object
                val reviewRequest = ReviewRequestDto(
                    productId = productId,
                    rating = rating,
                    comment = comment
                )

                // Serialize thành JSON RequestBody
                val gson = Gson()
                val jsonString = gson.toJson(reviewRequest)
                val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                // Tạo image part (nếu có)
                var imagePart: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = uriToFile(uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }
                }

                // Gọi API
                val resp = userRepository.createReview(requestBody, imagePart)
                if (resp.result != null) {
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isSubmitting = false, error = resp.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSubmitting = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver
        val tempFile = File(context.cacheDir, "temp_review_image_${System.currentTimeMillis()}.jpg")
        return try {
            contentResolver.openInputStream(uri)?.use { input ->
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
