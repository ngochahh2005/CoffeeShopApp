package com.example.coffeeshopapp.presentation.screen.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.presentation.theme.BackgroundColor
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.CoffeeTextColor
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.Pacifico
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TitleSmallColor

@Composable
fun ProductDetailScreen(
    product: Product
) {
    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Chi tiết sản phẩm", fontSize = 36.sp, fontFamily = Pacifico, color = TitleSmallColor)

        Image(
            painter = painterResource(R.drawable.st_bo),
            contentDescription = null,
            modifier = Modifier.clip(RoundedCornerShape(24.dp)).shadow(4.dp).fillMaxWidth().fillMaxHeight(.4f),
            contentScale = ContentScale.Crop
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium, color = CoffeeTextColor)

            var isFavorite by remember { mutableStateOf(false) }

            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier.align(Alignment.CenterVertically).size(36.dp)
            ) {
                if (isFavorite) Icon(Icons.Filled.Favorite, contentDescription = null, tint = PlaceHolderColor)
                else Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = PlaceHolderColor)
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ProductDetailScreenPreview() {
    CoffeeShopAppTheme {
        ProductDetailScreen(
            product = Product("1", "Sinh tố bơ", 25000, imageUrl = "", description = "Bơ sáp Daklak xay nhuyễn")
        )
    }
}