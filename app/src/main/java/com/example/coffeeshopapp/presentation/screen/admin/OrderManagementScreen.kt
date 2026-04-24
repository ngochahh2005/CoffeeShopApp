package com.example.coffeeshopapp.presentation.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.presentation.viewmodel.AdminOrderViewModel
import java.text.NumberFormat
import java.util.Locale

private val Brown = Color(0xFF553722)
private val Blue = Color(0xFF007AFF)
private val Green = Color(0xFF34C759)
private val Red = Color(0xFFFF3B30)
private val Orange = Color(0xFFFF9500)
private val Bg = Color(0xFFF7F8FA)
private val Card = Color(0xFFFFFFFF)
private val Sub = Color(0xFF8E8E93)

private fun moneyFmt(v: java.math.BigDecimal?): String {
    if (v == null) return "0Ä‘"
    return "${NumberFormat.getInstance(Locale.forLanguageTag("vi-VN")).format(v)}Ä‘"
}

private fun formatOrderTime(raw: String): String {
    return try {
        // ISO format: 2026-04-23T10:25:45 or similar
        val cleaned = raw.take(19) // "2026-04-23T10:25:45"
        val date = cleaned.substringBefore("T")
        val time = cleaned.substringAfter("T")
        "$time  $date"
    } catch (_: Exception) {
        raw.take(19)
    }
}

private val statusTabs = listOf("ALL" to "Tất cả", "PENDING" to "Chờ xác nhận", "CONFIRMED" to "Đã xác nhận", "DELIVERING" to "Đang giao", "COMPLETED" to "Hoàn tất", "CANCELLED" to "Đã huỷ")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(viewModel: AdminOrderViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    // Detail bottom sheet
    if (state.showDetailSheet && state.selectedOrder != null) {
        ModalBottomSheet(onDismissRequest = viewModel::dismissDetail, containerColor = Card) {
            OrderDetailSheet(order = state.selectedOrder!!, viewModel = viewModel)
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Quản lý đơn hàng", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Brown) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Status Tabs
            ScrollableTabRow(
                selectedTabIndex = statusTabs.indexOfFirst { it.first == state.currentTab }.coerceAtLeast(0),
                edgePadding = 12.dp,
                containerColor = Color.Transparent,
                contentColor = Brown
            ) {
                statusTabs.forEach { (key, label) ->
                    Tab(
                        selected = state.currentTab == key,
                        onClick = { viewModel.switchTab(key) },
                        text = { Text(label, fontSize = 13.sp) }
                    )
                }
            }

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brown) }
                state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Lỗi: ${state.error}", color = Red) }
                state.orders.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Không có đơn hàng nào.", color = Sub) }
                else -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.orders) { order -> OrderCard(order = order, onDetail = { viewModel.showDetail(order) }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: OrderDto, onDetail: () -> Unit) {
    val statusColor = when (order.status.uppercase()) {
        "COMPLETED" -> Green; "PENDING" -> Orange; "CANCELLED" -> Red; "CONFIRMED" -> Blue; "DELIVERING" -> Color(0xFF5856D6); else -> Sub
    }
    val statusLabel = when (order.status.uppercase()) {
        "COMPLETED" -> "Hoàn tất"; "PENDING" -> "Chờ XN"; "CANCELLED" -> "Đã huỷ"; "CONFIRMED" -> "Đã XN"; "DELIVERING" -> "Đang giao"; else -> order.status
    }
    Surface(shape = RoundedCornerShape(16.dp), color = Card, shadowElevation = 2.dp, onClick = onDetail) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(44.dp), shape = CircleShape, color = statusColor.copy(0.12f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Receipt, null, tint = statusColor) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("#${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Brown)
                Text(order.user?.fullName ?: order.user?.username ?: "", fontSize = 12.sp, color = Sub, maxLines = 1)
                Text(moneyFmt(order.totalPrice), fontSize = 14.sp, color = Brown, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(0.12f)) {
                    Text(statusLabel, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
                }
                if (order.createdAt != null) { Text(order.createdAt.take(10), fontSize = 10.sp, color = Sub, modifier = Modifier.padding(top = 4.dp)) }
            }
        }
    }
}

@Composable
private fun OrderDetailSheet(order: OrderDto, viewModel: AdminOrderViewModel) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("Chi tiết đơn hàng #${order.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Brown)
        Spacer(Modifier.height(12.dp))
        InfoRow("Khách hàng", order.user?.fullName ?: order.user?.username ?: "")
        InfoRow("Địa chỉ giao", order.deliveryAddress ?: "—")
        InfoRow("Ghi chú", order.note ?: "—")
        if (order.createdAt != null) {
            InfoRow("Thời gian tạo", formatOrderTime(order.createdAt))
        }
        HorizontalDivider(Modifier.padding(vertical = 10.dp))

        Text("Danh sách món", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Brown)
        Spacer(Modifier.height(6.dp))
        order.orderItems?.forEach { item ->
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                Text("${item.productName} ${item.size?.let { "($it)" } ?: ""}", Modifier.weight(1f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("x${item.quantity}", fontSize = 13.sp, color = Sub)
                Spacer(Modifier.width(8.dp))
                Text(moneyFmt(item.unitPrice), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Brown)
            }
            item.toppings?.forEach { t ->
                Text("  + ${t.toppingName}: ${moneyFmt(t.price)}", fontSize = 11.sp, color = Sub, modifier = Modifier.padding(start = 12.dp))
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 10.dp))

        if (order.discountAmount > java.math.BigDecimal.ZERO) { InfoRow("Giảm giá", "-${moneyFmt(order.discountAmount)}") }
        InfoRow("Tổng thanh toán", moneyFmt(order.totalPrice), bold = true)
        if (order.payment != null) { InfoRow("Thanh toán", "${order.payment.method ?: ""} - ${order.payment.status ?: ""}") }

        Spacer(Modifier.height(16.dp))

        // Status update buttons
        val nextStatus = when (order.status.uppercase()) {
            "PENDING" -> "CONFIRMED" to "Xác nhận đơn"
            "CONFIRMED" -> "DELIVERING" to "Bắt đầu giao"
            "DELIVERING" -> "COMPLETED" to "Hoàn tất giao"
            else -> null
        }
        if (nextStatus != null) {
            Button(
                onClick = { viewModel.updateOrderStatus(order.id, nextStatus.first) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brown)
            ) { Text(nextStatus.second) }
        }
        if (order.status.uppercase() in listOf("PENDING", "CONFIRMED")) {
            OutlinedButton(
                onClick = { viewModel.updateOrderStatus(order.id, "CANCELLED") },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Red)
            ) { Text("Huỷ đơn hàng") }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, fontSize = 13.sp, color = Sub, modifier = Modifier.width(120.dp))
        Text(value, fontSize = 13.sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = Brown)
    }
}


