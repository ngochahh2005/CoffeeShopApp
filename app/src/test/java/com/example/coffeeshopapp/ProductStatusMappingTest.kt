package com.example.coffeeshopapp

import com.example.coffeeshopapp.data.model.dto.ProductDto
import com.example.coffeeshopapp.utils.isActiveResolved
import com.google.gson.Gson
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductStatusMappingTest {
    private val gson = Gson()

    private fun parse(json: String): ProductDto = gson.fromJson(json, ProductDto::class.java)

    @Test
    fun status_activeField_true_mapsToActive() {
        val dto = parse(
            """
            {
              "id": 1,
              "name": "A",
              "description": null,
              "basePrice": 10000,
              "imageUrl": null,
              "categoryId": 1,
              "active": true,
              "sizes": []
            }
            """.trimIndent()
        )

        assertTrue(dto.isActiveResolved())
    }

    @Test
    fun status_isActiveField_false_mapsToInactive() {
        val dto = parse(
            """
            {
              "id": 1,
              "name": "A",
              "description": null,
              "basePrice": 10000,
              "imageUrl": null,
              "categoryId": 1,
              "isActive": false,
              "sizes": []
            }
            """.trimIndent()
        )

        assertFalse(dto.isActiveResolved())
    }

    @Test
    fun status_isDeleted_false_mapsToActive_whenIsActiveMissing() {
        val dto = parse(
            """
            {
              "id": 1,
              "name": "A",
              "description": null,
              "basePrice": 10000,
              "imageUrl": null,
              "categoryId": 1,
              "isDeleted": false,
              "sizes": []
            }
            """.trimIndent()
        )

        assertTrue(dto.isActiveResolved())
    }

    @Test
    fun status_isDeleted_true_mapsToInactive_whenIsActiveMissing() {
        val dto = parse(
            """
            {
              "id": 1,
              "name": "A",
              "description": null,
              "basePrice": 10000,
              "imageUrl": null,
              "categoryId": 1,
              "isDeleted": true,
              "sizes": []
            }
            """.trimIndent()
        )

        assertFalse(dto.isActiveResolved())
    }

    @Test
    fun status_missingFields_defaultsToActive() {
        val dto = parse(
            """
            {
              "id": 1,
              "name": "A",
              "description": null,
              "basePrice": 10000,
              "imageUrl": null,
              "categoryId": 1,
              "sizes": []
            }
            """.trimIndent()
        )

        assertTrue(dto.isActiveResolved())
    }
}

