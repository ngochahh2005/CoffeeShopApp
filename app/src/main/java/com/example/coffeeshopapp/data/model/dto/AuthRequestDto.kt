package com.example.coffeeshopapp.data.model.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("accessTokenExp") val accessTokenExp: Long,
    @SerializedName("refreshToken") val refreshToken: String?,
    @SerializedName("refreshTokenExp") val refreshTokenExp: Long?,
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("userId") val userId: Long
)