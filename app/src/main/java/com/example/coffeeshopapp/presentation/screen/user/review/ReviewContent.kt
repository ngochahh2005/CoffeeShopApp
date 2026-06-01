package com.example.coffeeshopapp.presentation.screen.user.review

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.dto.OrderItemDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.viewmodel.UserReviewUiState
import com.example.coffeeshopapp.utils.toFullImageUrl
import java.math.BigDecimal
import androidx.compose.ui.res.painterResource
import com.example.coffeeshopapp.presentation.components.CommonSpace

@Composable
fun ReviewContent(
    reviewItems: List<OrderItemDto>,
    allProducts: List<ProductDto>,
    uiState: UserReviewUiState,
    onBack: () -> Unit,
    onRatingChange: (Int, Int) -> Unit,
    onCommentChange: (Int, String) -> Unit,
    onImageChange: (Int, Uri?) -> Unit,
    onSubmitClick: () -> Unit
) {
    var activeProductIndex by remember { mutableStateOf<Int?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        activeProductIndex?.let { index ->
            if (uri != null) {
                onImageChange(index, uri)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3FA))
    ) {
        // Toolbar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF3D3450))
            }
            Text(
                text = "Đánh giá sản phẩm",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF3D3450),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            reviewItems.forEachIndexed { index, item ->
                val currentRating = uiState.ratings[index] ?: 5
                val currentComment = uiState.comments[index] ?: ""
                val currentUri = uiState.imagesMap[index]

                val matchingProduct = allProducts.find {
                    (it.id != 0L && it.id == item.productId) ||
                            (it.name.trim().equals(item.productName.trim(), ignoreCase = true))
                }
                val productImageUrl = matchingProduct?.imageUrl

                // Product Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = productImageUrl?.toFullImageUrl(),
                            contentDescription = item.productName,
                            placeholder = painterResource(R.drawable.loading_img),
                            error = painterResource(R.drawable.error_img),
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.productName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF3D3450)
                            )
                            if (!item.size.isNullOrBlank()) {
                                Text(text = "Size: ${item.size}", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }

                CommonSpace()

                // Rating Section
                Text(
                    text = "CHỌN ĐỂ ĐÁNH GIÁ",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8278A0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { starIndex ->
                        val currentStar = starIndex + 1
                        Icon(
                            imageVector = if (currentStar <= currentRating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            tint = if (currentStar <= currentRating) Color(0xFF8278A0)
                            else Color(0xFFD1D1D1),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onRatingChange(index, currentStar) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Comment Section
                Text(
                    text = "Để lại bình luận",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D3450)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currentComment,
                    onValueChange = { onCommentChange(index, it) },
                    placeholder = {
                        Text(
                            "Hãy chia sẻ chi tiết về trải nghiệm đồ uống của bạn...",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Image Selection
                Text(
                    text = "Thêm ảnh",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D3450)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable {
                                activeProductIndex = index
                                launcher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                            Text(text = "Tải lên", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    currentUri?.let { uri ->
                        Box(modifier = Modifier.size(80.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { onImageChange(index, null) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .padding(4.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.5f),
                                        RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                CommonSpace(32.dp)
            }

            // Submit Button
            Button(
                onClick = onSubmitClick,
                enabled = !uiState.isSubmitting && reviewItems.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3D3450),
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(text = "Đánh giá", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ReviewScreenPreview() {
    val mockReviewItems = listOf(
        OrderItemDto(
            productId = 101,
            productName = "Cà phê Sữa Đá",
            size = "M",
            unitPrice = BigDecimal(29000),
            quantity = 2,
        ),
        OrderItemDto(
            productId = 102,
            productName = "Trà Sữa Khoai Môn",
            size = "L",
            unitPrice = BigDecimal(35000),
            quantity = 1,
        )
    )

    val mockAllProducts = listOf(
        ProductDto(
            id = 101,
            name = "Cà phê Sữa Đá",
            description = "Cà phê đậm đà pha với sữa đặc có đường",
            basePrice = 29000.0,
            imageUrl = "https://example.com/caphesua.jpg",
            categoryId = 1,
            isActive = true,
            isDeleted = false,
            rating = 4.5,
            reviewers = 120,
            size = emptyList() // Hoặc truyền List<ProductSizeDto> nếu cần thiết
        ),
        ProductDto(
            id = 102,
            name = "Trà Sữa Khoai Môn",
            description = "Trà sữa vị khoai môn thơm ngon",
            basePrice = 35000.0,
            imageUrl = "https://example.com/trasuakhoaimon.jpg",
            categoryId = 2,
            isActive = true,
            isDeleted = false,
            rating = 4.8,
            reviewers = 85,
            size = emptyList()
        )
    )

    CoffeeShopAppTheme {
        ReviewContent(
            reviewItems = mockReviewItems,
            allProducts = mockAllProducts,
            uiState = UserReviewUiState(isSubmitting = false, error = null),
            onBack = {},
            onRatingChange = { _, _ -> },
            onCommentChange = { _, _ -> },
            onImageChange = { _, _ -> },
            onSubmitClick = { }
        )
    }
}