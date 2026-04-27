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
import androidx.compose.material.icons.filled.AddShoppingCart
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
import androidx.compose.ui.text.style.TextAlign
import com.example.coffeeshopapp.utils.getFullImageUrl
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.rememberScreenInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onAddToCartClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Surface (
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Box(Modifier.size(width = 32.dp, height = 4.dp))
            }
        },
        scrimColor = Color.Black.copy(alpha = 0.4f),
        containerColor = BackgroundColor,
    ) {
        Box(modifier = Modifier
            .fillMaxHeight(0.85f)
            .background(BackgroundColor)
        ) {
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

                    var isFavorite by remember { mutableStateOf(product.isFavorite) }

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
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
                        color = CoffeeTextColor,
                        textAlign = TextAlign.Justify
                    )
                }

                CommonSpace(52.dp)
            }

            // Button Add to cart
            FloatingActionButton (
                onClick = { onAddToCartClick() },
                modifier = Modifier
                    .padding(end = 24.dp, bottom = 52.dp)
                    .align(Alignment.BottomEnd)
                    .size(48.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = PlaceHolderColor,
                contentColor = BackgroundColor
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = null)
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ProductDetailScreenPreview() {
    CoffeeShopAppTheme {
        ProductDetailScreen(
            product = Product("1", "Sinh tố bơ", 25000, imageUrl = "", description = "Bơ sáp Daklak xay nhuyễn\nHiệu ứng phụ là sự thay đổi về trạng thái của ứng dụng diễn ra bên ngoài phạm vi của một hàm có khả năng kết hợp. Do vòng đời và các thuộc tính của thành phần kết hợp như các thành phần kết hợp lại không thể đoán trước, việc thực thi các thành phần kết hợp lại theo thứ tự khác nhau hoặc các thành phần kết hợp có thể bị loại bỏ, nên thành phần kết hợp tốt nhất là không có hiệu ứng phụ .\n" +
                    "\n" +
                    "Tuy nhiên, đôi khi cũng cần có các hiệu ứng phụ, chẳng hạn để kích hoạt các sự kiện một lần như hiển thị thanh thông báo nhanh hoặc điều hướng đến một màn hình khác trong điều kiện trạng thái nhất định. Các hành động này nên được gọi từ một môi trường được kiểm soát và có nhận thức về vòng đời của thành phần kết hợp đó. Trong trang này, bạn sẽ tìm hiểu về một số hiệu ứng phụ khác của API Jetpack Compose."),
        )
    }
}