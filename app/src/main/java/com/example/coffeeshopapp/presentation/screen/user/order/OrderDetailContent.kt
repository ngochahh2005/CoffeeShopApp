package com.example.coffeeshopapp.presentation.screen.user.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.model.dto.OrderDto
import com.example.coffeeshopapp.data.model.dto.OrderItemDto
import com.example.coffeeshopapp.data.model.dto.OrderItemToppingDto
import com.example.coffeeshopapp.data.model.dto.PaymentDto
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.TextColor
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.formatToVietnameseDate
import java.math.BigDecimal

@Composable
fun OrderDetailContent(order: OrderDto, modifier: Modifier = Modifier) {
    val (statusText, statusColor) = when (order.status.uppercase()) {
        "PENDING" -> "Chờ xác nhận" to Color(0xFFF59E0B)
        "CONFIRMED" -> "Đã xác nhận" to Color(0xFF3B82F6)
        "DELIVERING" -> "Đang giao" to Color(0xFF5856D6)
        "COMPLETED" -> "Đã hoàn tất" to Color(0xFF10B981)
        "CANCELLED" -> "Đã hủy" to Color(0xFFEF4444)
        else -> order.status to CoffeeTextColor
    }

    val paymentMethod = when (order.payment?.method) {
        "CASH" -> "Tiền mặt (COD)"
        "VNPAY" -> "Ví điện tử VNPAY"
        else -> "N/A"
    }

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
                    Text(text = "Trạng thái", color = Color.Gray)
                    Text(
                        text = statusText,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Mã đơn hàng", color = Color.Gray)
                    Text(text = "${order.id}", fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Thời gian", color = Color.Gray)
                    Text(text = order.createdAt?.formatToVietnameseDate() ?: "N/A", fontWeight = FontWeight.Medium)
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
                Text(text = "Địa chỉ nhận hàng", fontWeight = FontWeight.Bold)
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
                Text(text = "Danh sách món", fontWeight = FontWeight.Bold)
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
                SummaryRow(label = "Phương thức", value = paymentMethod)
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Tổng cộng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = "${order.totalPrice.toLong().formatGrouped()}đ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextColor
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

@Composable
@Preview(showSystemUi = true)
private fun OrderDetailPreview() {
    val mockToppings = listOf(
        OrderItemToppingDto(toppingName = "Trân châu đen"),
        OrderItemToppingDto(toppingName = "Thạch trái cây")
    )

    // 2. Tạo danh sách món ăn/nước uống giả lập
    val mockOrderItems = listOf(
        OrderItemDto(
            productId = 101,
            productName = "Cà phê Sữa Đá",
            size = "M",
            unitPrice = BigDecimal(29000),
            quantity = 2,
            toppings = emptyList() // Món này không thêm topping
        ),
        OrderItemDto(
            productId = 102,
            productName = "Trà Sữa Khoai Môn",
            size = "L",
            unitPrice = BigDecimal(35000),
            quantity = 1,
            toppings = mockToppings // Món này có kèm topping ở trên
        )
    )

    // 3. Đưa tất cả vào OrderDto để hiển thị lên Preview
    CoffeeShopAppTheme {
        OrderDetailContent(
            order = OrderDto(
                id = 12345,
                // Tính toán tổng tiền thực tế để hiển thị cho khớp logic:
                // (29000 * 2) + (35000 * 1) = 93000đ
                orderSubTotal = BigDecimal(93000),
                discountAmount = BigDecimal(15000), // Giả lập giảm giá 15k
                totalPrice = BigDecimal(78000),     // Tổng cộng = 93k - 15k = 78k
                status = "CONFIRMED", // Đổi thử thành DELIVERING, COMPLETED... để xem đổi màu
                createdAt = "2026-05-11T16:48:44",
                deliveryAddress = "Học viện Công nghệ Bưu chính Viễn thông (PTIT) - Hà Đông",
                payment = PaymentDto(method = "Tiền mặt (COD)"), // Giả lập phương thức thanh toán
                orderItems = mockOrderItems // Truyền danh sách món vào đây
            )
        )
    }
}
