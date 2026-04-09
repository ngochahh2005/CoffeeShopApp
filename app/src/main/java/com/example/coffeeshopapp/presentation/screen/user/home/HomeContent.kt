package com.example.coffeeshopapp.presentation.screen.user.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.coffeeCategories
import com.example.coffeeshopapp.data.model.entity.Category
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.trendingCoffeeList
import com.example.coffeeshopapp.presentation.components.Categories
import com.example.coffeeshopapp.presentation.components.SearchingTextField
import com.example.coffeeshopapp.presentation.components.TrendingItems
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconWhatshotColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel

@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel(),
    categories: List<Category>,
    trendingItems: List<Product>,
    loadingFavorites: Set<String> = emptySet(),
    onCategoryClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onAddToCartClick: (String, Offset) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 56.dp)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.titleMedium.toSpanStyle()
                                .copy(color = LabelColor)
                        ) {
                            append("Enjoy your\nMorning ")
                        }
                        withStyle(
                            style = MaterialTheme.typography.titleMedium.toSpanStyle()
                                .copy(color = CoffeeTextColor)
                        ) {
                            append("Coffee!!")
                        }

                    },
                    textAlign = TextAlign.Left,
                )

                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = LabelColor,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchingTextField(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                )
            }
        }

        item {
            TitleSmall("Danh Mục", onClickSeeMore = {})
            Categories(categories = categories, onCategoryClick = onCategoryClick)
        }

        item {
            TitleSmall(
                "Xu Hướng",
                onClickSeeMore = {},
                icon = Icons.Default.Whatshot,
                iconColor = IconWhatshotColor
            )
            TrendingItems(
                items = trendingItems,
                loadingFavorites =
                    loadingFavorites,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick
            )
        }
    }
}

@Composable
private fun TitleSmall(
    titleContent: String,
    onClickSeeMore: () -> Unit,
    icon: ImageVector? = null,
    iconColor: Color = LabelColor,
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 18.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(modifier = Modifier
            .wrapContentSize()
            .align(Alignment.CenterStart), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = titleContent,
                color = TitleSmallColor,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier,
            )

            if (icon != null)
                Icon(
                    imageVector = icon,
                    tint = iconColor,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
        }
        TextButton(
            onClick = onClickSeeMore,
            modifier = Modifier.align(Alignment.CenterEnd),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(modifier = Modifier.wrapContentWidth()) {
                Text(
                    text = "Xem thêm",
                    color = PlaceHolderColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PlaceHolderColor,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Bottom)
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun HomeContentPreview() {
    CoffeeShopAppTheme {
        HomeContent(
            categories = coffeeCategories,
            trendingItems = trendingCoffeeList,
            loadingFavorites = setOf("1"),
            onCategoryClick = { id ->
                println("Category $id clicked")
            },
            onFavoriteClick = { id ->
                println("Favorite $id clicked")
            },
            onAddToCartClick = { id, offset ->
                println("Add $id at $offset")
            }
        )
    }
}
