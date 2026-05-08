package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconStarRateColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.utils.getFullImageUrl

@Composable
fun ListItem(
    product: Product,
    isLoading: Boolean = false,
    onFavoriteClick: (String) -> Unit,
    openProductDetailScreen: (Product) -> Unit = {},
    onAddToCartClick: (String, Offset) -> Unit = {_, _ ->},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundColor)
            .clickable {
                openProductDetailScreen(product)
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = product.getFullImageUrl(),
            contentDescription = product.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.error_img),
            placeholder = painterResource(R.drawable.loading_img)
        )

        Column(modifier = Modifier
            .weight(1f)
            .padding(vertical = 4.dp)
            .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                color = TitleSmallColor,
                style = MaterialTheme.typography.labelLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Icon(
                    Icons.Default.Star,
                    tint = IconStarRateColor,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp).align(Alignment.CenterVertically)
                )
                Text(
                    text = product.rating.toString() + " (" + product.reviewers.toString() + " người đánh giá)",
                    color = CoffeeTextColor,
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = product.getPrice(),
                color = TitleSmallColor,
                fontFamily = k2d,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                if (!isLoading) onFavoriteClick(product.id) },
                modifier = Modifier.padding(top = 8.dp).size(20.dp).align(Alignment.CenterHorizontally)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = PlaceHolderColor
                    )
                }
            }

            var itemOffset by remember { mutableStateOf(Offset.Zero) }
            Box(modifier = Modifier
                .size(36.dp, 30.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                .background(color = CoffeeTextColor)
                .onGloballyPositioned { itemOffset = it.positionInRoot() }
                .clickable {
                    onAddToCartClick(product.id, itemOffset)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = CardBackgroundColor)
            }
        }
    }
}

@Composable
@Preview(name = "Favorite List Preview")
fun FavoriteListItemPreview() {
    CoffeeShopAppTheme {
        ListItem(
            product = Product(id = "1", name = "Cà phê Muối", price = 35000, isFavorite = true),
            onFavoriteClick = {},

        )
    }
}