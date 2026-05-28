package com.example.coffeeshopapp.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.formatToVietnameseDate(): String {
    return try {
        if (this.contains("T")) {
            val cleanInput = if (this.contains(".")) this.substringBefore(".") else this
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            val outputFormatter = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())

            val date = inputFormatter.parse(cleanInput)
            if (date != null) {
                outputFormatter.format(date)
            } else {
                this
            }
        } else {
            this
        }
    } catch (e: Exception) {
        e.printStackTrace()
        this
    }
}