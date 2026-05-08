package com.example.coffeeshopapp.presentation.screen.user.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.entity.Category
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.components.Categories
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.ListItem
import com.example.coffeeshopapp.presentation.components.SearchingTextField
import com.example.coffeeshopapp.presentation.components.TrendingItems
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconWhatshotColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel(),
    categories: List<Category>,
    trendingItems: List<Product>,
    loadingFavorites: Set<String> = emptySet(),
    favorites: Set<String> = emptySet(),
    onCategoryClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    openProductDetailScreen: (Product) -> Unit = {},
    onAddToCartClick: (String, Offset) -> Unit
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    


    val categoryPositions by remember {
        derivedStateOf {
            val positions = mutableMapOf<Long, Int>()
            var currentIdx = 4
            val grouped = uiState.allProduct.groupBy { product ->
                uiState.categories.find { it.id == product.categoryId }?.name
            }
            uiState.categories.forEach { category ->
                val productsInCat = grouped[category.name] ?: emptyList()
                if (productsInCat.isNotEmpty()) {
                    positions[category.id] = currentIdx
                    currentIdx += 1 + productsInCat.size // 1 cho tiêu đề + n cho sản phẩm
                }
            }
            positions to grouped
        }
    }

    val (positionsMap, groupedProducts) = categoryPositions

    val scrollToCategory = { clickedCategoryId: Long ->
        coroutineScope.launch {
            positionsMap[clickedCategoryId]?.let { targetIndex ->
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 56.dp)
    ) {
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
            TitleSmall("Danh Mục")
            Categories(
                categories = categories,
                onCategoryClick = { categoryId ->
                    scrollToCategory(categoryId)
                }
            )
        }

        item {
            TitleSmall(
                "Xu Hướng",
                icon = Icons.Default.Whatshot,
                iconColor = IconWhatshotColor
            )
            TrendingItems(
                items = trendingItems,
                loadingFavorites =
                    loadingFavorites,
                favorites = favorites,
                onFavoriteClick = onFavoriteClick,
                onAddToCartClick = onAddToCartClick,
                openProductDetailScreen = openProductDetailScreen
            )
        }

        item {
            TitleSmall("Danh sách sản phẩm")
            CommonSpace(12.dp)
        }
        groupedProducts.forEach { (categoryName, products) ->
            item {
                Text(
                    text = categoryName ?: "Khác",
                    fontSize = 20.sp,
                    fontFamily = k2d,
                    fontWeight = FontWeight.Bold,
                    color = PlaceHolderColor,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                CommonSpace(12.dp)
            }

            items(products, key = {it.id}) { product ->
                ListItem(
                    product = product,
                    isLoading = loadingFavorites.contains(product.id),
                    onFavoriteClick = onFavoriteClick,
                    onAddToCartClick = onAddToCartClick,
                    openProductDetailScreen = openProductDetailScreen,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                CommonSpace(12.dp)
            }
        }

    }
}

@Composable
private fun listProduct(
    categoryName: String? = null,
    products: List<Product> = emptyList()
) {
    Column (modifier = Modifier.fillMaxWidth()) {

    }
}

@Composable
private fun TitleSmall(
    titleContent: String,
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
    }
}

@Composable
@Preview(showSystemUi = true)
fun HomeContentPreview() {
    CoffeeShopAppTheme {
        listProduct()
    }
}
