package com.example.coffeeshopapp.data

object TokenProvider {
    // volatile token used by OkHttp interceptor
    @Volatile
    var token: String? = null
    
    @Volatile
    var refreshToken: String? = null
    
    @Volatile
    var context: android.content.Context? = null
}
