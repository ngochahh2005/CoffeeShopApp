package com.example.coffeeshopapp.domain.usecase

import com.example.coffeeshopapp.data.model.dto.ApiResponseDto
import com.example.coffeeshopapp.data.model.dto.ToppingDto
import com.example.coffeeshopapp.data.model.dto.ToppingRequestDto
import com.example.coffeeshopapp.data.repository.ToppingRepository
import okhttp3.MultipartBody

class GetToppingsUseCase(private val repository: ToppingRepository) {
    suspend operator fun invoke(): ApiResponseDto<List<ToppingDto>> = repository.getToppings()
}

class GetToppingByIdUseCase(private val repository: ToppingRepository) {
    suspend operator fun invoke(id: Long): ApiResponseDto<ToppingDto> = repository.getToppingById(id)
}

class CreateToppingUseCase(private val repository: ToppingRepository) {
    suspend operator fun invoke(dto: ToppingRequestDto, image: MultipartBody.Part?): ApiResponseDto<ToppingDto> =
        repository.createTopping(dto, image)
}

class UpdateToppingUseCase(private val repository: ToppingRepository) {
    suspend operator fun invoke(id: Long, dto: ToppingRequestDto, image: MultipartBody.Part?): ApiResponseDto<ToppingDto> =
        repository.updateTopping(id, dto, image)
}

class DeleteToppingUseCase(private val repository: ToppingRepository) {
    suspend operator fun invoke(id: Long): ApiResponseDto<Any> = repository.deleteTopping(id)
}
