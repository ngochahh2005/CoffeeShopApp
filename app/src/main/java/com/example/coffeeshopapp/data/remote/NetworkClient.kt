package com.example.coffeeshopapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
  // Android Emulator: 10.0.2.2 trỏ về localhost của máy tính (BE chạy cổng 8080)
  const val BASE_URL = "http://10.0.2.2:8080/"

  private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
  }

  private val okHttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
      val req = chain.request().newBuilder().apply {
        val token = com.example.coffeeshopapp.data.TokenProvider.token
        if (!token.isNullOrEmpty()) addHeader("Authorization", "Bearer $token")
      }.build()
      chain.proceed(req)
    }
    .build()

  private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttp)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  val api: ApiService = retrofit.create(ApiService::class.java)
}
