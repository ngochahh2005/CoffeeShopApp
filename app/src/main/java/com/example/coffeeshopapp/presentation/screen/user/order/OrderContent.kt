package com.example.coffeeshopapp.presentation.screen.user.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.data.model.entity.CartItemTopping
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.FlowTagRow
import com.example.coffeeshopapp.presentation.theme.AuxiliaryButtonColor
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.theme.pacifico
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.getCurrentDateTime

@Composable
fun OrderContent(
    items: List<CartItem>,
    onBack: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onAddMore: () -> Unit = {}
) {
    val totalAmount = items.sumOf { it.priceAtAdd * it.quantity }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxWidth().background(CardBackgroundColor).padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = TitleColor
                )
            }
            Text(
                text = "Đặt hàng",
                style = MaterialTheme.typography.titleMedium,
                color = TitleColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(text = getCurrentDateTime(), color = Color(0xff49454D), fontFamily = k2d, fontWeight = FontWeight.Normal, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.img_order),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.FillWidth
                )
                Text(
                    text = "Ly cà phê ngon nhất\nlà ly cà phê bạn\nthích nhất",
                    fontFamily = pacifico,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterStart)
                )
            }

            CommonSpace()

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Không có sản phẩm nào.", color = PlaceHolderColor)
                }
                return
            }

            Text(
                text = "DANH SÁCH MẶT HÀNG",
                fontFamily = k2d,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.2.sp,
                color = Color(0xff63567A)
            )

            CommonSpace()

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items, key = { it.lineId }) { item ->
                    OrderItemCard(item = item)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Tổng tiền", color = LabelColor)
                        Text(
                            text = "${totalAmount.formatGrouped()}đ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeTextColor
                        )
                    }
                    Button(
                        onClick = onCheckout,
                        colors = ButtonDefaults.buttonColors(containerColor = AuxiliaryButtonColor)
                    ) {
                        Text(text = "Thanh toán", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddMore,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Chọn thêm mặt hàng", color = CoffeeTextColor)
            }
        }
    }
}

@Composable
private fun OrderItemCard(item: CartItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrlAtAdd,
                placeholder = painterResource(R.drawable.st_bo),
                error = painterResource(R.drawable.error_img),
                contentDescription = item.nameAtAdd,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.nameAtAdd,
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color(0xff191B29)
                )
                Text(
                    text = "${item.priceAtAdd.formatGrouped()}đ x ${item.quantity}",
                    color = Color(0xff63567A),
                    fontFamily = k2d,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
                if (!item.selectedSizeName.isNullOrBlank()) {
                    FlowTagRow(size = item.selectedSizeName, toppings = item.toppings)
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun OrderScreenPreview() {
    val mockItems = listOf(
        CartItem(
            lineId = "1",
            productId = "1",
            nameAtAdd = "Trà sữa khoai môn trân châu đường đen",
            priceAtAdd = 29000,
            quantity = 2,
            imageUrlAtAdd = "",
            selectedSizeName = "L",
            toppings = listOf(
                CartItemTopping(
                    id = 1,
                    name = "Kem chesee",
                    price = 5000,
                ),
                CartItemTopping(
                    id = 2,
                    name = "Flan",
                    price = 5000,
                ),
                CartItemTopping(
                    id = 3,
                    name = "Trân châu đen",
                    price = 5000,
                )
            )
        ),
        CartItem(
            lineId = "2",
            productId = "2",
            nameAtAdd = "Bạc Xỉu",
            priceAtAdd = 35000,
            quantity = 1,
            imageUrlAtAdd = "",
            selectedSizeName = "M",
        ),
        CartItem(
            lineId = "3",
            productId = "3",
            nameAtAdd = "Bạc Xỉu",
            priceAtAdd = 35000,
            quantity = 1,
            imageUrlAtAdd = "",
            selectedSizeName = "M",
        ),
        CartItem(
            lineId = "4",
            productId = "4",
            nameAtAdd = "Bạc Xỉu",
            priceAtAdd = 35000,
            quantity = 1,
            imageUrlAtAdd = "",
            selectedSizeName = "M",
        ),
        CartItem(
            lineId = "5",
            productId = "5",
            nameAtAdd = "Bạc Xỉu",
            priceAtAdd = 35000,
            quantity = 1,
            imageUrlAtAdd = "",
            selectedSizeName = "M",
        ),
        CartItem(
            lineId = "2",
            productId = "2",
            nameAtAdd = "Bạc Xỉu",
            priceAtAdd = 35000,
            quantity = 1,
            imageUrlAtAdd = "",
            selectedSizeName = "M",
        ),
        CartItem(
            lineId = "6",
            productId = "6",
            nameAtAdd = "Bạc Xỉu",
            priceAtAdd = 35000,
            quantity = 1,
            imageUrlAtAdd = "",
            selectedSizeName = "M",
        ),

    )
    CoffeeShopAppTheme() {
        OrderContent(
            mockItems
        )
    }
}