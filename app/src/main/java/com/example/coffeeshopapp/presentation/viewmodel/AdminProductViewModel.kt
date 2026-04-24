package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.ProductRequestDto
import com.example.coffeeshopapp.data.model.dto.ProductSizeRequestDto
import com.example.coffeeshopapp.domain.usecase.CreateProductUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteProductUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoriesUseCase
import com.example.coffeeshopapp.domain.usecase.GetProductByIdUseCase
import com.example.coffeeshopapp.domain.usecase.GetProductsUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateProductUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.example.coffeeshopapp.utils.getErrorMessage

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<ProductDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val error: String? = null,
    val selectedProduct: ProductDto? = null,
    val showDeleteConfirmDialog: Boolean = false,
    val currentScreen: AdminProductScreenType = AdminProductScreenType.LIST
)

enum class AdminProductScreenType {
    LIST, DETAIL, CREATE, UPDATE
}

class AdminProductViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = getCategoriesUseCase()
                if (isSuccess(response.code)) {
                    _uiState.update { it.copy(categories = response.result ?: emptyList()) }
                }
            } catch (e: Exception) {
                // Not critical
            }
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = getProductsUseCase()
                if (isSuccess(response.code)) {
                    _uiState.update {
                        it.copy(products = response.result ?: emptyList(), isLoading = false)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showCreateForm() {
        _uiState.update {
            it.copy(
                currentScreen = AdminProductScreenType.CREATE,
                selectedProduct = null,
                error = null
            )
        }
    }

    fun showList() {
        _uiState.update {
            it.copy(
                currentScreen = AdminProductScreenType.LIST,
                selectedProduct = null,
                error = null
            )
        }
    }
    
    fun showDetail(productId: Long) {
        loadProductById(productId, AdminProductScreenType.DETAIL)
    }

    fun loadProductDetail(id: Long) {
        loadProductById(id, AdminProductScreenType.DETAIL)
    }

    fun loadProductForUpdate(id: Long) {
        loadProductById(id, AdminProductScreenType.UPDATE)
    }

    fun createProduct(
        name: String,
        description: String?,
        basePrice: Long,
        categoryId: Long,
        sizes: List<ProductSizeRequestDto>,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dto = ProductRequestDto(
                    name = name.trim(),
                    description = description,
                    basePrice = basePrice,
                    categoryId = categoryId,
                    sizes = sizes
                )
                val response = createProductUseCase(dto, image)
                if (isSuccess(response.code)) {
                    loadProducts()
                    _uiState.update { it.copy(currentScreen = AdminProductScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun updateProduct(
        id: Long,
        name: String,
        description: String?,
        basePrice: Long,
        categoryId: Long,
        sizes: List<ProductSizeRequestDto>,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dto = ProductRequestDto(
                    name = name.trim(),
                    description = description,
                    basePrice = basePrice,
                    categoryId = categoryId,
                    sizes = sizes
                )
                val response = updateProductUseCase(id, dto, image)
                if (isSuccess(response.code)) {
                    loadProducts()
                    _uiState.update { it.copy(currentScreen = AdminProductScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showDeleteDialog(product: ProductDto) {
        _uiState.update { it.copy(showDeleteConfirmDialog = true, selectedProduct = product) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false, selectedProduct = null) }
    }

    fun confirmDelete() {
        val id = uiState.value.selectedProduct?.id ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = deleteProductUseCase(id)
                if (isSuccess(response.code)) {
                    loadProducts()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    private fun loadProductById(id: Long, nextScreen: AdminProductScreenType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = getProductByIdUseCase(id)
                if (isSuccess(response.code) && response.result != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedProduct = response.result,
                            currentScreen = nextScreen
                        )
                    }
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
