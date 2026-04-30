package com.example.coffeeshopapp.presentation.screen.user.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.screen.user.ProductDetailScreen
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.viewmodel.CartViewModel

@Composable
fun CartScreen(viewModel: CartViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = CoffeeTextColor
                )
            }

            state.items.isEmpty() -> {
                Text(
                    text = "Giỏ hàng đang trống",
                    modifier = Modifier.align(Alignment.Center),
                    color = CoffeeTextColor
                )
            }

            else -> {
                CartContent(
                    state = state,
                    onToggleSelection = { viewModel.toggleSelection(it) },
                    onIncrease = { viewModel.increaseQuantity(it) },
                    onDecrease = { viewModel.decreaseQuantity(it) },
                    onRemove = { viewModel.removeItem(it) },
                    openOrderScreen = {},
                    openProductDetailScreen = { productId ->
                        viewModel.showProductDetail(productId)
                    }
                )
            }
        }

        if (viewModel.isShowSheet && viewModel.selectedProduct != null) {
            ProductDetailScreen(
                product = viewModel.selectedProduct!!,
                onAddToCartClick = {
                    viewModel.selectedProduct?.let { viewModel.addToCart(it) }
                },
                onDismiss = { viewModel.onDismiss() }
            )
        }
    }
}
