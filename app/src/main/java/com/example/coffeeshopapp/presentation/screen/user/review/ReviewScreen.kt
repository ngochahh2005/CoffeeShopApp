package com.example.coffeeshopapp.presentation.screen.user.review

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.data.model.dto.OrderItemDto
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.viewmodel.ReviewViewModel

@Composable
fun ReviewScreen(
    orderId: Long = 0,
    reviewItems: List<OrderItemDto> = emptyList(),
    onBack: () -> Unit = {},
    onReviewSubmitted: () -> Unit = {},
    viewModel: ReviewViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Đánh giá sản phẩm thành công!", Toast.LENGTH_SHORT).show()
            onReviewSubmitted()
            onBack()
        }
    }

    if (allProducts.isEmpty() && !uiState.isSuccess) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CoffeeTextColor)
        }
    } else {
        ReviewContent(
            reviewItems = reviewItems,
            allProducts = allProducts,
            uiState = uiState,
            onBack = onBack,
            onRatingChange = { index, rating -> viewModel.updateRating(index, rating) },
            onCommentChange = { index, comment -> viewModel.updateComment(index, comment) },
            onImageChange = { index, uri -> viewModel.updateImage(index, uri) },
            onSubmitClick = {
                viewModel.submitMutipleReviews(
                    orderId = orderId,
                    reviewItems = reviewItems
                )
            }
        )
    }
}
