package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.data.repository.ProductRepository

class GetProductsByCategoryUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(categoryId: Long): ApiResponseDto<List<ProductDto>> {
        return repository.getProductsByCategory(categoryId)
    }
}
