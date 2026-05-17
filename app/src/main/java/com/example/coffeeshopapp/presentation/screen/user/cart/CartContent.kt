package com.example.coffeeshopapp.presentation.screen.user.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.coffeeshopapp.data.local.CartDataStore
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.data.model.entity.CartItemTopping
import com.example.coffeeshopapp.presentation.components.FlowTagRow
import com.example.coffeeshopapp.presentation.theme.AuxiliaryButtonColor
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.viewmodel.CartUiState
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.toFullImageUrl

@Composable
fun CartContent(
    state: CartUiState,
    cartCount: Int,
    onToggleSelection: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onBack: () -> Unit,
    openOrderScreen: () -> Unit,
    openProductDetailScreen: (String) -> Unit,
    onQuantityChange: (String, Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 18.dp)) {
            IconButton(
                onClick = onBack,
                colors = IconButtonDefaults.iconButtonColors(contentColor = CoffeeTextColor)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "back", modifier = Modifier.size(36.dp))
            }

            Text(
                text = buildAnnotatedString {
                    withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(color = LabelColor)) {
                        append("Giỏ hàng ")
                    }
                    withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(color = CoffeeTextColor)) {
                        append("(${cartCount})")
                    }
                },
                style = MaterialTheme.typography.titleMedium,
                color = TitleSmallColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.items, key = { it.lineId }) { item ->
                ItemOfCart(
                    item = item,
                    isSelected = item.lineId in state.selectedIds,
                    onCheckedChange = { onToggleSelection(item.lineId) },
                    onIncrease = { onIncrease(item.lineId) },
                    onDecrease = { onDecrease(item.lineId) },
                    onRemove = { onRemove(item.lineId) },
                    onOpenDetail = { openProductDetailScreen(item.productId) },
                    onQuantityChange = { onQuantityChange(item.lineId, it)}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${state.totalAmount.formatGrouped()}đ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TitleSmallColor,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(.5f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = openOrderScreen,
                enabled = state.selectedIds.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuxiliaryButtonColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Mua hàng (${state.selectedIds.size})",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ItemOfCart(
    item: CartItem,
    isSelected: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onOpenDetail: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    var tmpQuantity by rememberSaveable(item.quantity) {
        mutableStateOf(item.quantity.toString())
    }
    val focusManager = LocalFocusManager.current

    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxOffset = -80f

    val draggableState = rememberDraggableState { delta ->
        val newOffset = offsetX + delta
        offsetX = newOffset.coerceIn(maxOffset, 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Red)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .clickable {
                        onRemove()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { offsetX = if (offsetX < -40f) maxOffset else 0f }
                ),
            color = BackgroundColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, end = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PlaceHolderColor,
                        uncheckedColor = PlaceHolderColor
                    )
                )

                AsyncImage(
                    model = item.imageUrlAtAdd.toFullImageUrl(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.error_img),
                    contentDescription = item.nameAtAdd,
                    modifier = Modifier.padding(end = 8.dp).size(108.dp),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.nameAtAdd,
                        fontFamily = k2d,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color(0xff191B29),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    CartItemOptions(item)

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = item.priceAtAdd.formatGrouped(),
                            color = Color(0xff63567A),
                            fontFamily = k2d,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                        )

                        Row(
                            modifier = Modifier.wrapContentSize().align(Alignment.CenterEnd),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onDecrease) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            BasicTextField(
                                value = tmpQuantity,
                                onValueChange = { newValue ->
                                    if ((newValue.isEmpty() || newValue.all { it.isDigit() }) && newValue.length <= 3) {
                                        tmpQuantity = newValue
                                    }
                                },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = TitleSmallColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier
                                    .width(35.dp)
                                    .wrapContentHeight()
                                    .onFocusChanged { focusState ->
                                        if (!focusState.isFocused) {
                                            val finalQty = tmpQuantity.toIntOrNull() ?: item.quantity
                                            if (finalQty > 0) onQuantityChange(finalQty) else tmpQuantity = item.quantity.toString()
                                        }
                                    },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        val finalQty = tmpQuantity.toIntOrNull() ?: item.quantity
                                        if (finalQty > 0) onQuantityChange(finalQty) else tmpQuantity = item.quantity.toString()
                                        focusManager.clearFocus()
                                    }
                                ),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ) {
                                        innerTextField()
                                    }
                                }
                            )
                            IconButton(onClick = onIncrease) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemOptions(item: CartItem) {
    val optionText = buildList {
        item.selectedSizeName?.let { add("Size $it (+${item.sizePriceExtra.formatGrouped()}đ)") }
        if (item.toppings.isNotEmpty()) {
            add("Topping: ${item.toppings.joinToString { "${it.name} (+${it.price.formatGrouped()}đ)" }}")
        }
    }.joinToString(" • ")

    if (optionText.isNotBlank()) {
        FlowTagRow(item.selectedSizeName, item.toppings)
    }
}

@Composable
@Preview(showSystemUi = true)
fun CartContentPreview() {
    val mockState = CartUiState(
        isLoading = false,
        items = listOf(
            CartItem(
                lineId = "1|size=M|toppings=1",
                productId = "1",
                nameAtAdd = "Hồng trà sữa trân châu đường đen",
                priceAtAdd = 40000,
                imageUrlAtAdd = "",
                quantity = 1,
                selectedSizeName = "M",
                sizePriceExtra = 5000,
                toppings = listOf(
                    CartItemTopping(id = 1, name = "Trân châu", price = 5000),
                    CartItemTopping(id = 2, name = "Thạch cà phê", price = 5000)
                )
            ),
            CartItem(
            lineId = "2",
            productId = "1",
            nameAtAdd = "Hồng trà sữa trân châu đường đen",
            priceAtAdd = 40000,
            imageUrlAtAdd = "",
            quantity = 1,
            sizePriceExtra = 5000,
            ),
            CartItem(
                lineId = "3|size=L|toppings=1",
                productId = "3",
                nameAtAdd = "Hồng trà sữa trân châu đường đen",
                priceAtAdd = 40000,
                imageUrlAtAdd = "",
                quantity = 1,
                selectedSizeName = "L",
                sizePriceExtra = 5000,
            )
        ),
        selectedIds = setOf("1|size=M|toppings=1")
    )

    CoffeeShopAppTheme {
        CartContent(
            state = mockState,
            cartCount = 3,
            onToggleSelection = {},
            onIncrease = {},
            onDecrease = {},
            onRemove = {},
            onBack = {},
            openOrderScreen = {},
            openProductDetailScreen = {},
            onQuantityChange = {_, _ ->}
        )
    }
}
