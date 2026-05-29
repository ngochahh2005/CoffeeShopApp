package com.example.coffeeshopapp.presentation.screen.user.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.coffeeshopapp.data.model.dto.OrderItemDto
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.viewmodel.OrderDetailViewModel

@Composable
fun OrderDetailScreen(
    orderId: Long,
    onBack: () -> Unit,
    onReviewClick: (List<OrderItemDto>) -> Unit = {},
    viewModel: OrderDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadOrderDetail(orderId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = TitleColor)
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
                OrderDetailContent(
                    order = uiState.order!!,
                    modifier = Modifier.padding(padding),
                    isLocalReviewed = uiState.isLocalReviewed,
                    onReviewClick = {items -> onReviewClick(items)}
                )
            }
        }
    }
}
