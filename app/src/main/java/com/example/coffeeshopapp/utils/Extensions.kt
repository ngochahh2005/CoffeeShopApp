package com.example.coffeeshopapp.utils

import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.ToppingDto
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.remote.NetworkClient
import org.json.JSONObject
import retrofit2.HttpException
import java.net.URI

fun Exception.getErrorMessage(): String {
    if (this is HttpException) {
        try {
            val errorBody = this.response()?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val json = JSONObject(errorBody)
                if (json.has("message")) {
                    return json.getString("message")
                }
            }
        } catch (e: Exception) {
            // Bỏ qua và dùng thông báo lỗi mặc định
        }
    }
    return this.message ?: "Lỗi không xác định"
}

private fun baseOriginFrom(baseUrl: String): String {
    val trimmed = baseUrl.trimEnd('/')
    return try {
        val uri = URI(trimmed)
        val scheme = uri.scheme
        val authority = uri.authority
        if (!scheme.isNullOrBlank() && !authority.isNullOrBlank()) "$scheme://$authority" else trimmed
    } catch (_: Exception) {
        trimmed
    }
}

private fun rewriteLocalhostIfNeeded(baseUrl: String, absoluteUrl: String): String {
    return try {
        val uri = URI(absoluteUrl)
        val host = uri.host?.lowercase()
        if (host == "localhost" || host == "127.0.0.1") {
            val origin = baseOriginFrom(baseUrl)
            val path = uri.rawPath ?: ""
            val query = uri.rawQuery?.let { "?$it" } ?: ""
            val fragment = uri.rawFragment?.let { "#$it" } ?: ""
            origin + path + query + fragment
        } else {
            absoluteUrl
        }
    } catch (_: Exception) {
        absoluteUrl
    }
}

private fun buildAbsoluteUrl(baseUrl: String, raw: String): String {
    val baseTrimmed = baseUrl.trimEnd('/')

    return when {
        raw.startsWith("http://") || raw.startsWith("https://") -> rewriteLocalhostIfNeeded(baseUrl, raw)
        raw.startsWith("/") -> baseTrimmed + raw
        else -> "$baseTrimmed/$raw"
    }
}

fun Product.getFullImageUrl(): String? {
    return this.imageUrl.toFullImageUrl()
}

fun String?.toFullImageUrl(): String? {
    if (this.isNullOrBlank()) return null
    return buildAbsoluteUrl(NetworkClient.BASE_URL, this)
}

fun ProductDto.isActiveResolved(): Boolean {
    return this.isActive ?: (this.isDeleted?.not() ?: true)
}

fun CategoryDto.isActiveResolved(): Boolean {
    return this.isActive ?: true
}

fun ToppingDto.isActiveResolved(): Boolean {
    return this.isActive ?: true
}
