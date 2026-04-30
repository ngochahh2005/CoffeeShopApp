package com.example.coffeeshopapp.presentation.screen.user.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.components.FavoriteListItem
import com.example.coffeeshopapp.presentation.components.TrendingItem
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconStarRateColor
import com.example.coffeeshopapp.presentation.theme.LabelColor

@Composable
fun FavoriteContent(
    favorites: List<Product>,
    loadingFavorites: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    onAddToCartClick: (String) -> Unit = {},
    openProductDetailScreen : (Product) -> Unit = {}
) {
    var isGrid by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(12.dp)) {
        FavoriteTitle(isGrid = isGrid, onToggleGrid = {isGrid = !isGrid})

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có sản phẩm yêu thích nào")
            }
        } else {
            if (isGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favorites, key = { it.id }) { product ->
                        TrendingItem(
                            product = product,
                            onFavoriteClick = { id -> onToggleFavorite(id) },
                            onAddToCartClick = { id, _ ->
                                onAddToCartClick(id)
                            },
                            openProductDetailScreen = openProductDetailScreen,
                            isLoading = loadingFavorites.contains(product.id)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites, key = { it.id }) { coffee ->
                        FavoriteListItem (
                            product = coffee,
                            isLoading = loadingFavorites.contains(coffee.id),
                            onFavoriteClick = { id -> onToggleFavorite(id) },
                            openProductDetailScreen = openProductDetailScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteTitle(
    isGrid: Boolean,
    onToggleGrid: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle()
                            .copy(color = LabelColor)
                    ) {
                        append("Ký ức ")
                    }
                    withStyle(
                        style = MaterialTheme.typography.titleMedium.toSpanStyle()
                            .copy(color = CoffeeTextColor)
                    ) {
                        append("vị giác")
                    }
                },
                textAlign = TextAlign.Left,
            )

            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = IconStarRateColor,
                modifier = Modifier.size(32.dp).align(Alignment.CenterVertically)
            )

        }

        Row() {
            IconButton(onClick = onToggleGrid) {
                Icon(
                    imageVector = if (isGrid) Icons.AutoMirrored.Filled.FormatListBulleted else Icons.Default.GridView,
                    contentDescription = null,
                    tint = LabelColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
@Preview(name = "FavouritesScreenPreview", showSystemUi = true, device = Devices.PIXEL_9_PRO_FOLD)
fun FavoriteScreenPreview2() {
    val mockFavorites = listOf(
        Product(id = "1", name = "Cà phê Muối", price = 35000, isFavorite = true),
        Product(id = "2", name = "Bạc Xỉu", price = 29000, isFavorite = true),
        Product(id = "3", name = "Espresso", price = 45000, isFavorite = true)
    )

    CoffeeShopAppTheme {
        FavoriteContent(
            favorites = mockFavorites,
            loadingFavorites = setOf("1")
        )
    }
}

@Composable
@Preview(name = "FavouritesScreenPreview", showSystemUi = true)
fun FavoriteScreenPreview() {
    val mockFavorites = listOf(
        Product(id = "1", name = "Cà phê Muối", price = 35000, isFavorite = true),
        Product(id = "2", name = "Bạc Xỉu", price = 29000, isFavorite = true),
        Product(id = "3", name = "Espresso", price = 45000, isFavorite = true)
    )

    CoffeeShopAppTheme {
        FavoriteContent(
            favorites = mockFavorites,
            loadingFavorites = setOf("1")
        )
    }
}
