package com.example.coffeeshopapp.presentation.screen.user.checkout

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
import com.example.coffeeshopapp.data.model.entity.CartItem
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

    LaunchedEffect(paymentError) {
        paymentError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    CheckoutContent(
        state = state,
        onBack = onBack,
        onDeliveryAddressChange = viewModel::setDeliveryAddress,
        onNoteChange = viewModel::setNote,
        onPaymentMethodSelect = viewModel::setPaymentMethod,
        onPromotionSelect = { promotion ->
            val selected = viewModel.selectPromotion(promotion)
            if (!selected && promotion != null) {
                Toast.makeText(
                    context,
                    "Đơn hàng tối thiểu ${promotion.requiredOrderAmount().formatGrouped()}đ để dùng mã ${promotion.promotionCode}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            selected
        },
        onSubmitOrder = {
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
        }
    )
}
