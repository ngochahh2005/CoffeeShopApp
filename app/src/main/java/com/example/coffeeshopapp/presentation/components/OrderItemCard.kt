package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.CartItem
import com.example.coffeeshopapp.presentation.theme.k2d
import com.example.coffeeshopapp.utils.formatGrouped
import com.example.coffeeshopapp.utils.toFullImageUrl

@Composable
fun OrderItemCard(
    item: CartItem,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrlAtAdd.toFullImageUrl(),
                placeholder = painterResource(R.drawable.loading_img),
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