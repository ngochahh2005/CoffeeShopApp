package com.example.coffeeshopapp.data

import com.example.coffeeshopapp.R

data class CoffeeItem(
    val id: String,
    val name: String,
    val price: Long,
    val icon: Int,
    val rating: Double,
    val reviewers: Int,
    var isFavorite: Boolean = false
) {
    fun getPrice(): String {
        var res: String = ""
        var price = this.price
        while (price > 0) {
            res = if (price % 1000 == 0.toLong()) "000.$res"
            else "${price % 1000}.$res"
            price /= 1000
        }
        res = res.substring(0, res.length - 1)
        return " $res ₫"
    }
}

var trendingCoffeeList = listOf(
    CoffeeItem(
        id = "1",
        name = "Latte",
        price = 69000,
        icon = R.drawable.icon_latte,
        rating = 4.5,
        reviewers = 100251
    ),
    CoffeeItem(
        id = "2",
        name = "Americano",
        price = 10,
        icon = R.drawable.icon_americano,
        rating = 5.0,
        reviewers = 100
    ),
    CoffeeItem(
        id = "3",
        name = "Cold Brew",
        price = 15,
        icon = R.drawable.icon_cold_brew,
        rating = 4.8,
        reviewers = 500
    )
)