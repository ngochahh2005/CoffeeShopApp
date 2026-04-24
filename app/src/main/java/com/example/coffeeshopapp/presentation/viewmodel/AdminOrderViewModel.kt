package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.coffeeshopapp.utils.getErrorMessage

data class OrderUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orders: List<OrderDto> = emptyList(),
    val selectedOrder: OrderDto? = null,
    val showDetailSheet: Boolean = false,
    val currentTab: String = "PENDING"
)

class AdminOrderViewModel(
    private val repository: AdminRepository,
    initialTab: String = "PENDING"
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState(currentTab = initialTab))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init { loadOrders(initialTab) }

    fun switchTab(status: String) {
        _uiState.update { it.copy(currentTab = status) }
        loadOrders(status)
    }

    fun loadOrders(status: String? = null) {
        val targetStatus = status ?: _uiState.value.currentTab
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.getOrders(targetStatus)
                if (isSuccess(res.code)) {
                    _uiState.update { it.copy(orders = res.result ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showDetail(order: OrderDto) = _uiState.update { it.copy(selectedOrder = order, showDetailSheet = true) }
    fun dismissDetail() = _uiState.update { it.copy(showDetailSheet = false, selectedOrder = null) }

    fun updateOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.updateOrderStatus(orderId, newStatus)
                if (isSuccess(res.code)) {
                    dismissDetail()
                    loadOrders()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
