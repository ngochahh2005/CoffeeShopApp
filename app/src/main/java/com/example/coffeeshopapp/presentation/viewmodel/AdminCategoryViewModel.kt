package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.domain.usecase.CreateCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoriesUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                // Depending on generic success code, adjusting typical logic.
                if (response.code == 200 || response.code == 1000 || response.code == 0) { 
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

    fun navigateTo(screenType: AdminCategoryScreenType, category: CategoryDto? = null) {
         _uiState.update { it.copy(currentScreen = screenType, selectedCategory = category) }
    }

    fun createCategory(name: String, description: String?) {
         viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dto = CategoryDto(name = name, description = description, imageUrl = null) 
                val response = createCategoryUseCase(dto)
                if (response.code == 200 || response.code == 1000 || response.code == 0) {
                    loadCategories()
                    navigateTo(AdminCategoryScreenType.LIST)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
         }
    }

    fun updateCategory(id: Long, name: String, description: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val dto = CategoryDto(id = id, name = name, description = description, imageUrl = null) 
                val response = updateCategoryUseCase(id, dto)
                if (response.code == 200 || response.code == 1000 || response.code == 0) {
                    loadCategories()
                    navigateTo(AdminCategoryScreenType.LIST)
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
                 if (response.code == 200 || response.code == 1000 || response.code == 0) {
                    loadCategories()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
