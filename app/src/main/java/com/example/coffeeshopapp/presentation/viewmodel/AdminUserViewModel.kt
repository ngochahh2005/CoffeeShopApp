package com.example.coffeeshopapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshopapp.data.model.dto.UserCreateRequestDto
import com.example.coffeeshopapp.data.model.dto.UserResponseDto
import com.example.coffeeshopapp.data.model.dto.UserUpdateRequestDto
import com.example.coffeeshopapp.data.model.dto.RoleAdminDto
import com.example.coffeeshopapp.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.coffeeshopapp.utils.getErrorMessage

enum class AdminUserScreenType { LIST, CREATE, UPDATE, DETAIL }

data class UserUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val users: List<UserResponseDto> = emptyList(),
    val roles: List<RoleAdminDto> = emptyList(),
    val selectedUser: UserResponseDto? = null,
    val showDeleteDialog: Boolean = false,
    val currentScreen: AdminUserScreenType = AdminUserScreenType.LIST,
    val searchQuery: String = ""
)

class AdminUserViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    init {
        loadRoles()
        loadUsers()
    }

    private fun loadRoles() {
        viewModelScope.launch {
            try {
                val res = repository.getRoles()
                if (isSuccess(res.code)) {
                    _uiState.update { it.copy(roles = res.result ?: emptyList()) }
                }
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.getUsers()
                if (isSuccess(res.code)) {
                    _uiState.update { it.copy(users = res.result ?: emptyList(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun showList() = _uiState.update { it.copy(currentScreen = AdminUserScreenType.LIST, selectedUser = null, error = null) }
    fun showCreateForm() = _uiState.update { it.copy(currentScreen = AdminUserScreenType.CREATE, selectedUser = null, error = null) }
    fun onSearchChange(q: String) = _uiState.update { it.copy(searchQuery = q) }

    fun showDetail(user: UserResponseDto) = _uiState.update { it.copy(currentScreen = AdminUserScreenType.DETAIL, selectedUser = user) }
    fun showUpdateForm(user: UserResponseDto) = _uiState.update { it.copy(currentScreen = AdminUserScreenType.UPDATE, selectedUser = user) }

    fun showDeleteDialog(user: UserResponseDto) = _uiState.update { it.copy(showDeleteDialog = true, selectedUser = user) }
    fun dismissDeleteDialog() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun createUser(request: UserCreateRequestDto, roleIds: List<Long>? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.createUser(request)
                if (isSuccess(res.code)) {
                    val createdUserId = res.result?.id
                    if (!roleIds.isNullOrEmpty() && createdUserId != null) {
                        val updateRes = repository.updateUser(
                            createdUserId,
                            UserUpdateRequestDto(roleIds = roleIds)
                        )
                        if (!isSuccess(updateRes.code)) {
                            _uiState.update { it.copy(isLoading = false, error = updateRes.message) }
                            return@launch
                        }
                    }

                    loadUsers()
                    _uiState.update { it.copy(currentScreen = AdminUserScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun updateUser(id: Long, request: UserUpdateRequestDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.updateUser(id, request)
                if (isSuccess(res.code)) {
                    loadUsers()
                    _uiState.update { it.copy(currentScreen = AdminUserScreenType.LIST) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = res.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    fun confirmDelete() {
        val id = _uiState.value.selectedUser?.id ?: return
        viewModelScope.launch {
            dismissDeleteDialog()
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = repository.deleteUser(id)
                if (isSuccess(res.code)) loadUsers()
                else _uiState.update { it.copy(isLoading = false, error = res.message) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.getErrorMessage()) }
            }
        }
    }

    val filteredUsers: List<UserResponseDto>
        get() {
            val q = _uiState.value.searchQuery.lowercase()
            if (q.isBlank()) return _uiState.value.users
            return _uiState.value.users.filter {
                (it.username.lowercase().contains(q)) ||
                (it.email?.lowercase()?.contains(q) == true) ||
                (it.fullName?.lowercase()?.contains(q) == true)
            }
        }

    private fun isSuccess(code: Int): Boolean = code == 200 || code == 1000 || code == 0
}
