package com.example.coffeeshopapp.utils

import com.example.coffeeshopapp.data.model.entity.Product

fun Product.getFullImageUrl(): String? {
    val url = this.imageUrl
    return when {
        url.isNullOrBlank() -> null
        url.startsWith("http") -> url
        url.startsWith("/") -> AppConstants.baseUrl + url
        else -> "${AppConstants.baseUrl}/$url"
    }
}

fun String?.toFullImageUrl(): String? {
    if (this.isNullOrBlank()) return null
    return when {
        this.isNullOrBlank() -> null
        this.startsWith("http") -> this
        this.startsWith("/") -> "${AppConstants.baseUrl}$this"
        else -> "${AppConstants.baseUrl}/$this"
    }
}