package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.model.dto.ProductRequestDto
import com.example.coffeeshopapp.data.repository.ProductRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class UpdateProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long, requestDto: ProductRequestDto, image: MultipartBody.Part?): ApiResponseDto<ProductDto> {
        val jsonRequest = Gson().toJson(requestDto)
        val requestBody = jsonRequest.toRequestBody("application/json".toMediaTypeOrNull())
        return repository.updateProduct(id, requestBody, image)
    }
}
