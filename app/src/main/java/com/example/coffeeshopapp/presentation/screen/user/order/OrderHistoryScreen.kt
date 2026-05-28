package com.example.coffeeshopapp.presentation.screen.user.order

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.viewmodel.OrderHistoryViewModel

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel(),
    onBack: () -> Unit = {},
    onReviewClick: (productId: Long, productName: String, size: String?, imageUrl: String?) -> Unit = {_, _, _, _ ->},
    onOrderClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    OrderHistoryContent(
        uiState = uiState,
        onBack = onBack,
        onReviewClick = onReviewClick,
        onCancelOrder = {orderId -> viewModel.cancelOrder(orderId)},
        onRetryLoad = {viewModel.loadOrders()},
        onOrderClick = onOrderClick
    )
}