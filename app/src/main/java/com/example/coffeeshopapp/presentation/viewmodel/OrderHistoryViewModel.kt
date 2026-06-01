package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.data.model.dto.PaymentMethodDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.remote.PaymentRequestDto
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderHistoryUiState(
    val isLoading: Boolean = false,
    val orders: List<OrderDto> = emptyList(),
    val error: String? = null
)

class OrderHistoryViewModel : ViewModel() {
    private val userRepository = com.example.coffeeshopapp.data.repository.UserRepository(NetworkClient.api)
    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val resp = userRepository.getMyOrders()
                if (resp.result != null) {
                    _uiState.update { it.copy(isLoading = false, orders = resp.result.sortedByDescending { it.createdAt ?: "" }) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resp.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val resp = userRepository.cancelOrder(orderId)
                if (resp.result != null) {
                    loadOrders() // Refresh list after cancellation
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resp.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun repayOrder(orderId: Long, onVnPayUrl: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val request = PaymentRequestDto(
                    paymentMethod = PaymentMethodDto.VNPAY,
                    bankCode = "NCB"
                )
                val resp = NetworkClient.api.createPayment(orderId, request)
                val payment = resp.result ?: throw IllegalStateException(resp.message ?: "Không thể tạo liên kết thanh toán")
                
                if (!payment.paymentUrl.isNullOrBlank()) {
                    onVnPayUrl(payment.paymentUrl)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Không nhận được URL thanh toán") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
