package com.example.coffeeshopapp.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.repository.AdminRepository
import com.example.coffeeshopapp.data.repository.CategoryRepository
import com.example.coffeeshopapp.domain.usecase.CreateCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoriesUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoryByIdUseCase
import com.example.coffeeshopapp.domain.usecase.GetProductsByCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateCategoryUseCase
import com.example.coffeeshopapp.presentation.screen.admin.DashboardScreen
import com.example.coffeeshopapp.presentation.screen.admin.OrderManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.PromotionManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.ReviewManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.UserManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.category.AdminCategoryScreen
import com.example.coffeeshopapp.presentation.screen.auth.ForgotPasswordScreen
import com.example.coffeeshopapp.presentation.screen.auth.LoginScreen
import com.example.coffeeshopapp.presentation.screen.auth.RegisterScreen
import com.example.coffeeshopapp.presentation.screen.user.CartScreen
import com.example.coffeeshopapp.presentation.screen.user.ProfileScreen
import com.example.coffeeshopapp.presentation.screen.user.favorite.FavouritesScreen
import com.example.coffeeshopapp.presentation.screen.user.home.HomeScreen
import com.example.coffeeshopapp.presentation.theme.AdminScreenTheme
import com.example.coffeeshopapp.presentation.viewmodel.*

@SuppressLint("RestrictedApi")
@Composable
fun NavGraph(innerPadding: PaddingValues, navController: NavHostController) {
    val sharedHomeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = Modifier.fillMaxSize().padding(innerPadding),
    ) {
        composable(route = Screen.UserHome.route) { backStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(backStackEntry)
            HomeScreen(
                viewModel = homeViewModel,
                openFavouritesScreen = { navController.navigate(Screen.Favourites.route) },
                openCartScreen = { navController.navigate(Screen.Cart.route) },
                openProfileScreen = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(route = Screen.Favourites.route) {
            val homeBackStackEntry = remember(it) {
                navController.currentBackStack.value.find { entry ->
                    entry.destination.route == Screen.UserHome.route
                }
            }
            val homeViewModel: HomeViewModel = if (homeBackStackEntry != null) {
                viewModel(homeBackStackEntry)
            } else {
                sharedHomeViewModel
            }
            FavouritesScreen(viewModel = homeViewModel)
        }

        composable(route = Screen.Cart.route) { CartScreen() }

        composable(route = Screen.Profile.route) {
            ProfileScreen(onOpenAdmin = { navController.navigate(Screen.AdminDashboard.route) })
        }

        // ─── Admin Dashboard ───
        composable(route = Screen.AdminDashboard.route) {
            val vm: DashboardViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DashboardViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme {
                DashboardScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onOpenCategory = { navController.navigate(Screen.AdminCategory.route) },
                    onOpenProduct = { navController.navigate(Screen.AdminProduct.createRoute(-1L)) },
                    onOpenUsers = { navController.navigate(Screen.AdminUsers.route) },
                    onOpenOrders = { status -> navController.navigate(Screen.AdminOrders.createRoute(status)) },
                    onOpenPromotions = { navController.navigate(Screen.AdminPromotions.route) },
                    onOpenReviews = { navController.navigate(Screen.AdminReviews.route) }
                )
            }
        }

        // ─── Admin Category ───
        composable(route = Screen.AdminCategory.route) {
            val viewModel: AdminCategoryViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = CategoryRepository(NetworkClient.api)
                    return AdminCategoryViewModel(
                        getCategoriesUseCase = GetCategoriesUseCase(repository),
                        getCategoryByIdUseCase = GetCategoryByIdUseCase(repository),
                        createCategoryUseCase = CreateCategoryUseCase(repository),
                        updateCategoryUseCase = UpdateCategoryUseCase(repository),
                        deleteCategoryUseCase = DeleteCategoryUseCase(repository),
                        getProductsByCategoryUseCase = GetProductsByCategoryUseCase(
                            com.example.coffeeshopapp.data.repository.ProductRepository(NetworkClient.api)
                        )
                    ) as T
                }
            })
            AdminScreenTheme {
                AdminCategoryScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // ─── Admin Users ───
        composable(route = Screen.AdminUsers.route) {
            val vm: AdminUserViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminUserViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme { UserManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // ─── Admin Orders ───
        composable(
            route = Screen.AdminOrders.route,
            arguments = listOf(androidx.navigation.navArgument("initialTab") {
                type = androidx.navigation.NavType.StringType
                defaultValue = "PENDING"
            })
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab") ?: "PENDING"
            val vm: AdminOrderViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminOrderViewModel(AdminRepository(NetworkClient.api), initialTab) as T
                }
            })
            AdminScreenTheme { OrderManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // ─── Admin Promotions ───
        composable(route = Screen.AdminPromotions.route) {
            val vm: AdminPromotionViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminPromotionViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme { PromotionManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // ─── Admin Reviews ───
        composable(route = Screen.AdminReviews.route) {
            val vm: AdminReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminReviewViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme { ReviewManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                openHomeScreen = { navController.navigate(Screen.UserHome.route) },
                openRegisterScreen = { navController.navigate(Screen.Register.route) },
                openResetPasswordScreen = { navController.navigate(Screen.ResetPassword.route) }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(openLoginScreen = { navController.navigate(Screen.Login.route) })
        }

        composable(
            route = Screen.AdminProduct.route,
            arguments = listOf(androidx.navigation.navArgument("productId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
            val viewModel: AdminProductViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = com.example.coffeeshopapp.data.repository.ProductRepository(NetworkClient.api)
                    val catRepo = CategoryRepository(NetworkClient.api)
                    return AdminProductViewModel(
                        getProductsUseCase = com.example.coffeeshopapp.domain.usecase.GetProductsUseCase(repository),
                        getProductByIdUseCase = com.example.coffeeshopapp.domain.usecase.GetProductByIdUseCase(repository),
                        createProductUseCase = com.example.coffeeshopapp.domain.usecase.CreateProductUseCase(repository),
                        updateProductUseCase = com.example.coffeeshopapp.domain.usecase.UpdateProductUseCase(repository),
                        deleteProductUseCase = com.example.coffeeshopapp.domain.usecase.DeleteProductUseCase(repository),
                        getCategoriesUseCase = GetCategoriesUseCase(catRepo)
                    ) as T
                }
            })
            if (productId != -1L) {
                androidx.compose.runtime.LaunchedEffect(productId) {
                    viewModel.showDetail(productId)
                }
            }
            AdminScreenTheme {
                com.example.coffeeshopapp.presentation.screen.admin.product.AdminProductScreen(
                    viewModel = viewModel,
                    isDeepLink = productId != -1L,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(route = Screen.ResetPassword.route) { ForgotPasswordScreen() }
    }
}
