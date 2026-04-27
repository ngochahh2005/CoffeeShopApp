package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.CartDataStore
import com.example.coffeeshopapp.data.model.entity.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val isLoading: Boolean = true,
    val items: List<CartItem> = emptyList()
) {
    val totalQuantity: Int
        get() = items.sumOf { it.quantity }

    val totalAmount: Long
        get() = items.sumOf { it.price * it.quantity }
}

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            CartDataStore.cartItemsFlow(getApplication()).collect { items ->
                _uiState.update { it.copy(isLoading = false, items = items) }
            }
        }
    }

    fun increaseQuantity(productId: String) {
        val current = _uiState.value.items.find { it.productId == productId } ?: return
        updateQuantity(productId, current.quantity + 1)
    }

    fun decreaseQuantity(productId: String) {
        val current = _uiState.value.items.find { it.productId == productId } ?: return
        updateQuantity(productId, current.quantity - 1)
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            CartDataStore.removeProduct(getApplication(), productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            CartDataStore.clear(getApplication())
        }
    }

    private fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            CartDataStore.updateQuantity(getApplication(), productId, quantity)
        }
    }
}
