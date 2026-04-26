package com.example.coffeeshopapp.presentation.screen.admin.product

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.ProductSizeRequestDto
import com.example.coffeeshopapp.presentation.viewmodel.AdminProductScreenType
import com.example.coffeeshopapp.presentation.viewmodel.AdminProductViewModel
import com.example.coffeeshopapp.presentation.viewmodel.ProductUiState
import com.example.coffeeshopapp.utils.isActiveResolved
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.toFullImageUrl
import com.example.coffeeshopapp.utils.uriToImagePart
import okhttp3.MultipartBody

@Composable
fun AdminProductScreen(
    viewModel: AdminProductViewModel,
    isDeepLink: Boolean = false,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteConfirmDialog && uiState.selectedProduct != null) {
        DeleteConfirmationDialog(
            product = uiState.selectedProduct!!,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog
        )
    }

    when (uiState.currentScreen) {
        AdminProductScreenType.LIST -> {
            ProductListScreen(
                uiState = uiState,
                onAddClick = viewModel::showCreateForm,
                onDetailClick = { viewModel.loadProductDetail(it.id) },
                onEditClick = { viewModel.loadProductForUpdate(it.id) },
                onDeleteClick = viewModel::showDeleteDialog,
                onBackClick = onBackClick
            )
        }

        AdminProductScreenType.DETAIL -> {
            ProductDetailScreen(
                product = uiState.selectedProduct,
                categories = uiState.categories,
                onBack = {
                    if (isDeepLink) {
                        onBackClick()
                    } else {
                        viewModel.showList()
                    }
                },
                onEdit = {
                    uiState.selectedProduct?.id?.let { viewModel.loadProductForUpdate(it) }
                }
            )
        }

        AdminProductScreenType.CREATE -> {
            ProductFormScreen(
                isUpdating = false,
                initialProduct = null,
                categories = uiState.categories,
                isLoading = uiState.isLoading,
                errorMessage = uiState.error,
                onSubmit = { name, desc, basePrice, catId, sizes, image ->
                    viewModel.createProduct(name, desc, basePrice, catId, sizes, image)
                },
                onBack = viewModel::showList
            )
        }

        AdminProductScreenType.UPDATE -> {
            ProductFormScreen(
                isUpdating = true,
                initialProduct = uiState.selectedProduct,
                categories = uiState.categories,
                isLoading = uiState.isLoading,
                errorMessage = uiState.error,
                onSubmit = { name, desc, basePrice, catId, sizes, image ->
                    val id = uiState.selectedProduct?.id ?: return@ProductFormScreen
                    viewModel.updateProduct(id, name, desc, basePrice, catId, sizes, image)
                },
                onBack = viewModel::showList
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListScreen(
    uiState: ProductUiState,
    onAddClick: () -> Unit,
    onDetailClick: (ProductDto) -> Unit,
    onEditClick: (ProductDto) -> Unit,
    onDeleteClick: (ProductDto) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            TopAppBar(
                title = { Text("Quản lý sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Thêm sản phẩm")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.products.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Chưa có sản phẩm nào.")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onDetailClick = { onDetailClick(product) },
                            onEditClick = { onEditClick(product) },
                            onDeleteClick = { onDeleteClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductDto,
    onDetailClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onDetailClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductImage(imageUrl = product.imageUrl, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontSize = 17.sp, lineHeight = 21.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Giá: ${product.basePrice.formatGrouped()} đ", fontSize = 13.sp, color = Color(0xFF757575))
                Text(
                    text = if (product.isActiveResolved()) "Đang kinh doanh" else "Ngừng bán",
                    fontSize = 13.sp,
                    color = if (product.isActiveResolved()) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
                )
            }
            Row {
                IconButton(onClick = onDetailClick) { Icon(Icons.Default.Visibility, "Xem chi tiết") }
                IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Sửa") }
                IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Xoá", tint = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductDto?,
    categories: List<CategoryDto>,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại") }
                },
                actions = { IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Sửa") } }
            )
        }
    ) { padding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Không tìm thấy sản phẩm.")
            }
            return@Scaffold
        }
        val categoryName = categories.find { it.id == product.categoryId }?.name ?: "Không xác định(${product.categoryId})"

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProductImage(imageUrl = product.imageUrl, modifier = Modifier.fillMaxWidth().height(180.dp))
            DetailRow("Tên sản phẩm", product.name)
            DetailRow("Danh mục", categoryName)
            DetailRow("Giá cơ bản", "${product.basePrice.formatGrouped()} đ")
            DetailRow("Mô tả", product.desc ?: "Không có")
            DetailRow("Trạng thái", if (product.isActiveResolved()) "Đang kinh doanh" else "Ngừng kinh doanh")
            
            Text("Các size và phụ thu", fontSize = 17.sp, lineHeight = 21.sp, fontWeight = FontWeight.Bold)
            product.size.forEach { size ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Size ${size.sizeName}")
                    Text("+${size.priceExtra.formatGrouped()} đ", color = Color(0xFF757575))
                }
            }
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
@Suppress("DEPRECATION")
@Composable
private fun ProductFormScreen(
    isUpdating: Boolean,
    initialProduct: ProductDto?,
    categories: List<CategoryDto>,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: (String, String, Long, Long, List<ProductSizeRequestDto>, MultipartBody.Part?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var lastError by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank() && errorMessage != lastError) {
            lastError = errorMessage
            showError = true
        }
    }

    var name by rememberSaveable(initialProduct?.id) { mutableStateOf(initialProduct?.name.orEmpty()) }
    var basePriceText by rememberSaveable(initialProduct?.id) { mutableStateOf(initialProduct?.basePrice?.toLong()?.toString() ?: "") }
    var desc by rememberSaveable(initialProduct?.id) { mutableStateOf(initialProduct?.desc.orEmpty()) }
    var selectedCategoryId by rememberSaveable(initialProduct?.id) { mutableStateOf(initialProduct?.categoryId) }
    var catExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(initialProduct?.categoryId, categories.size) {
        if (selectedCategoryId == null) {
            selectedCategoryId = initialProduct?.categoryId
        }
    }
    
    // Size management S, M, L
    val originalSizes = initialProduct?.size ?: emptyList()
    var sActive by rememberSaveable(initialProduct?.id) { mutableStateOf(originalSizes.any { it.sizeName == "S" }) }
    var mActive by rememberSaveable(initialProduct?.id) { mutableStateOf(originalSizes.any { it.sizeName == "M" }) }
    var lActive by rememberSaveable(initialProduct?.id) { mutableStateOf(originalSizes.any { it.sizeName == "L" }) }
    
    var sPriceText by rememberSaveable(initialProduct?.id) { mutableStateOf(originalSizes.find { it.sizeName == "S" }?.priceExtra?.toLong()?.toString() ?: "0") }
    var mPriceText by rememberSaveable(initialProduct?.id) { mutableStateOf(originalSizes.find { it.sizeName == "M" }?.priceExtra?.toLong()?.toString() ?: "0") }
    var lPriceText by rememberSaveable(initialProduct?.id) { mutableStateOf(originalSizes.find { it.sizeName == "L" }?.priceExtra?.toLong()?.toString() ?: "0") }

    var selectedImageUri by rememberSaveable(initialProduct?.id) { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedImageUri = uri }
    val title = if (isUpdating) "Cập nhật sản phẩm" else "Tạo sản phẩm"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại") } }
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
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Tên sản phẩm") }, singleLine = true
            )

            ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }?.name ?: "Chọn danh mục",
                    onValueChange = {}, readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                )
                ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = { selectedCategoryId = cat.id; catExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = basePriceText, onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) basePriceText = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Giá cơ bản (VNĐ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
            )

            OutlinedTextField(
                value = desc, onValueChange = { desc = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Mô tả") }, minLines = 3
            )

            Text("Phụ thu kích cỡ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            
            SizeRow("Size S", sActive, { sActive = it }, sPriceText, { if (it.isEmpty() || it.all { char -> char.isDigit() }) sPriceText = it })
            SizeRow("Size M", mActive, { mActive = it }, mPriceText, { if (it.isEmpty() || it.all { char -> char.isDigit() }) mPriceText = it })
            SizeRow("Size L", lActive, { lActive = it }, lPriceText, { if (it.isEmpty() || it.all { char -> char.isDigit() }) lPriceText = it })

            Text("Ảnh sản phẩm", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Surface(
                modifier = Modifier.fillMaxWidth().height(180.dp).clickable { imagePicker.launch("image/*") },
                shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color(0xFFBDBDBD)), color = Color(0xFFF8F8F8)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val previewImage = selectedImageUri ?: initialProduct?.imageUrl?.let { Uri.parse(it) }
                    if (previewImage != null) {
                        ProductImage(imageUrl = previewImage.toString(), modifier = Modifier.fillMaxSize())
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF757575))
                            Spacer(Modifier.height(6.dp))
                            Text("Nhấn để chọn ảnh", color = Color(0xFF757575))
                        }
                    }
                }
            }

            if (isLoading) CircularProgressIndicator()

            Button(
                onClick = {
                    val basePrice = basePriceText.toLongOrNull() ?: 0L
                    val sizes = mutableListOf<ProductSizeRequestDto>()
                    if (sActive) sizes.add(ProductSizeRequestDto("S", sPriceText.toLongOrNull() ?: 0L))
                    if (mActive) sizes.add(ProductSizeRequestDto("M", mPriceText.toLongOrNull() ?: 0L))
                    if (lActive) sizes.add(ProductSizeRequestDto("L", lPriceText.toLongOrNull() ?: 0L))
                    
                    val imagePart = selectedImageUri?.let { uriToImagePart(context, it) }
                    (selectedCategoryId ?: initialProduct?.categoryId)?.let { catId ->
                        onSubmit(name.trim(), desc.trim(), basePrice, catId, sizes, imagePart)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && basePriceText.isNotBlank() && (selectedCategoryId != null || initialProduct?.categoryId != null) && !isLoading
            ) {
                Text(if (isUpdating) "Cập nhật" else "Tạo sản phẩm")
            }
        }
    }
}

