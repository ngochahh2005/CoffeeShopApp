package com.example.coffeeshopapp.presentation.screen.user.checkout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.dto.PaymentMethodDto
import com.example.coffeeshopapp.data.model.dto.PromotionDto
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.presentation.components.CommonSpace
import com.example.coffeeshopapp.presentation.components.OrderItemCard
import com.example.coffeeshopapp.presentation.theme.AuxiliaryButtonColor
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.TextColor
import com.example.coffeeshopapp.presentation.theme.TitleColor
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.presentation.viewmodel.CheckoutUiState
import com.example.coffeeshopapp.utils.formatGrouped

@Composable
fun CheckoutContent(
    state: CheckoutUiState,
    onBack: () -> Unit,
    onDeliveryAddressChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onPaymentMethodSelect: (PaymentMethodDto) -> Unit,
    onPromotionSelect: (PromotionDto?) -> Boolean,
    onSubmitOrder: () -> Unit
) {
    val context = LocalContext.current
    var showPromotionSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackgroundColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = TitleColor
                    )
                }
                Text(
                    text = "Thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    color = TitleColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(12.dp)) {
                item {
                    CheckoutTitle("Danh sách sản phẩm")
                }
                items(state.items, key = { it.lineId }) { item ->
                    OrderItemCard(
                        item = item,
                        modifier = Modifier
                            .border(BorderStroke(1.dp, Color(0xffD8BFD8)),RoundedCornerShape(16.dp))
                    )
                    CommonSpace(12.dp)
                }

                item {
                    CheckoutTitle("Áp dụng ưu đãi")
                    val eligiblePromotion = state.selectedPromotion?.takeIf { promotion ->
                        state.totalAmount >= promotion.requiredOrderAmount()
                    }

                    PromotionSelectionRow(
                        selectedPromotion = eligiblePromotion,
                        onClick = { showPromotionSheet = true }
                    )
                    CommonSpace(12.dp)
                }

                item {
                    CheckoutTitle("Địa chỉ giao hàng")
                    OutlinedTextField(
                        value = state.deliveryAddress,
                        onValueChange = onDeliveryAddressChange,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, TextColor, RoundedCornerShape(16.dp)),
                        placeholder = { Text(text = "Nhập địa chỉ giao hàng") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = k2d,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextColor
                        ),
                        singleLine = true
                    )
                    CommonSpace(12.dp)
                }

                item {
                    CheckoutTitle("Ghi chú")
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = onNoteChange,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, TextColor, RoundedCornerShape(16.dp)),
                        placeholder = { Text(text = "Ghi chú đơn hàng (tùy chọn)") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = k2d,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextColor
                        ),
                        singleLine = true
                    )
                    CommonSpace(12.dp)
                }

                item {
                    val eligiblePromotion = state.selectedPromotion?.takeIf { promo ->
                        state.totalAmount >= promo.requiredOrderAmount()
                    }
                    val discountAmount = eligiblePromotion?.let { promo ->
                        if (promo.discountType == "PERCENTAGE") {
                            (state.totalAmount * (promo.discountValue / 100.0)).toLong()
                        } else {
                            promo.discountValue.toLong()
                        }
                    } ?: 0L
                    val finalAmount = (state.totalAmount - discountAmount).coerceAtLeast(0L)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xffBEBED1).copy(alpha = 0.3f))
                            .padding(horizontal = 12.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tổng tiền",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                color = Color(0xff49454D)
                            )
                            Text(
                                text = "${state.totalAmount.formatGrouped()}đ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                color = Color(0xff191B29)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Giảm giá",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                color = Color(0xff49454D)
                            )
                            Text(
                                text = "${discountAmount.formatGrouped()}đ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 16.sp,
                                color = Color(0xff191B29)
                            )
                        }

                        HorizontalDivider(color = Color(0xffD8BFD8))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Số tiền thanh toán",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xff63567A)
                            )
                            Text(
                                text = "${finalAmount.formatGrouped()}đ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xff63567A)
                            )
                        }
                    }

                    CommonSpace(12.dp)
                }

                item {
                    CheckoutTitle("Chọn phương thức thanh toán")
                    val vnpayLogoResId = remember(context) {
                        val id = context.resources.getIdentifier(
                            "logo_vnpay",
                            "drawable",
                            context.packageName
                        )
                        if (id != 0) id else R.drawable.loading_img
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPaymentMethodSelect(PaymentMethodDto.VNPAY) }
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    color = if (state.paymentMethod == PaymentMethodDto.VNPAY) Color(
                                        0xff63567A
                                    ) else Color(0xffCBC4CE)
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                color = if (state.paymentMethod == PaymentMethodDto.VNPAY) Color(
                                    0xffF4F2FF
                                ) else Color.White
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(vnpayLogoResId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Thanh toán bằng VNPAY",
                                color = Color(0xff191B29),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        RadioButton(
                            selected = state.paymentMethod == PaymentMethodDto.VNPAY,
                            onClick = { onPaymentMethodSelect(PaymentMethodDto.VNPAY) },
                            modifier = Modifier.align(Alignment.CenterEnd),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xff63567A),
                                unselectedColor = Color(0xffCBC4CE)
                            )
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPaymentMethodSelect(PaymentMethodDto.CASH) }
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    color = if (state.paymentMethod == PaymentMethodDto.CASH) Color(
                                        0xff63567A
                                    ) else Color(0xffCBC4CE)
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .background(
                                color = if (state.paymentMethod == PaymentMethodDto.CASH) Color(
                                    0xffF4F2FF
                                ) else Color.White
                            )
                            .padding(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalAtm,
                                contentDescription = null,
                                tint = Color(0xff7A757E)
                            )
                            Text(
                                text = "Thanh toán tiền mặt (COD)",
                                color = Color(0xff191B29),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        RadioButton(
                            selected = state.paymentMethod == PaymentMethodDto.CASH,
                            onClick = { onPaymentMethodSelect(PaymentMethodDto.CASH) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xff63567A),
                                unselectedColor = Color(0xffCBC4CE)
                            ),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                    CommonSpace(74.dp)
                }
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(CardBackgroundColor)
            .height(60.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp)

        ) {
            Button(
                onClick = onSubmitOrder,
                enabled = !state.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff3D3450),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = if (state.isSubmitting) "Đang xử lý..." else "XÁC NHẬN THANH TOÁN",
                )
            }
        }
    }

    if (showPromotionSheet) {
        val eligiblePromotion = state.selectedPromotion?.takeIf { promotion ->
            state.totalAmount >= promotion.requiredOrderAmount()
        }
        PromotionBottomSheet(
            promotions = state.availablePromotions,
            selectedPromotion = eligiblePromotion,
            onPromotionSelected = {
                if (onPromotionSelect(it)) {
                    showPromotionSheet = false
                }
            },
            onDismiss = { showPromotionSheet = false }
        )
    }
}

