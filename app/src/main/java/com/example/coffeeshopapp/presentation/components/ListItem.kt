package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.coffeeshopapp.presentation.theme.*
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.getFullImageUrl

@Composable
fun ListItem(
    product: Product,
    isLoading: Boolean = false,
    onFavoriteClick: (String) -> Unit,
    openProductDetailScreen: (Product) -> Unit = {},
    onAddToCartClick: (String, Offset) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var itemOffset by remember { mutableStateOf(Offset.Zero) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
//            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
            .clickable { openProductDetailScreen(product) },
        color = CardBackgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image with Aesthetic Frame
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(95.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8F8F8))
            ) {
                AsyncImage(
                    model = product.getFullImageUrl(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.error_img),
                    placeholder = painterResource(R.drawable.loading_img)
                )
                
                // Rating Overlay
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(10.dp))
                        Text(product.rating.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontFamily = k2d,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = TextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${product.reviewers} đánh giá",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal
                )

                Text(
                    text = "${product.price.formatGrouped()}đ",
                    color = TextColor,
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Favorite Button
                IconButton(
                    onClick = { if (!isLoading) onFavoriteClick(product.id) },
                    modifier = Modifier.size(28.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFFC38EB4))
                    } else {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (product.isFavorite) Color(0xFFC38EB4) else Color(0xFFA89FBA),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Premium Add Button
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .onGloballyPositioned { itemOffset = it.positionInRoot() }
                        .clickable { onAddToCartClick(product.id, itemOffset) },
                    shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                    color = CoffeeTextColor,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ListItemPreview() {
    CoffeeShopAppTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            ListItem(
                product = Product(id = "1", name = "Mocha Espresso Macchiato", price = 45000, rating = 4.8, reviewers = 1250),
                onFavoriteClick = {}
            )
        }
    }
}
