package com.example.coffeeshopapp.presentation.screen.user.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
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
            onSubmitClick = { ratings, comments, images ->
                viewModel.submitMutipleReviews(
                    orderId = orderId,
                    reviewItems = reviewItems,
                    ratings = ratings,
                    comments = comments,
                    images = images
                )
            }
        )
    }
}
