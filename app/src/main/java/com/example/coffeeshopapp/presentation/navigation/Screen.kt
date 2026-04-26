package com.example.coffeeshopapp.presentation.navigation

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Register: Screen(route = "register")
    object UserHome: Screen("user_home")
    object AdminDashboard: Screen("admin_dashboard")
    object AdminCategory: Screen("admin_category")
    object AdminProduct: Screen("admin_product?productId={productId}") {
        fun createRoute(productId: Long) = "admin_product?productId=$productId"
    }
    object AdminUsers: Screen("admin_users")
    object AdminOrders: Screen("admin_orders?initialTab={initialTab}") {
        fun createRoute(initialTab: String = "ALL") = "admin_orders?initialTab=$initialTab"
    }
    object AdminPromotions: Screen("admin_promotions")
    object AdminToppings: Screen("admin_toppings")
    object AdminReviews: Screen("admin_reviews")
    object ProductList: Screen("product_list")
    object Cart: Screen("cart")
    object OrderHistory: Screen("order_history")
    object OrderDetails: Screen("order_details")
    object Favourites: Screen("favourites")
    object ResetPassword: Screen("reset_password")
    object Profile: Screen("profile")
    object ProductDetail: Screen("product_detail")
}

val screenWithBottomBar = listOf(
    Screen.UserHome.route,
    Screen.Favourites.route,
    Screen.Cart.route,
    Screen.Profile.route,
    Screen.ProductDetail.route
)


