package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.ReviewDto
import com.example.coffeeshopapp.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.coffeeshopapp.utils.getErrorMessage

data class ReviewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val reviews: List<ReviewDto> = emptyList(),
    val selectedReview: ReviewDto? = null,
    val showDeleteDialog: Boolean = false,
    val showDetailSheet: Boolean = false
)

class AdminReviewViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init { loadReviews() }

    fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.getReviews()
                if (isSuccess(res.code)) {
                    _uiState.update { it.copy(reviews = res.result ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showDetail(review: ReviewDto) = _uiState.update { it.copy(selectedReview = review, showDetailSheet = true) }
    fun dismissDetail() = _uiState.update { it.copy(showDetailSheet = false, selectedReview = null) }

    fun showDeleteDialog(review: ReviewDto) = _uiState.update { it.copy(showDeleteDialog = true, selectedReview = review) }
    fun dismissDeleteDialog() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun confirmDelete() {
        val id = _uiState.value.selectedReview?.id ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.deleteReview(id)
                if (isSuccess(res.code)) loadReviews()
                else _uiState.update { it.copy(isLoading = false, error = res.message) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
