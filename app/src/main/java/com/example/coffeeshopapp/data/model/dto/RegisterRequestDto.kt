package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("email") val email: String?,
//    @SerializedName("firstName") val firstName: String?,
//    @SerializedName("lastName") val lastName: String?,
//    @SerializedName("phoneNumber") val phoneNumber: String?,
//    @SerializedName("dob") val dob: String?
)