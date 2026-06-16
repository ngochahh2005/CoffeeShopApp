package com.example.coffeeshopapp.presentation.screen.user.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.presentation.components.FlowTagRow
import com.example.coffeeshopapp.presentation.theme.*
import com.example.coffeeshopapp.presentation.viewmodel.CartUiState
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.toFullImageUrl

@Composable
fun CartContent(
    state: CartUiState,
    cartCount: Int,
    onToggleSelection: (String) -> Unit,
    onToggleAllSelection: (Boolean) -> Unit,
    onRemoveSelected: () -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onBack: () -> Unit,
    openOrderScreen: () -> Unit,
    openProductDetailScreen: (String) -> Unit,
    onQuantityChange: (String, Int) -> Unit
) {
    val allSelected = state.items.isNotEmpty() && state.selectedIds.size == state.items.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundColor,
                        Color(0xFFD1C8D5)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 8.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = LabelColor, modifier = Modifier.size(22.dp))
                }
                Text(
                    text = "Giỏ hàng của tôi",
                    fontFamily = pacifico,
                    fontSize = 28.sp,
                    color = LabelColor,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = LabelColor,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(cartCount.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // select all
            if (state.items.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = { onToggleAllSelection(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PlaceHolderColor,
                            uncheckedColor = PlaceHolderColor
                        )
                    )
                    Text("Chọn tất cả", style = MaterialTheme.typography.bodyMedium, color = LabelColor, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.selectedIds.isNotEmpty()) {
                        Text(
                            "Xóa mục đã chọn",
                            color = Color(0xFFE53935),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onRemoveSelected() }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 140.dp) // Space for floating bottom bar
            ) {
                items(state.items, key = { it.lineId }) { item ->
                    PremiumCartItem(
                        item = item,
                        isSelected = item.lineId in state.selectedIds,
                        onCheckedChange = { onToggleSelection(item.lineId) },
                        onIncrease = { onIncrease(item.lineId) },
                        onDecrease = { onDecrease(item.lineId) },
                        onRemove = { onRemove(item.lineId) },
                        onOpenDetail = { openProductDetailScreen(item.productId) },
                        onQuantityChange = { onQuantityChange(item.lineId, it) }
                    )
                }
            }
        }

        // check out
        AnimatedVisibility(
            visible = state.items.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp)),
                color = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Tổng tiền", style = MaterialTheme.typography.labelLarge, color = PlaceHolderColor)
                        Text(
                            text = "${state.totalAmount.formatGrouped()}đ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = TitleSmallColor,
                            fontSize = 18.sp
                        )
                    }

                    Button(
                        onClick = openOrderScreen,
                        enabled = state.selectedIds.isNotEmpty(),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff3D3450),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xffA0A0A0)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text("Thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumCartItem(
    item: CartItem,
    isSelected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onOpenDetail: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxOffset = -90f
    val draggableState = rememberDraggableState { delta ->
        val newOffset = offsetX + delta
        offsetX = newOffset.coerceIn(maxOffset, 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFFFF1F0))
                .clickable { onRemove() },
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFFF4D4F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX.dp)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { offsetX = if (offsetX < -45f) maxOffset else 0f }
                )
                .clickable(onClick = onOpenDetail),
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = PlaceHolderColor, uncheckedColor = PlaceHolderColor),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF5F5F5))
                        .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(18.dp))
                ) {
                    AsyncImage(
                        model = item.imageUrlAtAdd.toFullImageUrl(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.loading_img),
                        error = painterResource(R.drawable.error_img)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nameAtAdd,
                        fontFamily = k2d,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = TextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    CartItemOptions(item)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.priceAtAdd.formatGrouped()}đ",
                            color = TextColor,
                            fontFamily = k2d,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        )

                        Spacer(modifier = Modifier.width(4.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8F8F8))
                                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                                .padding(horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.Default.Remove, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            }
                            
                            QuantityField(item, onQuantityChange)

                            IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp), tint = TitleColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantityField(item: CartItem, onQuantityChange: (Int) -> Unit) {
    var tmpQty by remember(item.quantity) { mutableStateOf(item.quantity.toString()) }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = tmpQty,
        onValueChange = { if (it.length <= 2 && (it.isEmpty() || it.all { c -> c.isDigit() })) tmpQty = it },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        ),
        modifier = Modifier
            .width(28.dp)
            .onFocusChanged {
                if (!it.isFocused) {
                    val final = tmpQty.toIntOrNull() ?: item.quantity
                    if (final > 0) onQuantityChange(final) else tmpQty = item.quantity.toString()
                }
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = true,
        decorationBox = { innerTextField -> Box(contentAlignment = Alignment.Center) { innerTextField() } }
    )
}

@Composable
private fun CartItemOptions(item: CartItem) {
    if (item.selectedSizeName != null || item.toppings.isNotEmpty()) {
        FlowTagRow(item.selectedSizeName, item.toppings)
    }
}

@Composable
@Preview(showSystemUi = true)
fun CartPreview() {
    val mockState = CartUiState(
        items = listOf(
            CartItem(
                lineId = "1",
                productId = "p1",
                nameAtAdd = "Caramel Macchiato",
                imageUrlAtAdd = "https://example.com/coffee.jpg",
                priceAtAdd = 2000000,
                quantity = 1,
                selectedSizeName = "Vừa (M)",
            ),
            CartItem(
                lineId = "2",
                productId = "p2",
                nameAtAdd = "Bạc Xỉu Đá",
                imageUrlAtAdd = "https://example.com/coffee2.jpg",
                priceAtAdd = 35000,
                quantity = 2,
                selectedSizeName = "Lớn (L)",
                toppings = emptyList()
            )
        ),
        selectedIds = setOf("1"),
        totalAmount = 0
    )

    CoffeeShopAppTheme {
        CartContent(
            state = mockState,
            cartCount = 3,
            onToggleSelection = {},
            onToggleAllSelection = {},
            onRemoveSelected = {},
            onIncrease = {},
            onDecrease = {},
            onRemove = {},
            onBack = {},
            openOrderScreen = {},
            openProductDetailScreen = {},
            onQuantityChange = { _, _ -> }
        )
    }
}
