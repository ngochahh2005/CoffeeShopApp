package com.example.coffeeshopapp.presentation.screen.user.favorite

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel

@Composable
fun FavouritesScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites: List<Product> = uiState.trendingItems.filter { it.isFavorite }
    val loadingFavorites = uiState.loadingFavorites

    FavoriteContent(
        favorites = favorites,
        loadingFavorites = loadingFavorites,
        onToggleFavorite = { id -> viewModel.toggleFavorite(id) }
    )
}
