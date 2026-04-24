package com.example.coffeeshopapp.utils

import java.text.DecimalFormat

fun Long.formatGrouped(): String {
    return try {
        DecimalFormat("#,###").format(this)
    } catch (_: Exception) {
        this.toString()
    }
}

fun Double.formatGrouped(): String = this.toLong().formatGrouped()

