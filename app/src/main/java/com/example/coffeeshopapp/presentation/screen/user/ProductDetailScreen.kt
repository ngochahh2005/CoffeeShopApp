package com.example.coffeeshopapp.presentation.screen.user

import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.coffeeshopapp.utils.getFullImageUrl
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.rememberScreenInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    isFavorite: Boolean = product.isFavorite,
    onToggleFavorite: (String) -> Unit = {},
    onAddToCartClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
//    ModalBottomSheet(
//        onDismissRequest = {
//            onDismiss()
//        },
//        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
//        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
//        dragHandle = {
//            Surface (
//                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
//                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
//                shape = MaterialTheme.shapes.extraLarge
//            ) {
//                Box(Modifier.size(width = 32.dp, height = 4.dp))
//            }
//        },
//        scrimColor = Color.Black.copy(alpha = 0.4f),
//        containerColor = BackgroundColor,
//    ) {
        Box(modifier = Modifier.fillMaxHeight(0.85f).background(BackgroundColor)) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(24.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = product.getFullImageUrl(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.error_img),
                    contentDescription = product.name,
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(4.dp)
                        .fillMaxWidth()
                        .height(rememberScreenInfo().logoHeight),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = CoffeeTextColor
                    )

                    IconButton(
                        onClick = { onToggleFavorite(product.id) },
                        modifier = Modifier.align(Alignment.CenterVertically).size(36.dp)
                    ) {
                        if (isFavorite) Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = PlaceHolderColor
                        )
                        else Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = PlaceHolderColor
                        )
                    }
                }

                // Price
                Text(
                    text = product.getPrice(),
                    style = MaterialTheme.typography.titleSmall,
                    color = CoffeeTextColor
                )

                // Description
                if (product.description.isNotBlank()) {
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CoffeeTextColor
                    )
                }

                // Button Add
                FloatingActionButton(
                    onClick = { onAddToCartClick() },
                    modifier = Modifier.wrapContentSize(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Button(
                onClick = { onAddToCartClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .padding(24.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
//    }
}

@Composable
@Preview(showSystemUi = true)
fun ProductDetailScreenPreview() {
    CoffeeShopAppTheme {
        ProductDetailScreen(
            product = Product("1", "Sinh tố bơ", 25000, imageUrl = "", description = "Bơ sáp Daklak xay nhuyễn"),
        )
    }
}