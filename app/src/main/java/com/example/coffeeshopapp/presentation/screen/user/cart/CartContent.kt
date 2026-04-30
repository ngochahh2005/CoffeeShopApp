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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.presentation.theme.AuxiliaryButtonColor
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor
import com.example.coffeeshopapp.presentation.viewmodel.CartUiState
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.toFullImageUrl

@Composable
fun CartContent(
    state: CartUiState,
    onToggleSelection: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    openOrderScreen: () -> Unit,
    openProductDetailScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(top = 18.dp)) {
            IconButton(
                onClick = {},
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
                        append("(${state.totalItemsInCart})")
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
            items(state.items, key = { it.productId }) { item ->
                ItemOfCart(
                    item = item,
                    isSelected = item.productId in state.selectedIds,
                    onCheckedChange = { onToggleSelection(item.productId) },
                    onIncrease = { onIncrease(item.productId) },
                    onDecrease = { onDecrease(item.productId) },
                    onRemove = { onRemove(item.productId) },
                    onOpenDetail = { openProductDetailScreen(item.productId) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tổng tiền: ${state.totalAmount.formatGrouped()}đ",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TitleSmallColor,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp, start = 18.dp, end = 18.dp),
            onClick = openOrderScreen,
            enabled = state.selectedIds.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = AuxiliaryButtonColor,
                contentColor = Color.White
            )
        ) {
            Text("Mua hàng (${state.selectedIds.size})", style = MaterialTheme.typography.bodyLarge)
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
    onOpenDetail: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val maxOffset = -80f

    val draggableState = rememberDraggableState { delta ->
        val newOffset = offsetX + delta
        offsetX = newOffset.coerceIn(maxOffset, 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clickable(onClick = onOpenDetail)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
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

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item.nameAtAdd,
                        color = TitleSmallColor,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.priceAtAdd.formatGrouped(),
                            color = CoffeeTextColor,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            modifier = Modifier.wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onDecrease) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = item.quantity.toString(),
                                modifier = Modifier.padding(horizontal = 4.dp),
                                color = TitleSmallColor
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
@Preview(showSystemUi = true)
fun CartContentPreview() {
    val mockState = CartUiState(
        isLoading = false,
        items = listOf(
            CartItem(productId = "1", nameAtAdd = "Hồng trà sữa trân châu đường đen", priceAtAdd = 35000, imageUrlAtAdd = "", quantity = 1),
            CartItem(productId = "2", nameAtAdd = "Bạc Xỉu", priceAtAdd = 29000, imageUrlAtAdd = "", quantity = 2)
        ),
        selectedIds = setOf("1")
    )

    CoffeeShopAppTheme {
        CartContent(
            state = mockState,
            onToggleSelection = {},
            onIncrease = {},
            onDecrease = {},
            onRemove = {},
            openOrderScreen = {},
            openProductDetailScreen = {}
        )
    }
}
