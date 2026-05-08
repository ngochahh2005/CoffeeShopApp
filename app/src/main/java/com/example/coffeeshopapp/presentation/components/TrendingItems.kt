package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.data.model.entity.Product

@Composable
fun TrendingItems(
    items: List<Product> = emptyList(),
    loadingFavorites: Set<String> = emptySet(),
    favorites: Set<String> = emptySet(),
    onFavoriteClick: (String) -> Unit,
    openProductDetailScreen: (Product) -> Unit,
    onAddToCartClick: (String, Offset) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        items(items, key = { it.id }) { product ->
            val rendered = product.copy(isFavorite = favorites.contains(product.id))
            BoxItem(
                product = rendered,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick,
                openProductDetailScreen = openProductDetailScreen,
                isLoading = loadingFavorites.contains(product.id)
            )
        }
    }
}