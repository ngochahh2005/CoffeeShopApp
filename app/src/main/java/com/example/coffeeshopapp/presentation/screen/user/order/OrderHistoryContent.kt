package com.example.coffeeshopapp.presentation.screen.user.order

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.TextColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.viewmodel.OrderHistoryUiState
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.formatToVietnameseDate

@Composable
fun OrderHistoryContent(
    uiState: OrderHistoryUiState,
    onBack: () -> Unit,
    onReviewClick: (productId: Long, productName: String, size: String?, imageUrl: String?) -> Unit,
    onCancelOrder: (Long) -> Unit,
    onRetryLoad: () -> Unit,
    onOrderClick: (Long) -> Unit
) {
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
                    Button(onClick = { onRetryLoad() }) {
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
                        onCancelClick = { onCancelOrder(order.id) },
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
    val (statusText, statusColor) = when (order.status.uppercase()) {
        "PENDING" -> "Chờ xác nhận" to Color(0xFFF59E0B)
        "CONFIRMED" -> "Đã xác nhận" to Color(0xFF3B82F6)
        "DELIVERING" -> "Đang giao" to Color(0xFF5856D6)
        "COMPLETED" -> "Đã hoàn tất" to Color(0xFF10B981)
        "CANCELLED" -> "Đã hủy" to Color(0xFFEF4444)
        else -> order.status to CoffeeTextColor
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOrderClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Đơn hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = statusText,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
            CommonSpace(8.dp)
            Text(text = "Ngày đặt: ${order.createdAt?.formatToVietnameseDate() ?: "N/A"}", fontSize = 14.sp, color = Color.Gray)
            CommonSpace(8.dp)
            Text(text = "Tổng tiền: ${order.totalPrice.toLong().formatGrouped()}đ", fontWeight = FontWeight.SemiBold, color = TextColor)

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
                order.orderItems?.forEach { item ->
                    Button(
                        onClick = {
                            if (item.productId > 0) {
                                onReviewClick(item.productId, item.productName, item.size, null)
                            } else {
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

@Composable
@Preview(showSystemUi = true)
private fun OrderHistoryPreview() {
    val mockOrders = listOf(
        OrderDto(
            id = 1,
            status = "PENDING",
            totalPrice = java.math.BigDecimal(50000),
            createdAt = "2026-05-11T16:48:44",
            orderItems = emptyList()
        ),
        OrderDto(
            id = 2,
            status = "COMPLETED",
            totalPrice = java.math.BigDecimal(120000),
            createdAt = "2026-05-27",
            orderItems = emptyList()
        ),
        OrderDto(
            id = 3,
            status = "DELIVERING",
            totalPrice = java.math.BigDecimal(150000),
            createdAt = "2026-05-24",
            orderItems = emptyList()
        ),
        OrderDto(
            id = 4,
            status = "CANCELLED",
            totalPrice = java.math.BigDecimal(80000),
            createdAt = "2026-05-28",
            orderItems = emptyList()
        )
    )

    val mockUiState = OrderHistoryUiState(
        isLoading = false,
        orders = mockOrders,
        error = null
    )

    CoffeeShopAppTheme {
        OrderHistoryContent(
            uiState = mockUiState,
            onBack = {},
            onReviewClick = { _, _, _, _ -> },
            onCancelOrder = {},
            onRetryLoad = {},
            onOrderClick = {}
        )
    }
}
