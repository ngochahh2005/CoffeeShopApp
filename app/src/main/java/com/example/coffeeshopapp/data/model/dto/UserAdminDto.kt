package com.example.coffeeshopapp.data.model.dto

import com.example.coffeeshopapp.data.remote.RoleDto

data class UserResponseDto(
    val id: Long = 0,
    val username: String = "",
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val dob: String? = null,
    val avt: String? = null,
    val provider: String? = null,
    val roles: List<RoleDto>? = null
)

data class UserCreateRequestDto(
    val username: String = "",
    val password: String = "",
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val avt: String? = null
)

data class UserUpdateRequestDto(
    val password: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dob: String? = null,
    val roleIds: List<Long>? = null,
    val avt: String? = null
)
