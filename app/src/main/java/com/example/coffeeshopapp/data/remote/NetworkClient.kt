package com.example.coffeeshopapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import kotlinx.coroutines.runBlocking
import com.example.coffeeshopapp.data.TokenProvider
import com.example.coffeeshopapp.data.local.AuthDataStore

object NetworkClient {
  const val BASE_URL = "http://10.0.2.2:8080/"

  private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
  }

  private val authAuthenticator = object : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = TokenProvider.refreshToken
        if (refreshToken.isNullOrEmpty()) return null

        if (response.request.url.encodedPath.contains("/auth/refresh")) {
            return null
        }

        synchronized(this) {
            val newToken = TokenProvider.token
            if (response.request.header("Authorization") != "Bearer $newToken") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            }

            try {
                val call = retrofitForRefresh.create(ApiService::class.java).refreshToken(RefreshRequestDto(refreshToken))
                val refreshResponse = call.execute()

                if (refreshResponse.isSuccessful && refreshResponse.body()?.code == 1000) {
                    val newAccessToken = refreshResponse.body()?.result?.accessToken
                    val newRefreshToken = refreshResponse.body()?.result?.refreshToken

                    if (newAccessToken != null) {
                        TokenProvider.token = newAccessToken
                        if (newRefreshToken != null) TokenProvider.refreshToken = newRefreshToken

                        TokenProvider.context?.let { ctx ->
                            runBlocking {
                                AuthDataStore.setToken(ctx, newAccessToken, TokenProvider.refreshToken)
                            }
                        }

                        return response.request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()
                    }
                } else {
                    TokenProvider.token = null
                    TokenProvider.refreshToken = null
                    TokenProvider.context?.let { ctx ->
                        runBlocking {
                            AuthDataStore.clearAll(ctx)
                        }
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            android.widget.Toast.makeText(ctx, "Phiên đăng nhập hết hạn vui lòng đăng nhập lại", android.widget.Toast.LENGTH_LONG).show()
                            val intent = android.content.Intent(ctx, com.example.coffeeshopapp.MainActivity::class.java).apply {
                                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            ctx.startActivity(intent)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
  }

  private val okHttpForRefresh = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

  private val retrofitForRefresh = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpForRefresh)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  private val okHttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .authenticator(authAuthenticator)
    .addInterceptor { chain ->
      val req = chain.request().newBuilder().apply {
        val token = TokenProvider.token
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
