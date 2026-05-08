package com.example.coffeeshopapp.presentation.screen.user.favorite

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.utils.getFullImageUrl
import com.example.coffeeshopapp.presentation.screen.user.ProductDetailScreen
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.utils.CartPositionStore
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FavouritesScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites: List<Product> = uiState.favoriteItems
    val loadingFavorites = uiState.loadingFavorites

    val flyX = remember { Animatable(0f) }
    val flyY = remember { Animatable(0f) }
    val flyAlpha = remember { Animatable(0f) }
    val flyScale = remember { Animatable(1f) }
    var flyImageUrl by remember { mutableStateOf<String?>(null) }
    val cartOffset by CartPositionStore.cartOffset.collectAsState()
    val animationJob = remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.flyAnimationEvent.collect { pair ->
            animationJob.value?.cancel()
            animationJob.value = launch {
                val (coffeeId, start) = pair
                val item = uiState.allProduct.find { it.id == coffeeId }
                flyImageUrl = item?.getFullImageUrl()

                flyX.snapTo(start.x)
                flyY.snapTo(start.y)
                flyAlpha.snapTo(1f)
                flyScale.snapTo(1.2f)

                launch {
                    flyX.animateTo(cartOffset.x, tween(600, easing = LinearOutSlowInEasing))
                }
                launch {
                    flyY.animateTo(cartOffset.y, tween(600, easing = FastOutLinearInEasing))
                }
                launch { flyScale.animateTo(0.3f, tween(600)) }
            }

            launch {
                delay(400)
                flyAlpha.animateTo(0f, tween(200))
                flyImageUrl = null
            }
        }
    }

    LaunchedEffect(Unit) { viewModel.loadData(forceRefresh = true) }

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
                    onAddToCartClick = { id, offset -> viewModel.addToCart(id, offset) },
                    openProductDetailScreen = { product ->
                        val fullProduct = uiState.allProduct.find { it.id == product.id } ?: product
                        viewModel.showProduct(fullProduct)
                    }
                )
            }
        }

        val selectedProduct = viewModel.selectedProductId?.let { id ->
            uiState.allProduct.find { it.id == id } ?: uiState.favoriteItems.find { it.id == id } ?: uiState.trendingItems.find { it.id == id }
        }
        if (viewModel.isShowSheet && selectedProduct != null) {
            ProductDetailScreen(
                product = selectedProduct,
                isFavorite = selectedProduct.isFavorite,
                onToggleFavorite = { productId -> viewModel.toggleFavorite(productId) },
                onAddToCartClick = {
                    viewModel.addToCart(selectedProduct)
                },
                onDismiss = { viewModel.onDismiss() }
            )
        }

        flyImageUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        translationX = flyX.value
                        translationY = flyY.value
                        alpha = flyAlpha.value
                        scaleX = flyScale.value
                        scaleY = flyScale.value
                    }
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }
    }
}
