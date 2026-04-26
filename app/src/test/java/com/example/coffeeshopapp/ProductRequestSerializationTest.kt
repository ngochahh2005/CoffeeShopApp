package com.example.coffeeshopapp

import com.example.coffeeshopapp.data.model.dto.ProductRequestDto
import com.example.coffeeshopapp.data.model.dto.ProductSizeRequestDto
import com.google.gson.Gson
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductRequestSerializationTest {
    @Test
    fun gsonSerializesPricesAsIntegers_withoutDecimalPart() {
        val dto = ProductRequestDto(
            name = "A",
            description = null,
            basePrice = 40000L,
            categoryId = 1L,
            sizes = listOf(
                ProductSizeRequestDto(sizeName = "M", priceExtra = 5000L)
            )
        )

        val json = Gson().toJson(dto)
        assertTrue(json.contains("\"basePrice\":40000"))
        assertTrue(json.contains("\"priceExtra\":5000"))
        assertFalse("Should not serialize basePrice with .0", json.contains("\"basePrice\":40000.0"))
        assertFalse("Should not serialize priceExtra with .0", json.contains("\"priceExtra\":5000.0"))
    }
}

