package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.PromotionDto
import com.example.coffeeshopapp.data.model.dto.PromotionRequestDto
import com.example.coffeeshopapp.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.coffeeshopapp.utils.getErrorMessage

enum class AdminPromotionScreenType { LIST, CREATE, UPDATE, DETAIL }

data class PromotionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val promotions: List<PromotionDto> = emptyList(),
    val selectedPromotion: PromotionDto? = null,
    val showDeleteDialog: Boolean = false,
    val currentScreen: AdminPromotionScreenType = AdminPromotionScreenType.LIST,
    val searchQuery: String = ""
)

class AdminPromotionViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromotionUiState())
    val uiState: StateFlow<PromotionUiState> = _uiState.asStateFlow()

    init { loadPromotions() }

    fun loadPromotions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.getPromotions()
                if (isSuccess(res.code)) {
                    _uiState.update { it.copy(promotions = res.result ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showList() = _uiState.update { it.copy(currentScreen = AdminPromotionScreenType.LIST, selectedPromotion = null, error = null) }
    fun showCreateForm() = _uiState.update { it.copy(currentScreen = AdminPromotionScreenType.CREATE, selectedPromotion = null, error = null) }
    fun showDetail(p: PromotionDto) = _uiState.update { it.copy(currentScreen = AdminPromotionScreenType.DETAIL, selectedPromotion = p) }
    fun showUpdateForm(p: PromotionDto) = _uiState.update { it.copy(currentScreen = AdminPromotionScreenType.UPDATE, selectedPromotion = p) }
    fun onSearchChange(q: String) = _uiState.update { it.copy(searchQuery = q) }

    fun showDeleteDialog(p: PromotionDto) = _uiState.update { it.copy(showDeleteDialog = true, selectedPromotion = p) }
    fun dismissDeleteDialog() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun createPromotion(request: PromotionRequestDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.createPromotion(request)
                if (isSuccess(res.code)) {
                    loadPromotions()
                    _uiState.update { it.copy(currentScreen = AdminPromotionScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun updatePromotion(id: Long, request: PromotionRequestDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.updatePromotion(id, request)
                if (isSuccess(res.code)) {
                    loadPromotions()
                    _uiState.update { it.copy(currentScreen = AdminPromotionScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun confirmDelete() {
        val id = _uiState.value.selectedPromotion?.id ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.deletePromotion(id)
                if (isSuccess(res.code)) loadPromotions()
                else _uiState.update { it.copy(isLoading = false, error = res.message) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    val filteredPromotions: List<PromotionDto>
        get() {
            val q = _uiState.value.searchQuery.lowercase()
            if (q.isBlank()) return _uiState.value.promotions
            return _uiState.value.promotions.filter {
                it.name.lowercase().contains(q) || it.promotionCode.lowercase().contains(q)
            }
        }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
