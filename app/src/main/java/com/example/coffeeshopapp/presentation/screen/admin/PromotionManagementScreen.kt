package com.example.coffeeshopapp.presentation.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.model.dto.PromotionDto
import com.example.coffeeshopapp.data.model.dto.PromotionRequestDto
import com.example.coffeeshopapp.presentation.viewmodel.AdminPromotionScreenType
import com.example.coffeeshopapp.presentation.viewmodel.AdminPromotionViewModel
import java.time.LocalDate

private val Brown = Color(0xFF553722)
private val Purple = Color(0xFF6A1B9A)
private val Green = Color(0xFF34C759)
private val Red = Color(0xFFFF3B30)
private val Bg = Color(0xFFF7F8FA)
private val Card = Color(0xFFFFFFFF)
private val Sub = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionManagementScreen(viewModel: AdminPromotionViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    if (state.showDeleteDialog && state.selectedPromotion != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text("Xác nhận xoá", fontWeight = FontWeight.SemiBold) },
            text = { Text("Bạn có chắc muốn xoá khuyến mãi \"${state.selectedPromotion!!.name}\"?") },
            confirmButton = { TextButton(onClick = viewModel::confirmDelete) { Text("Xoá", color = Red) } },
            dismissButton = { TextButton(onClick = viewModel::dismissDeleteDialog) { Text("Huỷ") } }
        )
    }

    if (state.currentScreen == AdminPromotionScreenType.DETAIL && state.selectedPromotion != null) {
        ModalBottomSheet(onDismissRequest = viewModel::showList, containerColor = Card) {
            PromotionDetailTableSheet(promo = state.selectedPromotion!!, onEdit = { state.selectedPromotion?.let { viewModel.showUpdateForm(it) } })
        }
    }

    when (state.currentScreen) {
        AdminPromotionScreenType.CREATE -> PromotionFormScreen(isUpdate = false, promo = null, isLoading = state.isLoading, errorMessage = state.error, onSubmit = { viewModel.createPromotion(it) }, onBack = viewModel::showList)
        AdminPromotionScreenType.UPDATE -> PromotionFormScreen(isUpdate = true, promo = state.selectedPromotion, isLoading = state.isLoading, errorMessage = state.error, onSubmit = { state.selectedPromotion?.id?.let { id -> viewModel.updatePromotion(id, it) } }, onBack = viewModel::showList)
        else -> PromotionListScreen(state, viewModel, onBackClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromotionListScreen(state: com.example.coffeeshopapp.presentation.viewmodel.PromotionUiState, vm: AdminPromotionViewModel, onBack: () -> Unit) {
    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Quản lý khuyến mãi", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Brown) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = vm::showCreateForm, containerColor = Purple) { Icon(Icons.Default.Add, null, tint = Color.White) } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = state.searchQuery, onValueChange = vm::onSearchChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Tìm theo tên, mã KM...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null) }, shape = RoundedCornerShape(14.dp), singleLine = true
            )
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brown) }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Lỗi: ${state.error}", color = Red) }
                else -> {
                    val list = vm.filteredPromotions
                    if (list.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Chưa có khuyến mãi.", color = Sub) }
                    else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(list) { p -> PromotionCard(p, onDetail = { vm.showDetail(p) }, onEdit = { vm.showUpdateForm(p) }, onDelete = { vm.showDeleteDialog(p) }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionCard(p: PromotionDto, onDetail: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isActive = p.status.uppercase() == "ACTIVE"
    Surface(shape = RoundedCornerShape(16.dp), color = Card, shadowElevation = 2.dp, onClick = onDetail) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(40.dp), shape = RoundedCornerShape(10.dp), color = Purple.copy(0.12f)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Discount, null, tint = Purple, modifier = Modifier.size(22.dp)) }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(p.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Brown, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(p.promotionCode, fontSize = 12.sp, color = Purple, fontWeight = FontWeight.Medium)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = (if (isActive) Green else Sub).copy(0.12f)) {
                    Text(if (isActive) "Đang hoạt động" else p.status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, color = if (isActive) Green else Sub, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Giảm: ${if (p.discountType == "PERCENTAGE") "${p.discountValue.toInt()}%" else "${p.discountValue.toInt()}đ"}", fontSize = 12.sp, color = Color(0xFF555555), modifier = Modifier.weight(1f))
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Edit, null, tint = Green, modifier = Modifier.size(18.dp)) }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.Delete, null, tint = Red, modifier = Modifier.size(18.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromotionFormScreen(
    isUpdate: Boolean,
    promo: PromotionDto?,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: (PromotionRequestDto) -> Unit,
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

    var name by rememberSaveable { mutableStateOf(promo?.name ?: "") }
    var code by rememberSaveable { mutableStateOf(promo?.promotionCode ?: "") }
    var discountType by rememberSaveable { mutableStateOf(promo?.discountType ?: "PERCENTAGE") }
    var promotionType by rememberSaveable { mutableStateOf(promo?.promotionType ?: "LIMITED") }
    var discountValue by rememberSaveable { mutableStateOf(promo?.discountValue?.toString() ?: "") }
    var minOrder by rememberSaveable { mutableStateOf(promo?.minOrderValue?.toString() ?: "0") }
    var startDate by rememberSaveable { mutableStateOf(promo?.startDate ?: "") }
    var endDate by rememberSaveable { mutableStateOf(promo?.endDate ?: "") }
    var autoApply by rememberSaveable { mutableStateOf(promo?.autoApply ?: false) }
    var status by rememberSaveable { mutableStateOf(promo?.status ?: "ACTIVE") }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text(if (isUpdate) "Cập nhật khuyến mãi" else "Tạo khuyến mãi mới", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Brown) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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

        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tên khuyến mãi") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = code, onValueChange = { code = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Mã khuyến mãi") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            
            // Promotion type toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Loại KM:", fontSize = 14.sp, modifier = Modifier.width(100.dp))
                FilterChip(selected = promotionType == "LIMITED", onClick = { promotionType = "LIMITED" }, label = { Text("Có hạn") })
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = promotionType == "UNLIMITED", onClick = { promotionType = "UNLIMITED" }, label = { Text("Vô hạn") })
            }

            // Discount type toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Loại giảm giá:", fontSize = 14.sp, modifier = Modifier.width(100.dp))
                FilterChip(selected = discountType == "PERCENTAGE", onClick = { discountType = "PERCENTAGE" }, label = { Text("Phần trăm %") })
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = discountType == "FIXED_AMOUNT", onClick = { discountType = "FIXED_AMOUNT" }, label = { Text("Cố định đ") })
            }

            OutlinedTextField(value = discountValue, onValueChange = { discountValue = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Giá trị giảm") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = minOrder, onValueChange = { minOrder = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Đơn tối thiểu") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            
            if (promotionType == "LIMITED") {
                OutlinedTextField(value = startDate, onValueChange = { startDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Ngày bắt đầu (yyyy-MM-dd)") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = endDate, onValueChange = { endDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Ngày kết thúc (yyyy-MM-dd)") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tự động áp dụng", fontSize = 14.sp, modifier = Modifier.weight(1f))
                Switch(checked = autoApply, onCheckedChange = { autoApply = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Trạng thái:", fontSize = 14.sp, modifier = Modifier.width(100.dp))
                FilterChip(selected = status == "ACTIVE", onClick = { status = "ACTIVE" }, label = { Text("Active") })
                Spacer(Modifier.width(8.dp))
                FilterChip(selected = status == "INACTIVE", onClick = { status = "INACTIVE" }, label = { Text("Inactive") })
            }
            if (isLoading) CircularProgressIndicator(color = Brown, modifier = Modifier.align(Alignment.CenterHorizontally))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Huỷ bỏ") }
                Button(
                    onClick = {
                        val start = startDate.trim()
                        val end = endDate.trim()
                        if (promotionType == "LIMITED") {
                            if (start.isBlank() || end.isBlank()) {
                                lastError = "Vui lòng nhập ngày bắt đầu và ngày kết thúc"
                                showError = true
                                return@Button
                            }

                            val startParsed = runCatching { LocalDate.parse(start) }.getOrNull()
                            val endParsed = runCatching { LocalDate.parse(end) }.getOrNull()
                            if (startParsed == null || endParsed == null) {
                                lastError = "Sai định dạng ngày. Đúng: yyyy-MM-dd (vd: 2026-04-25)"
                                showError = true
                                return@Button
                            }
                            if (endParsed.isBefore(startParsed)) {
                                lastError = "Ngày kết thúc phải >= ngày bắt đầu"
                                showError = true
                                return@Button
                            }
                        }

                        onSubmit(PromotionRequestDto(
                            name = name, 
                            promotionCode = code, 
                            autoApply = autoApply, 
                            discountType = discountType,
                            promotionType = promotionType,
                            discountValue = discountValue.toDoubleOrNull() ?: 0.0, 
                            minOrderValue = minOrder.toDoubleOrNull() ?: 0.0,
                            startDate = start.ifBlank { null },
                            endDate = end.ifBlank { null },
                            status = status
                        ))
                    },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Brown),
                    enabled = !isLoading && name.isNotBlank() && code.isNotBlank()
                ) { Text(if (isUpdate) "Lưu cập nhật" else "Tạo mới") }
            }
        }
    }
}

