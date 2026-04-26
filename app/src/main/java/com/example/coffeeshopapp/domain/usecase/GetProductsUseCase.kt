package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.repository.ProductRepository

class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): ApiResponseDto<List<ProductDto>> {
        return repository.getProducts()
    }
}
