package com.example.coffeeshopapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Trung gian điều phối các sự kiện làm mới dữ liệu toàn cục.
 * Giúp màn hình Home hoặc các màn hình khác biết khi nào cần tải lại dữ liệu mà không cần load lại liên tục.
 */
object DataRefreshBroker {
    private val _refreshEvent = MutableSharedFlow<RefreshType>(extraBufferCapacity = 1)
    val refreshEvent = _refreshEvent.asSharedFlow()

    /**
     * Phát sự kiện yêu cầu làm mới dữ liệu.
     */
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