@Composable
private fun CheckoutTitle(title: String) {
    Text(
        text = title,
        fontFamily = k2d,
        fontWeight = FontWeight.Normal,
        letterSpacing = 1.2.sp,
        color = Color(0xff63567A),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun PromotionSelectionRow(
    selectedPromotion: PromotionDto?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(BorderStroke(1.dp, Color(0xffD8BFD8)), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Discount, contentDescription = null, tint = LabelColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = selectedPromotion?.promotionCode ?: "Chọn mã giảm giá",
                color = if (selectedPromotion != null) TextColor else LabelColor,
                fontWeight = FontWeight.Normal
            )
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = LabelColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromotionBottomSheet(
    promotions: List<PromotionDto>,
    selectedPromotion: PromotionDto?,
    onPromotionSelected: (PromotionDto?) -> Unit = {_ ->},
    onDismiss: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Mã giảm giá khả dụng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (promotions.isEmpty()) {
                Text(
                    text = "Không có mã giảm giá nào.",
                    modifier = Modifier.padding(vertical = 32.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPromotionSelected(null) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedPromotion == null, onClick = { onPromotionSelected(null) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Không sử dụng")
                        }
                    }
                    items(promotions) { promo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPromotionSelected(promo) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedPromotion?.id == promo.id, onClick = { onPromotionSelected(promo) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = promo.promotionCode, fontWeight = FontWeight.Bold)
                                Text(text = promo.name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                val discountText = if (promo.discountType == "PERCENTAGE") {
                                    "Giảm ${promo.discountValue.toInt()}%"
                                } else {
                                    "Giảm ${promo.discountValue.toLong().formatGrouped()}đ"
                                }
                                Text(text = discountText, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun CheckoutPreview() {
    val mockCartItems = listOf(
        CartItem(
            lineId = "1",
            nameAtAdd = "Cà phê Muối",
            priceAtAdd = 35000L,
            quantity = 2,
            imageUrlAtAdd = "",
            productId = "1",
            selectedSizeName = "M",
            sizePriceExtra = 0L,
            toppings = emptyList()),
        CartItem(
            lineId = "2",
            nameAtAdd = "Trà Đào Cam Sả",
            priceAtAdd = 45000L,
            quantity = 1,
            imageUrlAtAdd = "",
            productId = "2",
            selectedSizeName = "S",
            sizePriceExtra = 0L,
            toppings = emptyList()
        ),
    )

    val mockPromotions = listOf(
        PromotionDto(id = 1, promotionCode = "COFFEE50", name = "Giảm 50% cho bộ sưu tập cà phê", discountType = "PERCENTAGE", discountValue = 50.0),
        PromotionDto(id = 2, promotionCode = "COFFEE15K", name = "Giảm trực tiếp 15k trên đơn hàng", discountType = "FIXED", discountValue = 15000.0)
    )

    val mockUiState = CheckoutUiState(
        deliveryAddress = "Số 97 đường Trần Phú, Hà Đông, Hà Nội",
        note = "Ít đường ít đá giúp mình nhé!",
        items = mockCartItems,
        totalAmount = 115000L,
        availablePromotions = mockPromotions,
        selectedPromotion = mockPromotions[1],
        paymentMethod = PaymentMethodDto.VNPAY,
        isSubmitting = false,
        error = null
    )

    CoffeeShopAppTheme {
        CheckoutContent(
            state = mockUiState,
            onBack = {},
            onDeliveryAddressChange = {},
            onNoteChange = {},
            onPaymentMethodSelect = {},
            onPromotionSelect = { true },
            onSubmitOrder = {}
        )
    }
}
