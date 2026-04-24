package com.example.coffeeshopapp

import com.example.coffeeshopapp.data.remote.NetworkClient
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class BaseUrlTest {
    @Test
    fun baseUrl_mustBeHttpPort8080_andEndWithSlash() {
        val uri = URI(NetworkClient.BASE_URL)
        assertTrue("BASE_URL must use http", uri.scheme == "http")
        assertTrue("BASE_URL must use port 8080", uri.port == 8080)
        assertTrue("BASE_URL must end with '/'", NetworkClient.BASE_URL.endsWith("/"))
    }
}