@Composable
private fun SizeRow(
    label: String,
    isActive: Boolean, onActiveChange: (Boolean) -> Unit,
    priceValue: String, onPriceChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Checkbox(checked = isActive, onCheckedChange = onActiveChange)
        Text(label, modifier = Modifier.width(60.dp))
        Spacer(Modifier.width(16.dp))
        OutlinedTextField(
            value = priceValue, onValueChange = onPriceChange,
            modifier = Modifier.weight(1f), label = { Text("Phụ thu") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = isActive, singleLine = true
        )
    }
}

@Composable
private fun ProductImage(imageUrl: String?, modifier: Modifier = Modifier) {
    val finalUrl = remember(imageUrl) { resolveImageUrl(imageUrl) }
    if (finalUrl == null) {
        Box(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF9E9E9E))
        }
        return
    }
    AsyncImage(model = finalUrl, contentDescription = null, modifier = modifier.clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
}

private fun resolveImageUrl(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    if (raw.startsWith("content://")) return raw
    return raw.toFullImageUrl()
}

@Composable
private fun DeleteConfirmationDialog(product: ProductDto, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Xoá sản phẩm") },
        text = { Text("Bạn có chắc muốn xoá \"${product.name}\" không?", maxLines = 3, overflow = TextOverflow.Ellipsis) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Xoá", color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}


