package com.example.coffeeshopapp.presentation.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.model.dto.RoleAdminDto
import com.example.coffeeshopapp.data.model.dto.UserCreateRequestDto
import com.example.coffeeshopapp.data.model.dto.UserResponseDto
import com.example.coffeeshopapp.data.model.dto.UserUpdateRequestDto
import com.example.coffeeshopapp.presentation.viewmodel.AdminUserScreenType
import com.example.coffeeshopapp.presentation.viewmodel.AdminUserViewModel

private val Brown = Color(0xFF553722)
private val Blue = Color(0xFF007AFF)
private val Green = Color(0xFF34C759)
private val Red = Color(0xFFFF3B30)
private val Bg = Color(0xFFF7F8FA)
private val Card = Color(0xFFFFFFFF)
private val Sub = Color(0xFF8E8E93)

@Composable
fun UserManagementScreen(viewModel: AdminUserViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    if (state.showDeleteDialog && state.selectedUser != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text("Xác nhận xoá", fontWeight = FontWeight.SemiBold) },
            text = { Text("Bạn có chắc muốn xoá người dùng \"${state.selectedUser!!.username}\" không?") },
            confirmButton = { TextButton(onClick = viewModel::confirmDelete) { Text("Xoá", color = Red) } },
            dismissButton = { TextButton(onClick = viewModel::dismissDeleteDialog) { Text("Huỷ") } }
        )
    }

    when (state.currentScreen) {
        AdminUserScreenType.LIST -> UserListScreen(state, viewModel, onBackClick)
        AdminUserScreenType.CREATE -> UserFormScreen(
            isUpdate = false,
            user = null,
            roles = state.roles,
            isLoading = state.isLoading,
            errorMessage = state.error,
            onCreate = { req, roleIds -> viewModel.createUser(req, roleIds) },
            onUpdate = { },
            onBack = viewModel::showList
        )
        AdminUserScreenType.UPDATE -> UserFormScreen(
            isUpdate = true,
            user = state.selectedUser,
            roles = state.roles,
            isLoading = state.isLoading,
            errorMessage = state.error,
            onCreate = { _, _ -> },
            onUpdate = { req -> state.selectedUser?.id?.let { viewModel.updateUser(it, req) } },
            onBack = viewModel::showList
        )
        AdminUserScreenType.DETAIL -> UserDetailPopup(user = state.selectedUser, onDismiss = viewModel::showList)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserListScreen(state: com.example.coffeeshopapp.presentation.viewmodel.UserUiState, viewModel: AdminUserViewModel, onBack: () -> Unit) {
    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Quản lý người dùng", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Brown) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showCreateForm, containerColor = Brown) {
                Icon(Icons.Default.PersonAdd, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Tìm theo tên, email...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brown) }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Lỗi: ${state.error}", color = Red) }
                else -> {
                    val users = viewModel.filteredUsers
                    if (users.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Chưa có người dùng nào.", color = Sub) }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(users) { user -> UserCard(user, onDetail = { viewModel.showDetail(user) }, onEdit = { viewModel.showUpdateForm(user) }, onDelete = { viewModel.showDeleteDialog(user) }) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(user: UserResponseDto, onDetail: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val roleLabel = user.roles?.firstOrNull()?.name ?: "USER"
    val roleColor = if (roleLabel == "ADMIN") Color(0xFF6A1B9A) else Blue
    Surface(shape = RoundedCornerShape(16.dp), color = Card, shadowElevation = 2.dp) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(44.dp), shape = CircleShape, color = Brown.copy(0.12f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(user.username.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Brown)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.username, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Brown)
                    Spacer(Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(6.dp), color = roleColor.copy(0.12f)) {
                        Text(roleLabel, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, color = roleColor, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(user.email ?: "", fontSize = 12.sp, color = Sub, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}", fontSize = 12.sp, color = Color(0xFF555555))
            }
            IconButton(onClick = onDetail) { Icon(Icons.Default.Visibility, null, tint = Blue, modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Green, modifier = Modifier.size(20.dp)) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Red, modifier = Modifier.size(20.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserFormScreen(
    isUpdate: Boolean,
    user: UserResponseDto?,
    roles: List<RoleAdminDto>,
    isLoading: Boolean,
    errorMessage: String?,
    onCreate: (UserCreateRequestDto, List<Long>?) -> Unit,
    onUpdate: (UserUpdateRequestDto) -> Unit,
    onBack: () -> Unit
) {
    var lastError by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank() && errorMessage != lastError) {
            lastError = errorMessage
            showError = true
        }
    }

    var username by rememberSaveable { mutableStateOf(user?.username ?: "") }
    var email by rememberSaveable { mutableStateOf(user?.email ?: "") }
    var firstName by rememberSaveable { mutableStateOf(user?.firstName ?: "") }
    var lastName by rememberSaveable { mutableStateOf(user?.lastName ?: "") }
    var phone by rememberSaveable { mutableStateOf(user?.phoneNumber ?: "") }

    val effectiveRoles = remember(roles) {
        val filtered = roles.filter { it.name == "USER" || it.name == "ADMIN" }
        if (filtered.isNotEmpty()) filtered else listOf(RoleAdminDto(id = 0, name = "USER"), RoleAdminDto(id = 0, name = "ADMIN"))
    }
    var roleExpanded by remember { mutableStateOf(false) }
    var selectedRoleName by rememberSaveable {
        mutableStateOf(
            if (isUpdate) user?.roles?.firstOrNull { it.name != null }?.name ?: "USER" else "USER"
        )
    }
    val selectedRole = effectiveRoles.firstOrNull { it.name == selectedRoleName } ?: effectiveRoles.first()
    val selectedRoleIds: List<Long>? = selectedRole.id.takeIf { it > 0 }?.let { listOf(it) }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text(if (isUpdate) "Cập nhật người dùng" else "Tạo người dùng mới", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Brown) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (!isUpdate) {
                OutlinedTextField(value = username, onValueChange = { username = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tên đăng nhập") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            } else {
                OutlinedTextField(value = username, onValueChange = {}, modifier = Modifier.fillMaxWidth(), label = { Text("Tên đăng nhập") }, enabled = false, shape = RoundedCornerShape(12.dp))
            }
            OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Email") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, modifier = Modifier.weight(1f), label = { Text("Họ") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, modifier = Modifier.weight(1f), label = { Text("Tên") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            }
            OutlinedTextField(value = phone, onValueChange = { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Số điện thoại") }, singleLine = true, shape = RoundedCornerShape(12.dp))

            ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = it }) {
                OutlinedTextField(
                    value = selectedRole.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                    effectiveRoles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = {
                                selectedRoleName = role.name
                                roleExpanded = false
                            }
                        )
                    }
                }
            }

            if (isLoading) { CircularProgressIndicator(color = Brown, modifier = Modifier.align(Alignment.CenterHorizontally)) }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Huỷ bỏ") }
                Button(
                    onClick = {
                        val normalizedPhone = phone.trim()
                        if (normalizedPhone.isNotEmpty() && !Regex("^0[0-9]{9,10}$").matches(normalizedPhone)) {
                            lastError = "Số điện thoại không đúng định dạng"
                            showError = true
                            return@Button
                        }

                        if (!isUpdate && email.trim().isBlank()) {
                            lastError = "Email không được để trống"
                            showError = true
                            return@Button
                        }

                        if (isUpdate) {
                            onUpdate(
                                UserUpdateRequestDto(
                                    password = null,
                                    firstName = firstName.ifBlank { null },
                                    lastName = lastName.ifBlank { null },
                                    phoneNumber = normalizedPhone.ifBlank { null },
                                    roleIds = selectedRoleIds
                                )
                            )
                        } else {
                            val roleIdsToApply = if (selectedRole.name == "ADMIN") selectedRoleIds else null
                            onCreate(
                                UserCreateRequestDto(
                                    username = username.trim(),
                                    password = "DefaultPassword123!",
                                    email = email.trim(),
                                    firstName = firstName.ifBlank { null },
                                    lastName = lastName.ifBlank { null },
                                    phoneNumber = normalizedPhone.ifBlank { null }
                                ),
                                roleIdsToApply
                            )
                        }
                    },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brown),
                    enabled = !isLoading && (if (isUpdate) true else username.isNotBlank())
                ) { Text(if (isUpdate) "Lưu cập nhật" else "Tạo mới") }
            }
        }
    }
}

@Composable
private fun UserDetailPopup(user: UserResponseDto?, onDismiss: () -> Unit) {
    if (user == null) { onDismiss(); return }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chi tiết người dùng", fontWeight = FontWeight.Bold, color = Brown) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailLine("Tên đăng nhập", user.username)
                DetailLine("Email", user.email ?: "—")
                DetailLine("Họ tên", user.fullName ?: "${user.firstName ?: ""} ${user.lastName ?: ""}")
                DetailLine("SĐT", user.phoneNumber ?: "—")
                DetailLine("Ngày sinh", user.dob ?: "—")
                DetailLine("Vai trò", user.roles?.joinToString { it.name ?: "" } ?: "USER")
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Đóng", color = Brown) } }
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, color = Sub)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
