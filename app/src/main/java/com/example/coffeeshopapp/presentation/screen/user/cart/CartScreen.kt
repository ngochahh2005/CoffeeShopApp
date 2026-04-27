package com.example.coffeeshopapp.presentation.screen.user.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.viewmodel.CartViewModel
import com.example.coffeeshopapp.utils.formatGrouped

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
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Giỏ hàng (${state.totalQuantity})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TitleSmallColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.items, key = { it.productId }) { item ->
                            ItemofCart(
                                item = item,
                                onIncrease = { viewModel.increaseQuantity(item.productId) },
                                onDecrease = { viewModel.decreaseQuantity(item.productId) },
                                onRemove = { viewModel.removeItem(item.productId) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tổng tiền: ${state.totalAmount.formatGrouped()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TitleSmallColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.clearCart() }
                    ) {
                        Text("Xóa toàn bộ giỏ hàng")
                    }
                }
            }
        }
    }
}

