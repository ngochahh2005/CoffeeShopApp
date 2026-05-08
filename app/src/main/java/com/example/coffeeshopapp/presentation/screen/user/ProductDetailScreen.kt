package com.example.coffeeshopapp.presentation.screen.user

import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.example.coffeeshopapp.utils.getFullImageUrl
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.theme.pacifico
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
        Column(
            modifier = Modifier
                .fillMaxHeight(0.85f)
                .background(BackgroundColor)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Header(product)

            Image(product)

            CommonSpace(8.dp)
            Text(
                text = "GIỚI THIỆU",
                fontFamily = pacifico,
                modifier = Modifier.padding(horizontal = 20.dp),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = PlaceHolderColor
            )
            Text(
                text = product.description,
                fontFamily = pacifico,
                modifier = Modifier.padding(horizontal = 20.dp),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Justify,
                color = PlaceHolderColor
            )
        }
    }
}

@Composable
private fun Header(
    product: Product,
    isFavorite: Boolean = product.isFavorite,
    onToggleFavorite: (String) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp)) {
        Text(
            text = "Chi tiết sản phẩm",
            fontFamily = k2d,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TitleColor,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = { onToggleFavorite(product.id) },
            modifier = Modifier.size(20.dp)
        ) { }
        if (isFavorite) Icon(Icons.Default.Favorite, tint = LabelColor, contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd))
        else Icon(Icons.Default.FavoriteBorder, tint = LabelColor, contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun Image(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(rememberScreenInfo().logoHeight)
    ) {
        AsyncImage(
            model = product.getFullImageUrl(),
            placeholder = painterResource(R.drawable.loading_img),
            error = painterResource(R.drawable.error_img),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                BackgroundColor.copy(.65f),
                                BackgroundColor
                            )
                        )
                    )
                },
            contentScale = ContentScale.Crop,
        )

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).align(Alignment.BottomStart)) {
            Row(modifier = Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD79900), modifier = Modifier.size(14.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.bodySmall.toSpanStyle()
                                .copy(color = Color(0xff60417E))
                        ) {
                            append(product.rating.toString())
                        }
                        withStyle(
                            style = MaterialTheme.typography.bodySmall.toSpanStyle()
                                .copy(color = Color(0xff60417E))
                        ) {
                            append(" (${product.reviewers} đánh giá)")
                        }
                    }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(
                    text = product.name,
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    color = TitleColor,
                    softWrap = true,
                    modifier = Modifier.fillMaxWidth(.7f)
                )

                Text(
                    text = product.getPrice(),
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    color = TitleColor,
                    lineHeight = 36.sp
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ProductDetailScreenPreview() {
    CoffeeShopAppTheme {
        ProductDetailScreen(
            product = Product("1", "Trà sữa trân châu đường đen", 25000, imageUrl = "", description = "Bơ sáp Daklak xay nhuyễn\nHiệu ứng phụ là sự thay đổi về trạng thái của ứng dụng diễn ra bên ngoài phạm vi của một hàm có khả năng kết hợp. Do vòng đời và các thuộc tính của thành phần kết hợp như các thành phần kết hợp lại không thể đoán trước, việc thực thi các thành phần kết hợp lại theo thứ tự khác nhau hoặc các thành phần kết hợp có thể bị loại bỏ, nên thành phần kết hợp tốt nhất là không có hiệu ứng phụ."))
    }
}