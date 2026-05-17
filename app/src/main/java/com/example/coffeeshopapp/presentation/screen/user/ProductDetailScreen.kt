package com.example.coffeeshopapp.presentation.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.dto.ProductSizeDto
import com.example.coffeeshopapp.data.model.dto.ToppingDto
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.MainButtonColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.theme.pacifico
import com.example.coffeeshopapp.presentation.theme.rememberScreenInfo
import com.example.coffeeshopapp.utils.getFullImageUrl
import com.example.coffeeshopapp.utils.isActiveResolved
import com.example.coffeeshopapp.utils.toFullImageUrl
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    isFavorite: Boolean = product.isFavorite,
    onToggleFavorite: (String) -> Unit = {},
    onAddToCartClick: (Product, Int) -> Unit = { _, _ -> },
    onDismiss: () -> Unit = {}
) {
    val availableSizes = product.sizes.sortedWith(compareBy { sizeOrder(it.sizeName) })
    var selectedSizeName by rememberSaveable(product.id) {
        mutableStateOf(availableSizes.firstOrNull()?.sizeName)
    }
    val selectedSize = availableSizes.firstOrNull { it.sizeName == selectedSizeName }

    var quantityText by rememberSaveable(product.id) { mutableStateOf("1") }
    val quantity = quantityText.toIntOrNull()?.coerceIn(1, 999) ?: 1
    var showToppingSheet by rememberSaveable(product.id) { mutableStateOf(false) }
    var toppings by remember(product.id) { mutableStateOf<List<ToppingDto>>(emptyList()) }
    var selectedToppingIds by remember(product.id) { mutableStateOf<Set<Long>>(emptySet()) }

    val selectedToppings = toppings.filter { it.id in selectedToppingIds }
    val unitPrice = product.price +
            (selectedSize?.priceExtra?.toLong() ?: 0L) +
            selectedToppings.sumOf { it.price.toLong() }
    val configuredProduct = product.copy(
        price = unitPrice,
        selectedSizeName = selectedSize?.sizeName,
        selectedSizePriceExtra = selectedSize?.priceExtra?.toLong() ?: 0L,
        selectedToppings = selectedToppings
    )

    LaunchedEffect(product.id, availableSizes) {
        if (availableSizes.isEmpty()) {
            selectedSizeName = null
        } else if (availableSizes.none { it.sizeName == selectedSizeName }) {
            selectedSizeName = availableSizes.first().sizeName
        }
    }

    LaunchedEffect(product.id) {
        runCatching { NetworkClient.api.getToppings() }
            .onSuccess { response ->
                toppings = response.result
                    ?.filter { it.isActiveResolved() }
                    .orEmpty()
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Surface(
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
                .fillMaxHeight(0.9f)
                .background(BackgroundColor)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Header(product, isFavorite, onToggleFavorite)
            Image(product, unitPrice)

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

            SizeSelector(
                sizes = availableSizes,
                selectedSizeName = selectedSizeName,
                onSizeSelected = { selectedSizeName = it }
            )

            ToppingSelectorButton(
                selectedToppings = selectedToppings,
                onClick = { showToppingSheet = true }
            )

            BottomAddToCartBar(
                quantityText = quantityText,
                onQuantityTextChange = { quantityText = it },
                onDecrease = { quantityText = (quantity - 1).coerceAtLeast(1).toString() },
                onIncrease = { quantityText = (quantity + 1).coerceAtMost(999).toString() },
                onAddToCart = { onAddToCartClick(configuredProduct, quantity) }
            )
        }
    }

    if (showToppingSheet) {
        ToppingBottomSheet(
            toppings = toppings,
            selectedIds = selectedToppingIds,
            onToggleTopping = { toppingId ->
                selectedToppingIds = if (toppingId in selectedToppingIds) {
                    selectedToppingIds - toppingId
                } else {
                    selectedToppingIds + toppingId
                }
            },
            onDismiss = { showToppingSheet = false }
        )
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
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                tint = LabelColor,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Image(product: Product, unitPrice: Long) {
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
                        withStyle(MaterialTheme.typography.bodySmall.toSpanStyle().copy(color = Color(0xff60417E))) {
                            append(product.rating.toString())
                        }
                        withStyle(MaterialTheme.typography.bodySmall.toSpanStyle().copy(color = Color(0xff60417E))) {
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
                    text = formatPrice(unitPrice),
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
private fun SizeSelector(
    sizes: List<ProductSizeDto>,
    selectedSizeName: String?,
    onSizeSelected: (String) -> Unit
) {
    if (sizes.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "CHỌN SIZE",
            fontFamily = k2d,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TitleColor
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sizes.forEach { size ->
                SizeOption(
                    size = size,
                    selected = size.sizeName == selectedSizeName,
                    onClick = { onSizeSelected(size.sizeName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SizeOption(
    size: ProductSizeDto,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val background = if (selected) LabelColor else Color.White
    val borderColor = if (selected) MainButtonColor else PlaceHolderColor.copy(alpha = 0.55f)
    val titleColor = if (selected) Color.White else TitleColor

    Card(
        modifier = modifier
            .height(74.dp)
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(size.sizeName.uppercase(), fontFamily = k2d, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Text(sizeLabel(size.sizeName), fontFamily = k2d, fontSize = 13.sp, color = titleColor)
        }
    }
}

@Composable
private fun ToppingSelectorButton(
    selectedToppings: List<ToppingDto>,
    onClick: () -> Unit
) {
    val subtitle = if (selectedToppings.isEmpty()) {
        "Mặc định: Cream"
    } else {
        selectedToppings.joinToString { it.name }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(68.dp)
            .clickable(onClick = onClick),
        color = CardBackgroundColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("T", fontFamily = k2d, fontWeight = FontWeight.Bold, color = TitleColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Chọn topping", fontFamily = k2d, fontSize = 14.sp, color = TitleColor)
                Text(
                    subtitle,
                    fontFamily = k2d,
                    fontSize = 12.sp,
                    color = LabelColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BottomAddToCartBar(
    quantityText: String,
    onQuantityTextChange: (String) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuantityPicker(
                quantityText = quantityText,
                onQuantityTextChange = onQuantityTextChange,
                onDecrease = onDecrease,
                onIncrease = onIncrease,
                modifier = Modifier.weight(0.78f)
            )

            Button(
                onClick = onAddToCart,
                modifier = Modifier.weight(1.25f).height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TitleColor, contentColor = Color.White)
            ) {
                Text(
                    "THÊM VÀO\nGIỎ HÀNG",
                    fontFamily = k2d,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun QuantityPicker(
    quantityText: String,
    onQuantityTextChange: (String) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.height(56.dp),
        color = Color(0xFFEDE9FA),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onDecrease) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TitleColor)
            }

            BasicTextField(
                value = quantityText,
                onValueChange = { value ->
                    if ((value.isEmpty() || value.all { it.isDigit() }) && value.length <= 3) {
                        onQuantityTextChange(value)
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = TitleColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                modifier = Modifier.width(36.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if ((quantityText.toIntOrNull() ?: 0) < 1) onQuantityTextChange("1")
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) { innerTextField() }
                }
            )

            IconButton(onClick = onIncrease) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = TitleColor)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToppingBottomSheet(
    toppings: List<ToppingDto>,
    selectedIds: Set<Long>,
    onToggleTopping: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = BackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                "DANH SÁCH TOPPING",
                fontFamily = k2d,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TitleColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(toppings, key = { it.id }) { topping ->
                    ToppingCard(
                        topping = topping,
                        selected = topping.id in selectedIds,
                        onClick = { onToggleTopping(topping.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ToppingCard(
    topping: ToppingDto,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(314.dp)
            .border(1.dp, if (selected) MainButtonColor else PlaceHolderColor.copy(alpha = 0.55f), shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = topping.imageUrl.toFullImageUrl(),
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.error_img),
                contentDescription = topping.name,
                modifier = Modifier.fillMaxWidth().height(190.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    topping.name,
                    fontFamily = k2d,
                    fontSize = 16.sp,
                    color = TitleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatPrice(topping.price.toLong()), fontFamily = k2d, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LabelColor)
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = if (selected) MainButtonColor else TitleColor
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (selected) "✓" else "+", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun sizeOrder(sizeName: String): Int {
    return when (sizeName.uppercase()) {
        "S" -> 0
        "M" -> 1
        "L" -> 2
        else -> 3
    }
}

private fun sizeLabel(sizeName: String): String {
    return when (sizeName.uppercase()) {
        "S" -> "Small"
        "M" -> "Medium"
        "L" -> "Large"
        else -> sizeName
    }
}

private fun formatPrice(value: Long): String {
    return "${DecimalFormat("#,###").format(value)}đ"
}

@Composable
@Preview(showSystemUi = true)
fun ProductDetailScreenPreview() {
    CoffeeShopAppTheme {
        ProductDetailScreen(
            product = Product(
                id = "1",
                name = "Cappuccino",
                price = 45000,
                imageUrl = "",
                description = "Một tuyệt tác đặc trưng kết hợp chiều sâu mạnh mẽ của hạt cà phê Arabica thượng hạng cùng vị ngọt thanh thoát."
            )
        )
    }
}
