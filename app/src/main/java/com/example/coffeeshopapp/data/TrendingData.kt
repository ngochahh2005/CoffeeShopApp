package com.example.coffeeshopapp.data

import com.example.coffeeshopapp.data.model.entity.Product

var trendingCoffeeList = listOf(
    Product(
        id = "1",
        name = "Latte",
        price = 69000,
        rating = 4.5,
        reviewers = 100251
    ),
    Product(
        id = "2",
        name = "Americano",
        price = 10,
        imageUrl = null,
        rating = 5.0,
        reviewers = 100
    ),
    Product(
        id = "3",
        name = "Cold Brew",
        price = 15,
        rating = 4.8,
        reviewers = 500
    )
)