@Composable
private fun PromotionDetailSheet(promo: PromotionDto, onEdit: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Chi tiết khuyến mãi", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Brown)
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Purple) }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PDetailLine("Tên", promo.name)
            PDetailLine("Mã", promo.promotionCode)
            PDetailLine("Loại giới hạn", if (promo.promotionType == "LIMITED") "Có giới hạn thời gian" else "Vô hạn")
            PDetailLine("Loại giảm", if (promo.discountType == "PERCENTAGE") "Phần trăm" else "Cố định")
            PDetailLine("Giá trị", if (promo.discountType == "PERCENTAGE") "${promo.discountValue.toInt()}%" else "${promo.discountValue.toInt()}đ")
            PDetailLine("Đơn tối thiểu", "${promo.minOrderValue.toInt()}đ")
            if (promo.promotionType == "LIMITED") {
                PDetailLine("Bắt đầu", promo.startDate ?: "—")
                PDetailLine("Kết thúc", promo.endDate ?: "—")
            }
            PDetailLine("Tự động", if (promo.autoApply) "Có" else "Không")
            
            val statusColor = if (promo.status == "ACTIVE") Green else Sub
            Row { 
                Text("Trạng thái", fontSize = 14.sp, color = Sub, modifier = Modifier.width(120.dp))
                Surface(color = statusColor.copy(0.12f), shape = RoundedCornerShape(4.dp)) {
                    Text(promo.status, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) 
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PDetailLine(l: String, v: String) { 
    Row(verticalAlignment = Alignment.Top) { 
        Text(l, fontSize = 14.sp, color = Sub, modifier = Modifier.width(120.dp))
        Text(v, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333333), modifier = Modifier.weight(1f)) 
    } 
}

@Composable
private fun PromotionDetailTableSheet(promo: PromotionDto, onEdit: () -> Unit) {
    val statusColor = if (promo.status == "ACTIVE") Green else Sub
    val rows = buildList {
        add("Tên" to promo.name)
        add("Mã" to promo.promotionCode)
        add("Loại khuyến mại" to if (promo.promotionType == "LIMITED") "Có thời gian" else "Vô hạn")
        add("Kiểu giảm" to if (promo.discountType == "PERCENTAGE") "Phần trăm" else "Cố định")
        add("Giá trị" to if (promo.discountType == "PERCENTAGE") "${promo.discountValue.toInt()}%" else "${promo.discountValue.toInt()}đ")
        add("Đơn tối thiểu" to "${promo.minOrderValue.toInt()}đ")
        if (promo.promotionType == "LIMITED") {
            add("Bắt đầu" to (promo.startDate ?: "—"))
            add("Kết thúc" to (promo.endDate ?: "—"))
        }
        add("Tự động áp dụng" to if (promo.autoApply) "Có" else "Không")
        promo.timeStart?.takeIf { it.isNotBlank() }?.let { add("Giờ bắt đầu" to it) }
        promo.timeEnd?.takeIf { it.isNotBlank() }?.let { add("Giờ kết thúc" to it) }
        promo.usageLimitTotal?.let { add("Giới hạn tổng" to it.toString()) }
        promo.usageLimitPerUserTotal?.let { add("Mỗi người dùng" to it.toString()) }
        promo.usageLimitPerUserPerDay?.let { add("Mỗi ngày / user" to it.toString()) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Chi tiết khuyến mại", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Brown)
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.12f)) {
                    Text(
                        text = promo.status,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            FilledTonalIconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Purple)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFAFAFA),
            tonalElevation = 1.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6E6E6))
        ) {
            Column(Modifier.fillMaxWidth()) {
                rows.forEachIndexed { index, (label, value) ->
                    PromotionDetailTableRow(
                        label = label,
                        value = value,
                        isLast = index == rows.lastIndex
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PromotionDetailTableRow(label: String, value: String, isLast: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(label, fontSize = 13.sp, color = Sub, modifier = Modifier.width(132.dp))
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.weight(1f)
            )
        }
        if (!isLast) {
            HorizontalDivider(color = Color(0xFFECECEC))
        }
    }
}
