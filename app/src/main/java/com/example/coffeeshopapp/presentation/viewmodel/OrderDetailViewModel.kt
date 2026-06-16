package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.ReviewDataStore
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

data class OrderDetailUiState(
    val isLoading: Boolean = false,
    val order: OrderDto? = null,
    val error: String? = null,
    val isLocalReviewed: Boolean = false
)

class OrderDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()
    private var statusJob: Job? = null

    fun loadOrderDetail(orderId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val resp = NetworkClient.api.getOrderById(orderId)
                if (resp.result != null) {
                    _uiState.update { it.copy(isLoading = false, order = resp.result) }
                    checkAllProductsReviewedStatus(resp.result)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = resp.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun checkAllProductsReviewedStatus(order: OrderDto) {
        viewModelScope.launch {
            try {
                val allProductsResp = try {
                    NetworkClient.api.getProduct()
                } catch (_: Exception) { null }
                val allProducts = allProductsResp?.result ?: emptyList()

                supervisorScope {
                    val orderId = order.id
                    if (orderId == 0L) return@supervisorScope
                    
                    val items = order.orderItems ?: emptyList()
                    if (items.isEmpty()) {
                        _uiState.update { it.copy(isLocalReviewed = false) }
                        return@supervisorScope
                    }

                    val reviewStatusList = items.map { item ->
                        var productId = item.productId
                        if (productId == 0L) {
                            productId = allProducts.find {
                                it.name.trim().equals(item.productName.trim(), ignoreCase = true)
                            }?.id ?: 0L
                        }

                        if (productId == 0L) return@map false

                        try {
                            val checkResp = NetworkClient.api.checkOrderProductReviewed(orderId, productId)
                            checkResp.result ?: false
                        } catch (e: Exception) {
                            Log.e("OrderDetailViewModel", "Error checking review for order $orderId product $productId", e)
                            false
                        }
                    }

                    val allReviewed = reviewStatusList.isNotEmpty() && reviewStatusList.all { it }
                    _uiState.update { it.copy(isLocalReviewed = allReviewed) }
                }
            } catch (e: Exception) {
                Log.e("OrderDetailViewModel", "Error checking review status", e)
                _uiState.update { it.copy(isLocalReviewed = false) }
            }
        }
    }
}
