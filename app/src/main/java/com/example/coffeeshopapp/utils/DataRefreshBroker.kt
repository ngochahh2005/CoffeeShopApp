package com.example.coffeeshopapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object DataRefreshBroker {
    private val _refreshEvent = MutableSharedFlow<RefreshType>(extraBufferCapacity = 1)
    val refreshEvent = _refreshEvent.asSharedFlow()

    fun notifyDataChanged(type: RefreshType = RefreshType.ALL) {
        _refreshEvent.tryEmit(type)
    }
}

enum class RefreshType {
    PRODUCTS,
    CATEGORIES,
    TOPPINGS,
    ALL
}
