package com.example.coffeeshopapp.presentation.screen.user.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.screen.user.ProductDetailScreen
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel

@Composable
fun FavouritesScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites: List<Product> = uiState.trendingItems.filter { it.isFavorite }
    val loadingFavorites = uiState.loadingFavorites

    LaunchedEffect(Unit) { viewModel.loadData() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        when {
            uiState.isLoading && favorites.isEmpty() -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = CoffeeTextColor
                )
            }

            !uiState.isLoading && favorites.isEmpty() -> {
                Text("Chưa có sản phẩm yêu thích!", color = CoffeeTextColor, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.bodyMedium)
            }

            uiState.trendingItems.isEmpty() && uiState.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Đã xảy ra lỗi: ${uiState.error}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadData() }) {
                        Text("Thử lại")
                    }
                }
            }

            else -> {
                FavoriteContent(
                    favorites = favorites,
                    loadingFavorites = loadingFavorites,
                    onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                    onAddToCartClick = { id -> viewModel.addToCartById(id) },
                    openProductDetailScreen = { product ->
                        viewModel.showProduct(product)
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
