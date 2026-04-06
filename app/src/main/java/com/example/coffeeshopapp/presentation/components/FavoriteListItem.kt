package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.utils.getFullImageUrl

@Composable
fun FavoriteListItem(
    coffee: Product,
    isLoading: Boolean = false,
    onFavoriteClick: (String) -> Unit,
    onAddToCartClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackgroundColor)
            .clickable { /* mở chi tiết */ }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = coffee.getFullImageUrl(),
            contentDescription = coffee.name,
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.error_img),
            placeholder = painterResource(R.drawable.loading_img)
        )

        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp)
        ) {
            Text(
                text = coffee.name,
                fontWeight = FontWeight.Bold,
                color = TitleSmallColor,
                style = MaterialTheme.typography.bodyMedium
            )
            CommonSpace(4.dp)
            Text(text = coffee.getPrice(), color = CoffeeTextColor)
        }

        IconButton(onClick = {
            if (!isLoading) onFavoriteClick(coffee.id) }
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
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

@Composable
@Preview(name = "Favorite List Preview")
fun FavoriteListItemPreview() {
    CoffeeShopAppTheme {
        FavoriteListItem(
            coffee = Product(id = "1", name = "Cà phê Muối", price = 35000, isFavorite = true),
            onFavoriteClick = {},
            onAddToCartClick = {}
        )
    }
}