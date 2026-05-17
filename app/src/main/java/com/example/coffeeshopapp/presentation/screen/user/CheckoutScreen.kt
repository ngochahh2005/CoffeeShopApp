package com.example.coffeeshopapp.presentation.screen.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.presentation.theme.AuxiliaryButtonColor
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.viewmodel.CheckoutViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.dto.PaymentMethodDto
import com.example.coffeeshopapp.data.model.dto.PromotionDto
import com.example.coffeeshopapp.utils.formatGrouped

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = viewModel(),
    onBack: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {},
    onOpenVnPay: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val paymentError = state.error
    val isSubmitting = state.isSubmitting
    var showPromotionSheet by remember { mutableStateOf(false) }

    LaunchedEffect(paymentError) {
        paymentError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = CoffeeTextColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Thanh toán",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CoffeeTextColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Text(text = "Địa chỉ giao hàng", color = LabelColor)
                OutlinedTextField(
                    value = state.deliveryAddress,
                    onValueChange = viewModel::setDeliveryAddress,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Nhập địa chỉ giao hàng") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(text = "Ghi chú", color = LabelColor)
                OutlinedTextField(
                    value = state.note,
                    onValueChange = viewModel::setNote,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Ghi chú đơn hàng (tùy chọn)") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                PromotionSelectionRow(
                    selectedPromotion = state.selectedPromotion,
                    onClick = { showPromotionSheet = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(text = "Chọn phương thức thanh toán", color = LabelColor)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setPaymentMethod(PaymentMethodDto.VNPAY) }
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.paymentMethod == PaymentMethodDto.VNPAY,
                        onClick = { viewModel.setPaymentMethod(PaymentMethodDto.VNPAY) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Thanh toán bằng VNPAY", color = CoffeeTextColor)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setPaymentMethod(PaymentMethodDto.CASH) }
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.paymentMethod == PaymentMethodDto.CASH,
                        onClick = { viewModel.setPaymentMethod(PaymentMethodDto.CASH) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Thanh toán tại quầy", color = CoffeeTextColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(state.items, key = { it.lineId }) { item ->
                CheckoutItemCard(item = item)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                val discountAmount = state.selectedPromotion?.let { promo ->
                    if (promo.discountType == "PERCENTAGE") {
                        (state.totalAmount * (promo.discountValue / 100.0)).toLong()
                    } else {
                        promo.discountValue.toLong()
                    }
                } ?: 0L
                val finalAmount = (state.totalAmount - discountAmount).coerceAtLeast(0L)

                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 2.dp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Tạm tính", color = LabelColor)
                                Text(text = "${state.totalAmount.formatGrouped()}đ", color = CoffeeTextColor)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Giảm giá", color = LabelColor)
                                Text(text = "-${discountAmount.formatGrouped()}đ", color = Color.Red)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Tổng tiền", color = LabelColor)
                                Text(
                                    text = "${finalAmount.formatGrouped()}đ",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CoffeeTextColor
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.submitOrder(
                                        onVnPayUrl = { url ->
                                            onOpenVnPay(url)
                                            Toast.makeText(context, "Chuyển sang VNPAY", Toast.LENGTH_LONG).show()
                                        },
                                        onSuccess = {
                                            Toast.makeText(context, "Đặt hàng thành công", Toast.LENGTH_LONG).show()
                                            onPaymentSuccess()
                                        }
                                    )
                                },
                                enabled = !isSubmitting,
                                colors = ButtonDefaults.buttonColors(containerColor = AuxiliaryButtonColor)
                            ) {
                                Text(text = if (isSubmitting) "Đang xử lý..." else "XÁC NHẬN THANH TOÁN", color = Color.White)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showPromotionSheet) {
        PromotionBottomSheet(
            promotions = state.availablePromotions,
            selectedPromotion = state.selectedPromotion,
            onPromotionSelected = { 
                viewModel.selectPromotion(it)
                showPromotionSheet = false
            },
            onDismiss = { showPromotionSheet = false }
        )
    }
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
                color = if (selectedPromotion != null) CoffeeTextColor else LabelColor,
                fontWeight = if (selectedPromotion != null) FontWeight.Bold else FontWeight.Normal
            )
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = LabelColor)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PromotionBottomSheet(
    promotions: List<PromotionDto>,
    selectedPromotion: PromotionDto?,
    onPromotionSelected: (PromotionDto?) -> Unit,
    onDismiss: () -> Unit
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
private fun CheckoutItemCard(item: com.example.coffeeshopapp.data.model.entity.CartItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrlAtAdd,
                placeholder = painterResource(R.drawable.loading_img),
                error = painterResource(R.drawable.error_img),
                contentDescription = item.nameAtAdd,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(78.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nameAtAdd,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = CoffeeTextColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${item.priceAtAdd.formatGrouped()}đ x ${item.quantity}", color = LabelColor)
            }
        }
    }
}

