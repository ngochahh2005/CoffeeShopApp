package com.example.coffeeshopapp.presentation.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.model.dto.ReviewDto
import com.example.coffeeshopapp.presentation.viewmodel.AdminReviewViewModel
import com.example.coffeeshopapp.utils.toFullImageUrl

private val Brown = Color(0xFF553722)
private val Red = Color(0xFFFF3B30)
private val StarColor = Color(0xFFFFB300)
private val Bg = Color(0xFFF7F8FA)
private val Card = Color(0xFFFFFFFF)
private val Sub = Color(0xFF8E8E93)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewManagementScreen(viewModel: AdminReviewViewModel, onBackClick: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    // Delete dialog
    if (state.showDeleteDialog && state.selectedReview != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteDialog,
            title = { Text("Xác nhận xoá", fontWeight = FontWeight.SemiBold) },
            text = { Text("Bạn có chắc muốn xoá đánh giá của \"${state.selectedReview!!.username}\"?") },
            confirmButton = { TextButton(onClick = viewModel::confirmDelete) { Text("Xoá", color = Red) } },
            dismissButton = { TextButton(onClick = viewModel::dismissDeleteDialog) { Text("Huỷ") } }
        )
    }

    // Detail bottom sheet
    if (state.showDetailSheet && state.selectedReview != null) {
        ModalBottomSheet(onDismissRequest = viewModel::dismissDetail, containerColor = Card) {
            ReviewDetailSheet(review = state.selectedReview!!)
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text("Quản lý đánh giá", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Brown) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Brown) }
            state.error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Lỗi: ${state.error}", color = Red)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadReviews() }, colors = ButtonDefaults.buttonColors(containerColor = Brown)) { Text("Thử lại") }
                }
            }
            state.reviews.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("Chưa có đánh giá nào.", color = Sub) }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.reviews) { review ->
                        ReviewCard(review = review, onDetail = { viewModel.showDetail(review) }, onDelete = { viewModel.showDeleteDialog(review) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewDto, onDetail: () -> Unit, onDelete: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Card, shadowElevation = 2.dp, onClick = onDetail) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
            // Avatar
            Surface(Modifier.size(40.dp), shape = CircleShape, color = Brown.copy(0.12f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(review.username?.take(1)?.uppercase() ?: "?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Brown)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(review.username ?: "Ẩn danh", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Brown)
                    Spacer(Modifier.width(8.dp))
                    repeat(review.rating.coerceIn(0, 5)) { Icon(Icons.Default.Star, null, tint = StarColor, modifier = Modifier.size(14.dp)) }
                    repeat((5 - review.rating).coerceAtLeast(0)) { Icon(Icons.Default.StarBorder, null, tint = Sub.copy(0.4f), modifier = Modifier.size(14.dp)) }
                }
                Text(review.productName ?: "", fontSize = 12.sp, color = Sub)
                if (!review.comment.isNullOrBlank()) {
                    Text(review.comment, fontSize = 13.sp, color = Color(0xFF555555), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                }
                if (review.createdAt != null) { Text(review.createdAt.take(10), fontSize = 10.sp, color = Sub, modifier = Modifier.padding(top = 2.dp)) }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, null, tint = Red, modifier = Modifier.size(18.dp)) }
        }
    }
}

@Composable
private fun ReviewDetailSheet(review: ReviewDto) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("Chi tiết đánh giá", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Brown)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(48.dp), shape = CircleShape, color = Brown.copy(0.12f)) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(review.username?.take(1)?.uppercase() ?: "?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Brown) }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(review.username ?: "Ẩn danh", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Brown)
                Row { repeat(review.rating.coerceIn(0, 5)) { Icon(Icons.Default.Star, null, tint = StarColor, modifier = Modifier.size(18.dp)) } }
            }
        }
        Spacer(Modifier.height(12.dp))
        RDetailLine("Sản phẩm", review.productName ?: "—")
        RDetailLine("Ngày đánh giá", review.createdAt?.take(10) ?: "—")
        Spacer(Modifier.height(8.dp))
        Text("Nội dung", fontSize = 13.sp, color = Sub)
        Text(review.comment ?: "Không có nội dung", fontSize = 14.sp, color = Color(0xFF333333), modifier = Modifier.padding(top = 4.dp))

        if (!review.imageUrl.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            val url = review.imageUrl.toFullImageUrl()
            AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun RDetailLine(l: String, v: String) { Row(Modifier.padding(vertical = 2.dp)) { Text(l, fontSize = 12.sp, color = Sub, modifier = Modifier.width(110.dp)); Text(v, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Brown) } }
