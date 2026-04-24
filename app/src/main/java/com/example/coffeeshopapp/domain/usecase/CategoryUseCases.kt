package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.CategoryDto
import com.example.coffeeshopapp.data.model.dto.CategoryRequestDto
import com.example.coffeeshopapp.data.repository.CategoryRepository
import okhttp3.MultipartBody

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(): ApiResponseDto<List<CategoryDto>> {
        return repository.getCategories()
    }
}

class GetCategoryByIdUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long): ApiResponseDto<CategoryDto> {
        return repository.getCategoryById(id)
    }
}

class CreateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(
        dto: CategoryRequestDto,
        image: MultipartBody.Part?
    ): ApiResponseDto<CategoryDto> {
        return repository.createCategory(dto, image)
    }
}

class UpdateCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(
        id: Long,
        dto: CategoryRequestDto,
        image: MultipartBody.Part?
    ): ApiResponseDto<CategoryDto> {
        return repository.updateCategory(id, dto, image)
    }
}

class DeleteCategoryUseCase(private val repository: CategoryRepository) {
    suspend operator fun invoke(id: Long): ApiResponseDto<Any> {
        return repository.deleteCategory(id)
    }
}
