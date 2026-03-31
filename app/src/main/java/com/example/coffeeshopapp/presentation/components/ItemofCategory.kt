package com.example.coffeeshopapp.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.coffeeCategories
import com.example.coffeeshopapp.data.model.Category
import com.example.coffeeshopapp.presentation.theme.CardBackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor

@Composable
fun Categories(
    categories: List<Category> = coffeeCategories,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
    ) {
        items(coffeeCategories, key = { it.id }) { category ->
            ItemOfCategories(
                coffeeIcon = category.icon,
                coffeeName = category.name,
                onClick = { onCategoryClick(category.id.toString()) }
            )
        }
    }
}

@Composable
fun ItemOfCategories(
    @DrawableRes coffeeIcon: Int,
    coffeeName: String,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .size(100.dp, 120.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(CardBackgroundColor)
        .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.icon_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(.6f)
                        .padding(top = 5.dp)
                        .align(Alignment.TopCenter)
                )
                Image(
                    painter = painterResource(coffeeIcon),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(.6f)
                        .padding(top = 5.dp)
                        .align(Alignment.TopCenter)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = coffeeName, color = LabelColor, style = MaterialTheme.typography.bodySmall)
        }
    }
}
