package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.CartDataStore
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.repository.ProductRepository
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val totalItemsInCart: Int = 0,
    val selectedCount: Int = 0,
    val totalAmount: Long = 0,
    val selectedIds: Set<String> = emptySet(),
    val error: String? = null
)

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepository = ProductRepository(NetworkClient.api)
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    var selectedProduct by mutableStateOf<Product?>(null)
        private set
    var isShowSheet by mutableStateOf(false)
        private set

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            CartDataStore.cartItemsFlow(getApplication()).collect { items ->
                _uiState.update { currentState ->
                    val cleanedSelectedIds = currentState.selectedIds.intersect(items.map { it.lineId }.toSet())
                    val newState = currentState.copy(
                        isLoading = false,
                        items = items,
                        totalItemsInCart = items.size,
                        selectedIds = cleanedSelectedIds
                    )
                    calculateTotal(newState)
                }
            }
        }
    }

    fun toggleSelection(lineId: String) {
        _uiState.update { currentState ->
            val newSelectedIds = currentState.selectedIds.toMutableSet()
            if (newSelectedIds.contains(lineId)) newSelectedIds.remove(lineId) else newSelectedIds.add(lineId)
            calculateTotal(currentState.copy(selectedIds = newSelectedIds))
        }
    }

    fun toggleAllSelection(selected: Boolean) {
        _uiState.update { currentState ->
            val newSelectedIds = if (selected) {
                currentState.items.map { it.lineId }.toSet()
            } else {
                emptySet()
            }
            calculateTotal(currentState.copy(selectedIds = newSelectedIds))
        }
    }

    fun removeSelectedItems() {
        val selectedIds = _uiState.value.selectedIds
        if (selectedIds.isEmpty()) return
        
        viewModelScope.launch {
            CartDataStore.removeProducts(getApplication(), selectedIds)
            _uiState.update { it.copy(selectedIds = emptySet()) }
        }
    }

    fun increaseQuantity(lineId: String) {
        val current = _uiState.value.items.find { it.lineId == lineId } ?: return
        updateQuantity(lineId, current.quantity + 1)
    }

    fun decreaseQuantity(lineId: String) {
        val current = _uiState.value.items.find { it.lineId == lineId } ?: return
        updateQuantity(lineId, current.quantity - 1)
    }

    fun removeItem(lineId: String) {
        viewModelScope.launch {
            CartDataStore.removeProduct(getApplication(), lineId)
        }
    }

    fun showProductDetail(productId: String) {
        viewModelScope.launch {
            val id = productId.toLongOrNull()
            if (id == null) {
                _uiState.update { it.copy(error = "Product id không hợp lệ") }
                return@launch
            }

            try {
                val resp = productRepository.getProductById(id)
                val dto = resp.result
                if (dto != null) {
                    selectedProduct = dto.toProduct()
                    isShowSheet = true
                } else {
                    _uiState.update { it.copy(error = resp.message ?: "Không tải được chi tiết sản phẩm") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            }
        }
    }

    fun onDismiss() {
        selectedProduct = null
        isShowSheet = false
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            CartDataStore.addProduct(getApplication(), product, quantity)
        }
    }

    fun updateQuantity(lineId: String, quantity: Int) {
        if (quantity < 1) {
            removeItem(lineId)
            return
        }
        viewModelScope.launch {
            CartDataStore.updateQuantity(getApplication(), lineId, quantity)
        }
    }

    private fun calculateTotal(state: CartUiState): CartUiState {
        val selectedItems = state.items.filter { it.lineId in state.selectedIds }
        return state.copy(
            selectedCount = selectedItems.sumOf { it.quantity },
            totalAmount = selectedItems.sumOf { it.priceAtAdd * it.quantity }
        )
    }
}

private fun ProductDto.toProduct(): Product {
    return Product(
        id = id.toString(),
        name = name,
        price = basePrice.toLong(),
        description = description.orEmpty(),
        imageUrl = imageUrl,
        rating = 0.0,
        reviewers = 0,
        categoryId = categoryId,
        sizes = size
    )
}
