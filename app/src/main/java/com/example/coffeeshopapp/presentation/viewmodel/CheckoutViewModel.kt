package com.example.coffeeshopapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.local.CartDataStore
import com.example.coffeeshopapp.data.model.dto.CartItemRequestDto
import com.example.coffeeshopapp.data.model.dto.OrderRequestDto
import com.example.coffeeshopapp.data.model.dto.PaymentMethodDto
import com.example.coffeeshopapp.data.model.dto.SizeTypeDto
import com.example.coffeeshopapp.data.model.dto.PromotionDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.presentation.utils.OrderSession
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val isLoading: Boolean = true,
    val items: List<com.example.coffeeshopapp.data.model.entity.CartItem> = emptyList(),
    val totalAmount: Long = 0L,
    val deliveryAddress: String = "",
    val note: String = "",
    val paymentMethod: PaymentMethodDto = PaymentMethodDto.CASH,
    val availablePromotions: List<PromotionDto> = emptyList(),
    val selectedPromotion: PromotionDto? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null
)

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        observeCart()
        loadPromotions()
    }

    private fun loadPromotions() {
        viewModelScope.launch {
            try {
                val response = NetworkClient.api.getAllPromotions()
                if (response.result != null) {
                    _uiState.update { it.copy(availablePromotions = response.result.filter { it.status == "ACTIVE" }) }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun selectPromotion(promotion: PromotionDto?) {
        _uiState.update { it.copy(selectedPromotion = promotion) }
    }

    private fun observeCart() {
        viewModelScope.launch {
            CartDataStore.cartItemsFlow(getApplication()).collect { items ->
                val selectedIds = OrderSession.selectedLineIds.toSet()
                val selectedItems = items.filter { it.lineId in selectedIds }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = selectedItems,
                        totalAmount = selectedItems.sumOf { it.priceAtAdd * it.quantity }
                    )
                }
            }
        }
    }

    fun setDeliveryAddress(value: String) {
        _uiState.update { it.copy(deliveryAddress = value) }
    }

    fun setNote(value: String) {
        _uiState.update { it.copy(note = value) }
    }

    fun setPaymentMethod(value: PaymentMethodDto) {
        _uiState.update { it.copy(paymentMethod = value) }
    }

    fun submitOrder(
        onVnPayUrl: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            try {
                val state = _uiState.value
                if (state.deliveryAddress.isBlank()) {
                    throw IllegalArgumentException("Vui lòng nhập địa chỉ giao hàng")
                }
                if (state.items.isEmpty()) {
                    throw IllegalStateException("Không có sản phẩm nào để thanh toán")
                }

                NetworkClient.api.clearCart()

                state.items.forEach { item ->
                    val productId = item.productId.toLongOrNull()
                        ?: throw IllegalArgumentException("ID sản phẩm không hợp lệ")
                    val size = item.selectedSizeName?.takeIf { it.isNotBlank() }
                        ?.let {
                            try {
                                SizeTypeDto.valueOf(it)
                            } catch (ignored: IllegalArgumentException) {
                                SizeTypeDto.S
                            }
                        } ?: SizeTypeDto.S

                    val request = CartItemRequestDto(
                        productId = productId,
                        size = size,
                        quantity = item.quantity,
                        note = null,
                        toppingIds = item.toppings.map { it.id }.takeIf { it.isNotEmpty() }
                    )
                    NetworkClient.api.addToCartItem(request)
                }

                val orderRequest = OrderRequestDto(
                    deliveryAddress = state.deliveryAddress,
                    note = state.note.ifBlank { null },
                    promotionCode = state.selectedPromotion?.promotionCode,
                    paymentMethod = state.paymentMethod,
                    bankCode = if (state.paymentMethod == PaymentMethodDto.VNPAY) "NCB" else null
                )

                val resp = NetworkClient.api.createOrderFromCart(orderRequest)
                val order = resp.result ?: throw IllegalStateException(resp.message ?: "Không thể tạo đơn hàng")

                if (state.paymentMethod == PaymentMethodDto.VNPAY && !order.payment?.paymentUrl.isNullOrBlank()) {
                    onVnPayUrl(order.payment!!.paymentUrl!!)
                } else {
                    onSuccess()
                }

                // Chỉ xóa các item đã được chọn mua khỏi local CartDataStore
                val selectedIds = OrderSession.selectedLineIds
                selectedIds.forEach { lineId ->
                    CartDataStore.removeProduct(getApplication(), lineId)
                }
                OrderSession.selectedLineIds = emptyList()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.getErrorMessage()) }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }
}
