package com.example.coffeeshopapp.presentation.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.model.dto.*
import com.example.coffeeshopapp.presentation.viewmodel.DashboardUiState
import com.example.coffeeshopapp.presentation.viewmodel.DashboardViewModel
import com.example.coffeeshopapp.utils.toFullImageUrl
import java.text.NumberFormat
import java.util.Locale

private val CoffeeBrown = Color(0xFF553722)
private val CoffeeLight = Color(0xFF6F4E37)
private val AccentBlue = Color(0xFF007AFF)
private val AccentGreen = Color(0xFF34C759)
private val AccentRed = Color(0xFFFF3B30)
private val AccentOrange = Color(0xFFFF9500)
private val SurfaceBg = Color(0xFFF7F8FA)
private val CardBg = Color(0xFFFFFFFF)
private val SubText = Color(0xFF8E8E93)

private fun formatMoney(value: java.math.BigDecimal?): String {
    if (value == null) return "0đ"
    val fmt = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${fmt.format(value)}đ"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit,
    onOpenCategory: () -> Unit = {},
    onOpenProduct: () -> Unit = {},
    onOpenUsers: () -> Unit = {},
    onOpenOrders: (String) -> Unit = {},
    onOpenPromotions: () -> Unit = {},
    onOpenReviews: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = SurfaceBg,
        topBar = {
            TopAppBar(
                title = { Text("Tổng quan", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = CoffeeBrown) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = CoffeeBrown) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (state.isLoading && state.overview == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CoffeeBrown) }
            return@Scaffold
        }
        if (state.error != null && state.overview == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Lỗi: ${state.error}", color = AccentRed, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadAll() }, colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown)) { Text("Thử lại") }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Quick Stats ───
            item { StatsGrid(state.overview, onUsers = onOpenUsers, onProducts = onOpenProduct, onOrders = onOpenOrders) }

            // ─── Quick Nav ───
            item { QuickNavRow(onOpenCategory, onOpenProduct, onOpenUsers, onOpenOrders, onOpenPromotions, onOpenReviews) }

            // ─── Revenue Chart ───
            item { RevenueSection(state, onToggle = { viewModel.switchRevenueGroupBy(it) }) }

            // ─── Top Products ───
            if (state.topProducts.isNotEmpty()) {
                item { SectionTitle("Sản phẩm bán chạy") }
                items(state.topProducts.take(5)) { TopProductItem(it) }
            }

            // ─── Recent Orders ───
            if (state.recentOrders.isNotEmpty()) {
                item { SectionTitle("Đơn hàng gần đây") }
                items(state.recentOrders.take(5)) { RecentOrderItem(it) }
            }

            // ─── Recent Reviews ───
            if (state.recentReviews.isNotEmpty()) {
                item { SectionTitle("Đánh giá gần đây") }
                items(state.recentReviews.take(5)) { RecentReviewItem(it) }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun StatsGrid(overview: DashboardOverviewDto?, onUsers: () -> Unit, onProducts: () -> Unit, onOrders: (String) -> Unit) {
    val o = overview ?: return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Người dùng", o.totalUsers.toString(), Icons.Default.People, AccentBlue, onUsers)
            StatCard(Modifier.weight(1f), "Sản phẩm", o.totalProducts.toString(), Icons.Default.Inventory2, CoffeeLight, onProducts)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Tổng đơn", o.totalOrders.toString(), Icons.Default.ShoppingCart, AccentOrange) { onOrders("PENDING") }
            StatCard(Modifier.weight(1f), "Chờ xác nhận", o.pendingOrders.toString(), Icons.Default.HourglassTop, Color(0xFFFFCC00)) { onOrders("PENDING") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Hoàn tất", o.completedOrders.toString(), Icons.Default.CheckCircle, AccentGreen) { onOrders("COMPLETED") }
            StatCard(Modifier.weight(1f), "Đã huỷ", o.cancelledOrders.toString(), Icons.Default.Cancel, AccentRed) { onOrders("CANCELLED") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Doanh thu hôm nay", formatMoney(o.todayRevenue), Icons.Default.TrendingUp, AccentGreen, {})
            StatCard(Modifier.weight(1f), "Doanh thu tháng", formatMoney(o.monthRevenue), Icons.Default.CalendarMonth, AccentBlue, {})
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        shadowElevation = 2.dp,
        onClick = onClick
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.12f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CoffeeBrown)
                Text(label, fontSize = 11.sp, color = SubText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun QuickNavRow(
    onCategory: () -> Unit, onProduct: () -> Unit, onUsers: () -> Unit,
    onOrders: (String) -> Unit, onPromotions: () -> Unit, onReviews: () -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item { QuickNavChip("Danh mục", Icons.Default.Category, Color(0xFF0070EB), onCategory) }
        item { QuickNavChip("Sản phẩm", Icons.Default.Inventory2, Color(0xFF2E7D32), onProduct) }
        item { QuickNavChip("Người dùng", Icons.Default.People, AccentBlue, onUsers) }
        item { QuickNavChip("Đơn hàng", Icons.Default.ReceiptLong, AccentOrange) { onOrders("PENDING") } }
        item { QuickNavChip("Khuyến mãi", Icons.Default.Discount, Color(0xFF6A1B9A), onPromotions) }
        item { QuickNavChip("Đánh giá", Icons.Default.Star, Color(0xFFFFB300), onReviews) }
    }
}

@Composable
private fun QuickNavChip(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun RevenueSection(state: DashboardUiState, onToggle: (String) -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardBg, shadowElevation = 2.dp) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Biểu đồ doanh thu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CoffeeBrown)
                Row {
                    FilterChip(selected = state.revenueGroupBy == "DAY", onClick = { onToggle("DAY") },
                        label = { Text("Ngày", fontSize = 12.sp) }, modifier = Modifier.height(32.dp))
                    Spacer(Modifier.width(6.dp))
                    FilterChip(selected = state.revenueGroupBy == "MONTH", onClick = { onToggle("MONTH") },
                        label = { Text("Tháng", fontSize = 12.sp) }, modifier = Modifier.height(32.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            if (state.revenuePoints.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("Chưa có dữ liệu doanh thu", color = SubText, fontSize = 13.sp)
                }
            } else {
                // Simple bar chart representation
                val maxRevenue = state.revenuePoints.maxOfOrNull { it.revenue.toDouble() } ?: 1.0
                LazyRow(Modifier.fillMaxWidth().height(140.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
                    items(state.revenuePoints) { point ->
                        val ratio = if (maxRevenue > 0) (point.revenue.toDouble() / maxRevenue).toFloat() else 0f
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
                            Text(formatMoney(point.revenue), fontSize = 8.sp, color = SubText, maxLines = 1)
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier.fillMaxWidth(0.6f).height((100 * ratio).coerceAtLeast(4f).dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(CoffeeLight)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(point.label, fontSize = 9.sp, color = SubText, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = CoffeeBrown, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun TopProductItem(item: TopProductDto) {
    Surface(shape = RoundedCornerShape(14.dp), color = CardBg, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val imgUrl = item.imageUrl.toFullImageUrl()
            AsyncImage(model = imgUrl, contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = CoffeeBrown)
            }
            Text("${item.totalSold} đã bán", fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun RecentOrderItem(order: OrderDto) {
    val statusColor = when (order.status.uppercase()) {
        "COMPLETED" -> AccentGreen; "PENDING" -> AccentOrange; "CANCELLED" -> AccentRed; else -> AccentBlue
    }
    val statusLabel = when (order.status.uppercase()) {
        "COMPLETED" -> "Hoàn tất"; "PENDING" -> "Chờ xác nhận"; "CANCELLED" -> "Đã huỷ"
        "CONFIRMED" -> "Đã xác nhận"; "DELIVERING" -> "Đang giao"; else -> order.status
    }
    Surface(shape = RoundedCornerShape(14.dp), color = CardBg, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), shape = CircleShape, color = statusColor.copy(0.12f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Receipt, null, tint = statusColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("#${order.id} - ${order.user?.fullName ?: order.user?.username ?: ""}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(formatMoney(order.totalPrice), fontSize = 13.sp, color = CoffeeBrown, fontWeight = FontWeight.Medium)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(0.12f)) {
                Text(statusLabel, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun RecentReviewItem(review: ReviewDto) {
    Surface(shape = RoundedCornerShape(14.dp), color = CardBg, shadowElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.Top) {
            Surface(Modifier.size(36.dp), shape = CircleShape, color = CoffeeLight.copy(0.15f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(review.username?.take(1)?.uppercase() ?: "?", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CoffeeBrown)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(review.username ?: "Ẩn danh", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(Modifier.width(6.dp))
                    repeat(review.rating.coerceIn(0, 5)) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                    }
                }
                Text(review.productName ?: "", fontSize = 12.sp, color = SubText)
                if (!review.comment.isNullOrBlank()) {
                    Text(review.comment, fontSize = 12.sp, color = Color(0xFF555555), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}
