package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.domain.usecase.CreateCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoriesUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoryByIdUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDto> = emptyList(),
    val error: String? = null,
    val selectedCategory: CategoryDto? = null,
    val showDeleteConfirmDialog: Boolean = false,
    val currentScreen: AdminCategoryScreenType = AdminCategoryScreenType.LIST
)

enum class AdminCategoryScreenType {
    LIST, DETAIL, CREATE, UPDATE
}

class AdminCategoryViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = getCategoriesUseCase()
                if (isSuccess(response.code)) {
                    _uiState.update { 
                        it.copy(categories = response.result ?: emptyList(), isLoading = false)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun showCreateForm() {
        _uiState.update {
            it.copy(
                currentScreen = AdminCategoryScreenType.CREATE,
                selectedCategory = null,
                error = null
            )
        }
    }

    fun showList() {
        _uiState.update {
            it.copy(
                currentScreen = AdminCategoryScreenType.LIST,
                selectedCategory = null,
                error = null
            )
        }
    }

    fun loadCategoryDetail(id: Long) {
        loadCategoryById(id, AdminCategoryScreenType.DETAIL)
    }

    fun loadCategoryForUpdate(id: Long) {
        loadCategoryById(id, AdminCategoryScreenType.UPDATE)
    }

    fun createCategory(
        name: String,
        displayOrder: Int,
        image: MultipartBody.Part?
    ) {
         viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dto = CategoryDto(
                    name = name.trim(),
                    imageUrl = null,
                    description = null,
                    displayOrder = displayOrder,
                    isActive = true
                )
                val response = createCategoryUseCase(dto, image)
                if (isSuccess(response.code)) {
                    loadCategories()
                    _uiState.update { it.copy(currentScreen = AdminCategoryScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
         }
    }

    fun updateCategory(
        id: Long,
        name: String,
        displayOrder: Int,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val old = uiState.value.selectedCategory
                val dto = CategoryDto(
                    id = id,
                    name = name.trim(),
                    imageUrl = old?.imageUrl,
                    description = old?.description,
                    displayOrder = displayOrder,
                    isActive = old?.isActive ?: true
                )
                val response = updateCategoryUseCase(id, dto, image)
                if (isSuccess(response.code)) {
                    loadCategories()
                    _uiState.update { it.copy(currentScreen = AdminCategoryScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
         }
    }

    fun showDeleteDialog(category: CategoryDto) {
        _uiState.update { it.copy(showDeleteConfirmDialog = true, selectedCategory = category) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false, selectedCategory = null) }
    }

    fun confirmDelete() {
        val id = uiState.value.selectedCategory?.id ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = deleteCategoryUseCase(id)
                 if (isSuccess(response.code)) {
                    loadCategories()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadCategoryById(id: Long, nextScreen: AdminCategoryScreenType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = getCategoryByIdUseCase(id)
                if (isSuccess(response.code) && response.result != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedCategory = response.result,
                            currentScreen = nextScreen
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
