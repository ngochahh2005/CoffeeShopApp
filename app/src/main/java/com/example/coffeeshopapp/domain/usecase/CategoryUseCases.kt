package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.repository.CategoryRepository

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): ApiResponseDto<List<CategoryDto>> {
        return repository.getCategories()
    }
}

class CreateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(dto: CategoryDto): ApiResponseDto<CategoryDto> {
        return repository.createCategory(dto)
    }
}

class UpdateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long, dto: CategoryDto): ApiResponseDto<CategoryDto> {
        return repository.updateCategory(id, dto)
    }
}

class DeleteCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long): ApiResponseDto<Any> {
        return repository.deleteCategory(id)
    }
}
