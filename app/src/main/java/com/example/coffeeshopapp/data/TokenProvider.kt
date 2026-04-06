package com.example.coffeeshopapp.data

object TokenProvider {
    // volatile token used by OkHttp interceptor
    @Volatile
    var token: String? = null
}
