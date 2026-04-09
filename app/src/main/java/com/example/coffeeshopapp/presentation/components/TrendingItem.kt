package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.trendingCoffeeList
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor2
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconStarRateColor
import com.example.coffeeshopapp.presentation.theme.IconWhatshotColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.utils.getFullImageUrl

@Composable
fun TrendingItem(
    coffee: Product,
    onFavoriteClick: (String) -> Unit,
    onAddToCartClick: (String, Offset) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier.size(200.dp, 220.dp).clip(RoundedCornerShape(16.dp))
                .background(CardBackgroundColor).padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(.6f)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackgroundColor2)
            ) {
                AsyncImage(
                    model = coffee.getFullImageUrl(),
                    contentDescription = coffee.name,
                    modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.error_img)
                )
            }

            Box(modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 6.dp)) {
                Column(
                    modifier = Modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row() {
                        Icon(
                            Icons.Default.Whatshot,
                            tint = IconWhatshotColor,
                            contentDescription = null
                        )

                        Text(
                            text = coffee.name,
                            color = TitleSmallColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 20.dp)
                        )
                    }

                    CommonSpace(1.dp)

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(
                            Icons.Default.StarRate,
                            tint = IconStarRateColor,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp).align(Alignment.CenterVertically)
                        )
                        Text(
                            text = coffee.rating.toString() + " (" + coffee.reviewers.toString() + " người đánh giá)",
                            color = CoffeeTextColor,
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = k2d,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    CommonSpace(4.dp)

                    Text(
                        text = coffee.getPrice(),
                        color = TitleSmallColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                IconButton(
                    onClick = { if (!isLoading) onFavoriteClick(coffee.id) },
                    modifier = Modifier.size(24.dp).align(Alignment.TopEnd).padding(top = 4.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = Dp(2f),
                            color = PlaceHolderColor
                        )
                    } else {
                        Icon(
                            imageVector = if (coffee.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = PlaceHolderColor
                        )
                    }
                }
            }
        }

        // Nút +
        var itemOffset by remember { mutableStateOf(Offset.Zero) }
        Box(modifier = Modifier
            .size(36.dp, 30.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
            .background(color = CoffeeTextColor)
            .align(Alignment.BottomEnd)
            .onGloballyPositioned { itemOffset = it.positionInRoot() }
            .clickable {
                onAddToCartClick(coffee.id, itemOffset)
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = CardBackgroundColor)
        }
    }

}

@Composable
fun TrendingItems(
    items: List<Product> = trendingCoffeeList,
    loadingFavorites: Set<String> = emptySet(),
    onFavoriteClick: (String) -> Unit,
    onAddToCartClick: (String, Offset) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        items(items, key = { it.id }) { coffee ->
            TrendingItem(
                coffee = coffee,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick,
                isLoading = loadingFavorites.contains(coffee.id)
            )
        }
    }
}

@Composable
@Preview(name = "Trending Item Preview")
fun TrendingItemPreview() {
    CoffeeShopAppTheme {
        TrendingItem(
            coffee = Product(
                id = "1",
                name = "Nước ép giải nhiệt mùa hè",
                price = 35000,
                rating = 4.5,
                reviewers = 100000,
                isFavorite = true
            ),
            onFavoriteClick = {},
            onAddToCartClick = {_, _ ->}
        )
    }
}