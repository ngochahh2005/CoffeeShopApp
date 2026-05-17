package com.example.coffeeshopapp.presentation.screen.user.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.coffeeshopapp.presentation.viewmodel.OrderHistoryViewModel
import com.example.coffeeshopapp.utils.formatGrouped

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel(),
    onBack: () -> Unit,
    onReviewClick: (productId: Long, productName: String, size: String?, imageUrl: String?) -> Unit,
    onOrderClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = TitleColor)
            }
            Text(
                text = "Lịch sử đơn hàng",
                style = MaterialTheme.typography.titleMedium,
                color = TitleColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CoffeeTextColor)
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Lỗi: ${uiState.error}", color = Color.Red)
                    Button(onClick = { viewModel.loadOrders() }) {
                        Text("Thử lại")
                    }
                }
            }
        } else if (uiState.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Bạn chưa có đơn hàng nào.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.orders) { order ->
                    OrderHistoryItem(
                        order = order,
                        onReviewClick = onReviewClick,
                        onCancelClick = { viewModel.cancelOrder(order.id) },
                        onOrderClick = { onOrderClick(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItem(
    order: OrderDto,
    onReviewClick: (productId: Long, productName: String, size: String?, imageUrl: String?) -> Unit,
    onCancelClick: () -> Unit,
    onOrderClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOrderClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Đơn hàng #${order.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = order.status,
                    color = when (order.status.uppercase()) {
                        "COMPLETED" -> Color(0xFF10B981)
                        "CANCELLED" -> Color(0xFFEF4444)
                        "PENDING" -> Color(0xFFF59E0B)
                        else -> CoffeeTextColor
                    },
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Ngày đặt: ${order.createdAt ?: "N/A"}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Tổng tiền: ${order.totalPrice.toLong().formatGrouped()}đ", fontWeight = FontWeight.SemiBold, color = CoffeeTextColor)

            if (order.status.uppercase() == "PENDING") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hủy đơn hàng", fontSize = 12.sp, color = Color.White)
                }
            }

            if (order.status.uppercase() == "COMPLETED") {
                Spacer(modifier = Modifier.height(8.dp))
                // User expects to click review for the order. 
                // Since the UI shows review for a product, let's list items or just review the first one for now.
                // Better yet, if there are multiple items, we should ideally review each, but based on prompt:
                // "người dùng có thể click nút đánh giá tương ứng với đơn hàng đó"
                
                order.orderItems?.forEach { item ->
                    Button(
                        onClick = {
                            if (item.productId > 0) {
                                onReviewClick(item.productId, item.productName, item.size, null)
                            } else {
                                // Fallback nếu không có productId, có thể hiện Toast
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B6BA8)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Đánh giá ${item.productName}", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
