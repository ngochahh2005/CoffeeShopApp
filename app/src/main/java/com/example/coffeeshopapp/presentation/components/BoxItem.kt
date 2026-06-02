package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun BoxItem(
    product: Product,
    onFavoriteClick: (String) -> Unit = {},
    onAddToCartClick: (String, Offset) -> Unit = {_, _ -> },
    modifier: Modifier = Modifier,
    openProductDetailScreen: (Product) -> Unit = {},
    isLoading: Boolean = false,
) {
    var itemOffset by remember { mutableStateOf(Offset.Zero) }

    Surface(
        modifier = modifier
            .width(180.dp)
            .height(260.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp))
            .clickable { openProductDetailScreen(product) },
        color = Color.White,
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column() {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    AsyncImage(
                        model = product.getFullImageUrl(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.loading_img),
                        error = painterResource(R.drawable.error_img)
                    )

                    // hot/trending icon
                    if (product.isTrending) {
                        Surface(
                            color = Color(0xFFFFEFEF).copy(alpha = 0.9f),
                            shape = CircleShape,
                            modifier = Modifier.padding(8.dp).size(32.dp).align(Alignment.TopStart)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Whatshot,
                                    null,
                                    tint = IconWhatshotColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // favorite button
                    Surface(
                        color = Color.White.copy(alpha = 0.8f),
                        shape = CircleShape,
                        modifier = Modifier.padding(8.dp).size(32.dp).align(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { if (!isLoading) onFavoriteClick(product.id) }) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFFC38EB4)
                                )
                            } else {
                                Icon(
                                    imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (product.isFavorite) Color(0xFFC38EB4) else Color(0xFFA89FBA),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // rating
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(topStart = 12.dp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                product.rating.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // info
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = product.name,
                        fontFamily = k2d,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    CommonSpace(6.dp)

                    Text(
                        text = "${product.reviewers} đánh giá",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = k2d,
                        fontWeight = FontWeight.Normal
                    )

                    CommonSpace(6.dp)
                    Text(
                        text = "${product.price.formatGrouped()}đ",
                        color = TextColor,
                        fontFamily = k2d,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
            // add button
            Surface(
                modifier = Modifier
                    .height(36.dp)
                    .width(40.dp)
                    .onGloballyPositioned { itemOffset = it.positionInRoot() }
                    .clickable { onAddToCartClick(product.id, itemOffset) }
                    .align(Alignment.BottomEnd),
                shape = RoundedCornerShape(topStart = 18.dp, bottomEnd = 28.dp),
                color = CoffeeTextColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
@Preview
fun BoxItemPreview() {
    CoffeeShopAppTheme() {
        BoxItem(
            product = Product(id = "1", name = "Mocha Espresso Macchiato", price = 45000, rating = 4.8, reviewers = 1250),
            onFavoriteClick = {}
        )
    }
}