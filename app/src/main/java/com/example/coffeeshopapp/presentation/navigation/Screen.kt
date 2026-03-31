package com.example.coffeeshopapp.presentation.navigation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Register: Screen(route = "register")
    object UserHome: Screen("user_home")
    object AdminHome: Screen("admin_home")
    object ProductList: Screen("product_list")
    object Cart: Screen("cart")
    object OrderHistory: Screen("order_history")
    object OrderDetails: Screen("order_details")
    object Favourites: Screen("favourites")
    object ResetPassword: Screen("reset_password")
    object Profile: Screen("profile")
}

val screenWithBottomBar = listOf(
    Screen.UserHome.route,
    Screen.Favourites.route,
    Screen.Cart.route,
    Screen.Profile.route
)