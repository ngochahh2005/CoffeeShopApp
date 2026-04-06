package com.example.coffeeshopapp.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun uriToImagePart(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri) ?: "image/*"
    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
        ?: throw IllegalArgumentException("Cannot read uri: $uri")
    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
    val fileName = "category_${System.currentTimeMillis()}"
    return MultipartBody.Part.createFormData(partName, fileName, requestBody)
}
