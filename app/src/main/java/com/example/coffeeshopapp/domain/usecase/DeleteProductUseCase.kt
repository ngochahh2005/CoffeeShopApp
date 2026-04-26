package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.repository.ProductRepository

class DeleteProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): ApiResponseDto<Any> {
        return repository.deleteProduct(id)
    }
}
