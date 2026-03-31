package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.data.CoffeeItem
import com.example.coffeeshopapp.data.trendingCoffeeList
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor2
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconStarRateColor
import com.example.coffeeshopapp.presentation.theme.IconWhatshotColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.theme.k2d

@Composable
fun TrendingItem(
    coffee: CoffeeItem,
    onFavoriteClick: (String) -> Unit,
    onAddToCartClick: (String) -> Unit,
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
                Image(
                    painter = painterResource(coffee.icon),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).fillMaxSize(.9f)
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
                        )
                    }

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
                            fontFamily = k2d
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = coffee.getPrice(),
                        color = TitleSmallColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                IconButton(
                    onClick = { onFavoriteClick(coffee.id) },
                    modifier = Modifier.size(24.dp).align(Alignment.TopEnd).padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = if (coffee.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = PlaceHolderColor
                    )
                }
            }
        }

        Box(modifier = Modifier
            .size(36.dp, 30.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp))
            .background(color = CoffeeTextColor)
            .align(Alignment.BottomEnd)
            .clickable {
                onAddToCartClick(coffee.id)
            }
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = CardBackgroundColor)
        }
    }

}

@Composable
fun TrendingItems(
    items: List<CoffeeItem> = trendingCoffeeList,
    onFavoriteClick: (String) -> Unit,
    onAddToCartClick: (String) -> Unit
) {
    val trendingListState = remember {
        val list = mutableStateListOf<CoffeeItem>()
        list.addAll(trendingCoffeeList)
        list
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        items(items, key = { it.id }) { coffee ->
            TrendingItem(
                coffee = coffee,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick
            )
        }
    }
}