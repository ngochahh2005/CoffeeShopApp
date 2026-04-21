package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import com.example.coffeeshopapp.presentation.theme.LabelColor
import com.example.coffeeshopapp.presentation.theme.PlaceHolderColor
import com.example.coffeeshopapp.presentation.theme.TextColor
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel

@Composable
fun SearchingTextField(
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = viewModel.searchKeyWords,
        onValueChange = { viewModel.onSearchKeyWordsChange(it) },
        modifier = modifier,
        shape = RoundedCornerShape(36.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LabelColor,
            unfocusedBorderColor = LabelColor,

            focusedTextColor = TextColor,
            unfocusedTextColor = TextColor,

        ),
        placeholder = {
            Text(text = "Nhập tên sản phẩm...", color = PlaceHolderColor, style = MaterialTheme.typography.bodyMedium)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = LabelColor)
        },
        singleLine = true,
    )
}

@Composable
@Preview(name = "Searching TextField")
fun SearchPreview() {
    CoffeeShopAppTheme {
        SearchingTextField()
    }
}