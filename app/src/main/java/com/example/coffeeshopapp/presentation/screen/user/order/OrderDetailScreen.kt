package com.example.coffeeshopapp.presentation.screen.user.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.viewmodel.OrderDetailViewModel
import com.example.coffeeshopapp.utils.formatGrouped

@Composable
fun OrderDetailScreen(
    orderId: Long,
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF3D3450))
                }
                Text(
                    text = "Chi tiết đơn hàng",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF3D3450),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        containerColor = BackgroundColor
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CoffeeTextColor)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!, color = Color.Red)
                }
            }
            uiState.order != null -> {
                OrderDetailContent(order = uiState.order!!, modifier = Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun OrderDetailContent(order: OrderDto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Trạng thái", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = order.status,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status.uppercase()) {
                            "COMPLETED" -> Color(0xFF10B981)
                            "CANCELLED" -> Color(0xFFEF4444)
                            "PENDING" -> Color(0xFFF59E0B)
                            else -> CoffeeTextColor
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Mã đơn hàng", color = Color.Gray, fontSize = 14.sp)
                    Text(text = "#${order.id}", fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Thời gian", color = Color.Gray, fontSize = 14.sp)
                    Text(text = order.createdAt ?: "N/A", fontWeight = FontWeight.Medium)
                }
            }
        }

        // Delivery Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Địa chỉ nhận hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = order.deliveryAddress ?: "N/A", color = Color.DarkGray)
            }
        }

        // Items
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Danh sách món", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                order.orderItems?.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.productName, fontWeight = FontWeight.Medium)
                            if (!item.size.isNullOrBlank()) {
                                Text(text = "Size: ${item.size}", fontSize = 12.sp, color = Color.Gray)
                            }
                            item.toppings?.let { tps ->
                                if (tps.isNotEmpty()) {
                                    Text(
                                        text = "Topping: " + tps.joinToString { it.toppingName ?: "" },
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        Text(text = "x${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                        Text(text = "${(item.unitPrice.toLong() * item.quantity).formatGrouped()}đ")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF5F5F5))
                }
            }
        }

        // Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryRow(label = "Tạm tính", value = "${order.orderSubTotal.toLong().formatGrouped()}đ")
                SummaryRow(label = "Khuyến mãi", value = "-${order.discountAmount.toLong().formatGrouped()}đ", valueColor = Color.Red)
                SummaryRow(label = "Phương thức", value = order.payment?.method ?: "N/A")
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Tổng cộng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = "${order.totalPrice.toLong().formatGrouped()}đ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CoffeeTextColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium, color = valueColor)
    }
}
