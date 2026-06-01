package com.example.coffeeshopapp.presentation.screen.user.order

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.viewmodel.OrderHistoryViewModel

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOrderClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    OrderHistoryContent(
        uiState = uiState,
        onBack = onBack,
        onCancelOrder = { orderId -> viewModel.cancelOrder(orderId) },
        onRetryLoad = { viewModel.loadOrders() },
        onOrderClick = onOrderClick,
        onRepayOrder = { orderId ->
            viewModel.repayOrder(orderId) { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        }
    )
}
