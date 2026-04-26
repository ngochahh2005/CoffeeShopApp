package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.ToppingDto
import com.example.coffeeshopapp.data.model.dto.ToppingRequestDto
import com.example.coffeeshopapp.domain.usecase.CreateToppingUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteToppingUseCase
import com.example.coffeeshopapp.domain.usecase.GetToppingByIdUseCase
import com.example.coffeeshopapp.domain.usecase.GetToppingsUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateToppingUseCase
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

data class ToppingUiState(
    val isLoading: Boolean = false,
    val toppings: List<ToppingDto> = emptyList(),
    val error: String? = null,
    val selectedTopping: ToppingDto? = null,
    val showDeleteConfirmDialog: Boolean = false,
    val currentScreen: AdminToppingScreenType = AdminToppingScreenType.LIST
)

enum class AdminToppingScreenType {
    LIST, DETAIL, CREATE, UPDATE
}

class AdminToppingViewModel(
    private val getToppingsUseCase: GetToppingsUseCase,
    private val getToppingByIdUseCase: GetToppingByIdUseCase,
    private val createToppingUseCase: CreateToppingUseCase,
    private val updateToppingUseCase: UpdateToppingUseCase,
    private val deleteToppingUseCase: DeleteToppingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToppingUiState())
    val uiState: StateFlow<ToppingUiState> = _uiState.asStateFlow()

    init {
        loadToppings()
    }

    fun loadToppings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = getToppingsUseCase()
                if (isSuccess(response.code)) {
                    _uiState.update { it.copy(toppings = response.result ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showCreateForm() {
        _uiState.update { it.copy(currentScreen = AdminToppingScreenType.CREATE, selectedTopping = null, error = null) }
    }

    fun showList() {
        _uiState.update { it.copy(currentScreen = AdminToppingScreenType.LIST, selectedTopping = null, error = null) }
    }

    fun loadToppingDetail(id: Long) = loadToppingById(id, AdminToppingScreenType.DETAIL)

    fun loadToppingForUpdate(id: Long) = loadToppingById(id, AdminToppingScreenType.UPDATE)

    fun createTopping(name: String, price: Long, image: MultipartBody.Part?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = createToppingUseCase(ToppingRequestDto(name = name.trim(), price = price), image)
                if (isSuccess(response.code)) {
                    loadToppings()
                    _uiState.update { it.copy(currentScreen = AdminToppingScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun updateTopping(id: Long, name: String, price: Long, image: MultipartBody.Part?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val old = uiState.value.selectedTopping
                val response = updateToppingUseCase(
                    id,
                    ToppingRequestDto(name = name.trim(), imageUrl = old?.imageUrl, price = price),
                    image
                )
                if (isSuccess(response.code)) {
                    loadToppings()
                    _uiState.update { it.copy(currentScreen = AdminToppingScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showDeleteDialog(topping: ToppingDto) {
        _uiState.update { it.copy(showDeleteConfirmDialog = true, selectedTopping = topping) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false, selectedTopping = null) }
    }

    fun confirmDelete() {
        val id = uiState.value.selectedTopping?.id ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = deleteToppingUseCase(id)
                if (isSuccess(response.code)) {
                    loadToppings()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun loadToppingById(id: Long, nextScreen: AdminToppingScreenType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = getToppingByIdUseCase(id)
                if (isSuccess(response.code) && response.result != null) {
                    _uiState.update { it.copy(isLoading = false, selectedTopping = response.result, currentScreen = nextScreen) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
