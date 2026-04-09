package com.example.coffeeshopapp.presentation.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AdminModule(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    onOpenCategory: () -> Unit
) {
    val modules = listOf(
        AdminModule("Danh mục", "Quản lý danh mục", Icons.Default.Category, Color(0xFF0070EB), onOpenCategory),
        AdminModule("Sản phẩm", "Quản lý sản phẩm", Icons.Default.Inventory2, Color(0xFF2E7D32), {}),
        AdminModule("Đơn hàng", "Quản lý đơn hàng", Icons.Default.ReceiptLong, Color(0xFFEF6C00), {}),
        AdminModule("Cấu hình", "Thiết lập hệ thống", Icons.Default.Settings, Color(0xFF6A1B9A), {})
    )

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color(0xFF3F2A1D))
                Text(
                    text = " Admin Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3F2A1D)
                )
            }
            Text(
                text = "Truy cập nhanh các module quản trị",
                color = Color(0xFF7B7672),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            }

            items(modules) { module ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = module.onClick),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = module.color.copy(alpha = 0.14f)
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Icon(module.icon, contentDescription = null, tint = module.color)
                            }
                        }
                        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(module.title, fontWeight = FontWeight.Bold, color = Color(0xFF2B2B2B))
                            Text(module.description, color = Color(0xFF7B7672), fontSize = 13.sp)
                        }
                        Text("Mở", color = module.color, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
