package com.example.coffeeshopapp.presentation.screen.user.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CoffeeMaker
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Category
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.components.Categories
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.ListItem
import com.example.coffeeshopapp.presentation.components.SearchingTextField
import com.example.coffeeshopapp.presentation.components.BoxItems
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.IconWhatshotColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TextColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel
import com.example.coffeeshopapp.utils.getFullImageUrl
import com.example.coffeeshopapp.utils.toFullImageUrl
import kotlinx.coroutines.launch

@Composable
fun HomeContent(
    viewModel: HomeViewModel = viewModel(),
    categories: List<Category> = emptyList(),
    trendingItems: List<Product> = emptyList(),
    recommendationItems: List<Product> = emptyList(),
    loadingFavorites: Set<String> = emptySet(),
    favorites: Set<String> = emptySet(),
    onFavoriteClick: (String) -> Unit = {},
    openProductDetailScreen: (Product) -> Unit = {},
    onChatBotClick: () -> Unit = {},
    onAddToCartClick: (String, Offset) -> Unit = {_, _ ->}
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val isSearching = viewModel.searchKeyWords.isNotBlank()

    val categoryPositions by remember(filteredProducts, uiState.categories, isSearching) {
        derivedStateOf {
            val positions = mutableMapOf<Long, Int>()
            val initialIdx = if (isSearching) 2 else 5
            var currentIdx = initialIdx

            val grouped = filteredProducts.groupBy { product ->
                uiState.categories.find { it.id == product.categoryId }
            }
            uiState.categories.forEach { category ->
                val productsInCat = grouped[category] ?: emptyList()
                if (productsInCat.isNotEmpty()) {
                    positions[category.id] = currentIdx
                    currentIdx += 1 + productsInCat.size
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

                Image(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(80.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchingTextField(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth(.85f)
                )

                IconButton(
                    onClick = onChatBotClick
                ) {
                    Image(painter = painterResource(R.drawable.btn_chatbot), contentDescription = null, modifier = Modifier.fillMaxSize())
                }
            }
        }

        if (!isSearching) {
            item {
                TitleSmall("Danh Mục")
                CommonSpace(6.dp)
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
                CommonSpace(6.dp)
                BoxItems(
                    items = trendingItems,
                    loadingFavorites = loadingFavorites,
                    favorites = favorites,
                    onFavoriteClick = onFavoriteClick,
                    onAddToCartClick = onAddToCartClick,
                    openProductDetailScreen = openProductDetailScreen
                )
            }

            item {
                TitleSmall(
                    "Dành cho bạn",
                    icon = Icons.Default.Favorite,
                    iconColor = Color(0xFFC38EB4)
                )
                CommonSpace(6.dp)
                BoxItems(
                    items = recommendationItems,
                    loadingFavorites = loadingFavorites,
                    favorites = favorites,
                    onFavoriteClick = onFavoriteClick,
                    onAddToCartClick = onAddToCartClick,
                    openProductDetailScreen = openProductDetailScreen
                )
            }

            item {
                TitleSmall(
                    titleContent = "Danh sách sản phẩm",
                    icon = Icons.Default.CoffeeMaker,
                    iconColor = CoffeeTextColor
                )
                CommonSpace(12.dp)
            }
        } else {
            item {
                TitleSmall("Kết quả tìm kiếm cho \"${viewModel.searchKeyWords}\"")
                CommonSpace(12.dp)
            }
        }

        uiState.categories.forEach { category ->
            val products = groupedProducts[category] ?: emptyList()
            if (products.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 20.sp,
                            fontFamily = k2d,
                            fontWeight = FontWeight.Normal,
                            color = TextColor,
                        )
                        if (category.imageUrl != null) {
                            AsyncImage(
                                model = category.imageUrl.toFullImageUrl(),
                                contentDescription = category.name,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.loading_img),
                                error = painterResource(R.drawable.error_img)
                            )
                        }
                    }
                    CommonSpace(12.dp)
                }

                items(products, key = { it.id }) { product ->
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

    }
}
