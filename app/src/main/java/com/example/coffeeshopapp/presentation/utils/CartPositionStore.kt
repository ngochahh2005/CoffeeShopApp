package com.example.coffeeshopapp.presentation.utils

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CartPositionStore {
    private val _cartOffset = MutableStateFlow(Offset.Zero)
    val cartOffset = _cartOffset.asStateFlow()

    fun update(offset: Offset) {
        _cartOffset.value = offset
    }
}
