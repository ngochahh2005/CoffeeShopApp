package com.example.coffeeshopapp.presentation.screen.admin.category

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.viewmodel.AdminCategoryScreenType
import com.example.coffeeshopapp.presentation.viewmodel.AdminCategoryViewModel
import com.example.coffeeshopapp.presentation.viewmodel.CategoryUiState
import com.example.coffeeshopapp.utils.isActiveResolved
import com.example.coffeeshopapp.utils.toFullImageUrl
import com.example.coffeeshopapp.utils.uriToImagePart
import okhttp3.MultipartBody

@Composable
fun AdminCategoryScreen(
    viewModel: AdminCategoryViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProductClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteConfirmDialog && uiState.selectedCategory != null) {
        DeleteConfirmationDialog(
            category = uiState.selectedCategory!!,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog
        )
    }

    when (uiState.currentScreen) {
        AdminCategoryScreenType.LIST -> {
            CategoryListScreen(
                uiState = uiState,
                onAddClick = viewModel::showCreateForm,
                onDetailClick = { viewModel.loadCategoryDetail(it.id) },
                onEditClick = { viewModel.loadCategoryForUpdate(it.id) },
                onDeleteClick = viewModel::showDeleteDialog,
                onBackClick = onBackClick
            )
        }

        AdminCategoryScreenType.DETAIL -> {
            CategoryDetailScreen(
                category = uiState.selectedCategory,
                onBack = viewModel::showList,
                onEdit = {
                    uiState.selectedCategory?.id?.let { viewModel.loadCategoryForUpdate(it) }
                }
            )
        }

        AdminCategoryScreenType.CREATE -> {
            CategoryFormScreen(
                isUpdating = false,
                initialCategory = null,
                isLoading = uiState.isLoading,
                errorMessage = uiState.error,
                onSubmit = { name, description, displayOrder, image ->
                    viewModel.createCategory(name, description, displayOrder, image)
                },
                onBack = viewModel::showList
            )
        }

        AdminCategoryScreenType.UPDATE -> {
            CategoryFormScreen(
                isUpdating = true,
                initialCategory = uiState.selectedCategory,
                isLoading = uiState.isLoading,
                errorMessage = uiState.error,
                onSubmit = { name, description, displayOrder, image ->
                    val id = uiState.selectedCategory?.id ?: return@CategoryFormScreen
                    viewModel.updateCategory(id, name, description, displayOrder, image)
                },
                onBack = viewModel::showList
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryListScreen(
    uiState: CategoryUiState,
    onAddClick: () -> Unit,
    onDetailClick: (CategoryDto) -> Unit,
    onEditClick: (CategoryDto) -> Unit,
    onDeleteClick: (CategoryDto) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý danh mục",
                        style = MaterialTheme.typography.titleMedium,
                        color = TitleColor,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add category")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Lỗi: ${uiState.error}", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
            }

            uiState.categories.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Chưa có danh mục nào.", fontSize = 14.sp)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.categories) { category ->
                        CategoryCard(
                            category = category,
                            onDetailClick = { onDetailClick(category) },
                            onEditClick = { onEditClick(category) },
                            onDeleteClick = { onDeleteClick(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryDto,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryImage(
                imageUrl = category.imageUrl,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Thứ tự hiển thị: ${category.displayOrder}",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = if (category.isActiveResolved()) "Đang hiển thị" else "Đã ẩn",
                    fontSize = 13.sp,
                    color = if (category.isActiveResolved()) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
                )
            }

            Row {
                IconButton(onClick = onDetailClick) {
                    Icon(Icons.Default.Visibility, contentDescription = "Detail")
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDetailScreen(
    category: CategoryDto?,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết danh mục",
                        style = MaterialTheme.typography.titleMedium,
                        color = TitleColor,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TitleColor)
                    }
                }
            )
        }
    ) { padding ->
        if (category == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy danh mục.", fontSize = 14.sp)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CategoryImage(
                imageUrl = category.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            DetailRow("Tên", category.name)
            DetailRow("Thứ tự hiển thị", category.displayOrder.toString())
            DetailRow("Trạng thái", if (category.isActiveResolved()) "Đang hiển thị" else "Đã ẩn")
            DetailRow("Mô tả", category.description ?: "Không có")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(text = label, color = Color(0xFF757575), fontSize = 12.sp)
        Text(text = value, fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFormScreen(
    isUpdating: Boolean = false,
    initialCategory: CategoryDto?,
    isLoading: Boolean = false,
    errorMessage: String?,
    onSubmit: (String, String, Int, MultipartBody.Part?) -> Unit = {_, _, _, _ -> },
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var lastError by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var name by rememberSaveable(initialCategory?.id) { mutableStateOf(initialCategory?.name.orEmpty()) }
    var description by rememberSaveable(initialCategory?.id) { mutableStateOf(initialCategory?.description.orEmpty()) }
    var displayOrderText by rememberSaveable(initialCategory?.id) {
        mutableStateOf(if (initialCategory != null) initialCategory.displayOrder.toString() else "0")
    }
    var selectedImageUri by rememberSaveable(initialCategory?.id) { mutableStateOf<Uri?>(null) }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank() && errorMessage != lastError) {
            lastError = errorMessage
            showError = true
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    val title = if (isUpdating) "Cập nhật danh mục" else "Tạo danh mục"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TitleColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (showError && !lastError.isNullOrBlank()) {
            AlertDialog(
                onDismissRequest = { showError = false },
                title = { Text("Có lỗi xảy ra") },
                text = { Text(lastError ?: "") },
                confirmButton = { TextButton(onClick = { showError = false }) { Text("OK") } }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tên danh mục", fontSize = 14.sp) },
                singleLine = true
            )

            OutlinedTextField(
                value = displayOrderText,
                onValueChange = { input ->
                    if (input.isEmpty() || input.all { it.isDigit() }) {
                        displayOrderText = input
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Thứ tự hiển thị", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mô tả", fontSize = 14.sp) },
                minLines = 3
            )

            Text(
                text = "Ảnh danh mục",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { imagePicker.launch("image/*") },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFBDBDBD)),
                color = Color(0xFFF8F8F8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val previewImage = selectedImageUri ?: initialCategory?.imageUrl?.let { Uri.parse(it) }
                    if (previewImage != null) {
                        CategoryImage(
                            imageUrl = previewImage.toString(),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Color(0xFF757575)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Nhấn để chọn ảnh", color = Color(0xFF757575), fontSize = 14.sp)
                        }
                    }
                }
            }

            if (selectedImageUri != null) {
                Text(
                    text = "Đã chọn ảnh mới",
                    color = Color(0xFF2E7D32),
                    fontSize = 12.sp
                )
            }

            if (isLoading) {
                CircularProgressIndicator()
            }

            Button(
                onClick = {
                    val displayOrder = displayOrderText.toIntOrNull() ?: 0
                    val imagePart = selectedImageUri?.let { uriToImagePart(context, it) }
                    onSubmit(name.trim(), description.trim(), displayOrder, imagePart)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && !isLoading
            ) {
                Text(if (isUpdating) "Cập nhật" else "Tạo danh mục", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun CategoryImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    val finalUrl = remember(imageUrl) { resolveImageUrl(imageUrl) }

    if (finalUrl == null) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF9E9E9E))
        }
        return
    }

    AsyncImage(
        model = finalUrl,
        contentDescription = null,
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Crop
    )
}

private fun resolveImageUrl(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    if (raw.startsWith("content://")) return raw
    return raw.toFullImageUrl()
}

@Composable
private fun DeleteConfirmationDialog(
    category: CategoryDto,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa danh mục", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, fontFamily = k2d) },
        text = {
            Text(
                text = "Bạn có chắc muốn xóa \"${category.name}\" không?",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Xóa", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", fontSize = 14.sp)
            }
        }
    )
}

@Composable
@Preview(showSystemUi = true)
private fun DeleteConfirmationDialogPreview() {
    CoffeeShopAppTheme() {
        DeleteConfirmationDialog(
            CategoryDto(
                id = 1,
                name = "Cà phê",
                description = "Các dòng cà phê mới nhất",
                displayOrder = 1,
                imageUrl = null,
                isActive = true
            )
        )
    }
}

@Composable
@Preview(showSystemUi = true)
private fun CategoryFormScreenPreview() {
    CoffeeShopAppTheme() {
        CategoryFormScreen(
            isUpdating = true,
            initialCategory = CategoryDto(
                id = 1,
                name = "Cà phê",
                description = "Các dòng cà phê mới nhất",
                displayOrder = 1,
                imageUrl = null,
                isActive = true
            ),
            isLoading = false,
            errorMessage = null
        )
    }
}

@Composable
@Preview(showSystemUi = true)
private fun CategoryDetailScreenPreview() {
    CoffeeShopAppTheme() {
        CategoryDetailScreen(
            CategoryDto(
                id = 1,
                name = "Cà phê",
                description = "Các dòng cà phê mới nhất",
                displayOrder = 1,
                imageUrl = null,
                isActive = true
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Danh sách đầy đủ")
@Composable
fun PreviewCategoryListSuccess() {
    val mockCategories = listOf(
        CategoryDto(id = 1, name = "Cà phê", description = "Mô tả 1", displayOrder = 1),
        CategoryDto(id = 2, name = "Sinh tố", description = "Mô tả 2", displayOrder = 2),
        CategoryDto(id = 3, name = "Bánh kem", description = "Mô tả 3", displayOrder = 3)
    )

    CoffeeShopAppTheme() {
        CategoryListScreen(
            uiState = CategoryUiState(
                categories = mockCategories,
                isLoading = false,
                error = null
            ),
            onAddClick = {},
            onDetailClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}