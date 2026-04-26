package com.example.coffeeshopapp

import com.example.coffeeshopapp.utils.toFullImageUrl
import com.example.coffeeshopapp.data.remote.NetworkClient
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageUrlExtensionsTest {
    private val origin = NetworkClient.BASE_URL.trimEnd('/')

    @Test
    fun toFullImageUrl_relativePathWithLeadingSlash_joinsWithSingleSlash() {
        assertEquals(
            "$origin/uploads/a.jpg",
            "/uploads/a.jpg".toFullImageUrl()
        )
    }

    @Test
    fun toFullImageUrl_relativePathWithoutLeadingSlash_joinsWithSingleSlash() {
        assertEquals(
            "$origin/uploads/a.jpg",
            "uploads/a.jpg".toFullImageUrl()
        )
    }

    @Test
    fun toFullImageUrl_localhostAbsoluteUrl_rewritesToBaseHost_forEmulator() {
        assertEquals(
            "$origin/uploads/a.jpg",
            "http://localhost:8080/uploads/a.jpg".toFullImageUrl()
        )
    }
}
