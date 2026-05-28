package com.example.coffeeshopapp.presentation.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.coffeeshopapp.data.local.AuthDataStore
import com.example.coffeeshopapp.data.model.entity.Product
import com.example.coffeeshopapp.data.remote.GoogleAuthRequestDto
import com.example.coffeeshopapp.data.remote.NetworkClient
import com.example.coffeeshopapp.data.TokenProvider
import com.example.coffeeshopapp.data.repository.AdminRepository
import com.example.coffeeshopapp.data.repository.CategoryRepository
import com.example.coffeeshopapp.data.repository.ToppingRepository
import com.example.coffeeshopapp.domain.usecase.CreateCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.CreateToppingUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.DeleteToppingUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoriesUseCase
import com.example.coffeeshopapp.domain.usecase.GetCategoryByIdUseCase
import com.example.coffeeshopapp.domain.usecase.GetProductsByCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.GetToppingByIdUseCase
import com.example.coffeeshopapp.domain.usecase.GetToppingsUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateCategoryUseCase
import com.example.coffeeshopapp.domain.usecase.UpdateToppingUseCase
import com.example.coffeeshopapp.presentation.screen.admin.DashboardScreen
import com.example.coffeeshopapp.presentation.screen.admin.OrderManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.PromotionManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.ReviewManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.ToppingManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.UserManagementScreen
import com.example.coffeeshopapp.presentation.screen.admin.category.AdminCategoryScreen
import com.example.coffeeshopapp.presentation.screen.admin.product.AdminProductScreen
import com.example.coffeeshopapp.presentation.screen.auth.ForgotPasswordScreen
import com.example.coffeeshopapp.presentation.screen.auth.LoginScreen
import com.example.coffeeshopapp.presentation.screen.auth.OtpVerificationScreen
import com.example.coffeeshopapp.presentation.screen.auth.RegisterScreen
import com.example.coffeeshopapp.presentation.screen.user.ChangePasswordScreen
import com.example.coffeeshopapp.presentation.screen.user.cart.CartScreen
import com.example.coffeeshopapp.presentation.screen.user.ProductDetailScreen
import com.example.coffeeshopapp.presentation.screen.user.checkout.CheckoutScreen
import com.example.coffeeshopapp.presentation.screen.user.order.OrderHistoryScreen
import com.example.coffeeshopapp.presentation.screen.user.order.OrderDetailScreen
import com.example.coffeeshopapp.presentation.screen.user.order.ReviewScreen
import com.example.coffeeshopapp.presentation.screen.user.order.OrderScreen
import com.example.coffeeshopapp.presentation.screen.user.ProfileScreen
import com.example.coffeeshopapp.presentation.screen.user.favorite.FavouritesScreen
import com.example.coffeeshopapp.presentation.screen.user.home.HomeScreen
import com.example.coffeeshopapp.presentation.theme.AdminScreenTheme
import com.example.coffeeshopapp.presentation.viewmodel.*
import com.example.coffeeshopapp.utils.getErrorMessage
import kotlinx.coroutines.launch

private data class AuthSnapshot(
    val token: String?,
    val refreshToken: String?
)

@SuppressLint("RestrictedApi")
@Composable
fun NavGraph(innerPadding: PaddingValues, navController: NavHostController) {
    val context = LocalContext.current
    val authSnapshot by produceState<AuthSnapshot?>(initialValue = null, context) {
        val token = AuthDataStore.readTokenBlocking(context)
        val refreshToken = AuthDataStore.readRefreshTokenBlocking(context)
        TokenProvider.token = token
        TokenProvider.refreshToken = refreshToken
        value = AuthSnapshot(token, refreshToken)
    }

    if (authSnapshot == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startRoute = if (authSnapshot?.token.isNullOrBlank()) Screen.Login.route else Screen.UserHome.route
    val sharedHomeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = Modifier.fillMaxSize().padding(innerPadding),

        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        }
    ) {
        // home
        composable(route = Screen.UserHome.route) {
            HomeScreen(viewModel = sharedHomeViewModel)
        }

        // favorite
        composable(route = Screen.Favourites.route) {
            FavouritesScreen(viewModel = sharedHomeViewModel)
        }

        // cart
        composable(route = Screen.Cart.route) { CartScreen(navController = navController) }

        // order
        composable(route = Screen.Order.route) {
            OrderScreen(
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) },
                onAddMore = { navController.popBackStack() }
            )
        }

        // checkout
        composable(route = Screen.Checkout.route) {
            val context = LocalContext.current
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Screen.UserHome.route) {
                        popUpTo(Screen.UserHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenVnPay = { url ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    navController.navigate(Screen.UserHome.route) {
                        popUpTo(Screen.UserHome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // profile
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onOpenAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                onOpenChangePassword = { navController.navigate(Screen.ChangePassword.route) },
                onOpenOrderHistory = { navController.navigate(Screen.OrderHistory.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // order history
        composable(route = Screen.OrderHistory.route) {
            OrderHistoryScreen(
                onBack = { navController.popBackStack() },
                onReviewClick = { productId, productName, size, imageUrl ->
                    navController.navigate(Screen.Review.createRoute(productId, productName, size, imageUrl))
                },
                onOrderClick = { orderId: Long ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                }
            )
        }

        // order details
        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(androidx.navigation.navArgument("orderId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderDetailScreen(orderId = orderId, onBack = { navController.popBackStack() })
        }

        // review
        composable(
            route = Screen.Review.route,
            arguments = listOf(
                androidx.navigation.navArgument("productId") { type = androidx.navigation.NavType.LongType },
                androidx.navigation.navArgument("productName") { type = androidx.navigation.NavType.StringType; nullable = true },
                androidx.navigation.navArgument("size") { type = androidx.navigation.NavType.StringType; nullable = true },
                androidx.navigation.navArgument("imageUrl") { type = androidx.navigation.NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            val size = backStackEntry.arguments?.getString("size")
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")
            ReviewScreen(
                productId = productId,
                productName = productName,
                size = size,
                imageUrl = imageUrl,
                onBack = { navController.popBackStack() }
            )
        }

        // change password
        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    val returnedToProfile = navController.popBackStack(Screen.Profile.route, false)
                    if (!returnedToProfile) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.ChangePassword.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // admin dashboard
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
                    onOpenToppings = { navController.navigate(Screen.AdminToppings.route) },
                    onOpenReviews = { navController.navigate(Screen.AdminReviews.route) }
                )
            }
        }

        // admin category
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

        // admin users
        composable(route = Screen.AdminUsers.route) {
            val vm: AdminUserViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminUserViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme { UserManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // admin orders
        composable(
            route = Screen.AdminOrders.route,
            arguments = listOf(androidx.navigation.navArgument("initialTab") {
                type = androidx.navigation.NavType.StringType
                defaultValue = "ALL"
            })
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString("initialTab") ?: "ALL"
            val vm: AdminOrderViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminOrderViewModel(AdminRepository(NetworkClient.api), initialTab) as T
                }
            })
            AdminScreenTheme { OrderManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // admin promotions
        composable(route = Screen.AdminPromotions.route) {
            val vm: AdminPromotionViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminPromotionViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme { PromotionManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // admin topping
        composable(route = Screen.AdminToppings.route) {
            val vm: AdminToppingViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repository = ToppingRepository(NetworkClient.api)
                    return AdminToppingViewModel(
                        getToppingsUseCase = GetToppingsUseCase(repository),
                        getToppingByIdUseCase = GetToppingByIdUseCase(repository),
                        createToppingUseCase = CreateToppingUseCase(repository),
                        updateToppingUseCase = UpdateToppingUseCase(repository),
                        deleteToppingUseCase = DeleteToppingUseCase(repository)
                    ) as T
                }
            })
            AdminScreenTheme { ToppingManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // admin reviewers
        composable(route = Screen.AdminReviews.route) {
            val vm: AdminReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AdminReviewViewModel(AdminRepository(NetworkClient.api)) as T
                }
            })
            AdminScreenTheme { ReviewManagementScreen(viewModel = vm, onBackClick = { navController.popBackStack() }) }
        }

        // login
        composable(route = Screen.Login.route) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            LoginScreen(
                openHomeScreen = { navController.navigate(Screen.UserHome.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }},
                openAdminScreen = { navController.navigate(Screen.AdminDashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }},
                openRegisterScreen = { navController.navigate(Screen.Register.route) },
                openResetPasswordScreen = { navController.navigate(Screen.ForgotPassword.route) },
                onGoogleLogin = {
                    coroutineScope.launch {
                        try {
                            val idToken = com.example.coffeeshopapp.data.auth.GoogleSignInHelper.signIn(context)
                            if (idToken != null) {
                                val resp = NetworkClient.api.googleLogin(GoogleAuthRequestDto(idToken))
                                if (resp.result != null) {
                                    val token = resp.result.accessToken
                                    val refreshToken = resp.result.refreshToken
                                    AuthDataStore.setToken(context, token, refreshToken)
                                    TokenProvider.token = token
                                    TokenProvider.refreshToken = refreshToken
                                    // Fetch user info
                                    var isAdmin = false
                                    try {
                                        val meResp = NetworkClient.api.getMyInfo()
                                        val roles = meResp.result?.roles?.mapNotNull { it.name ?: it.code } ?: emptyList()
                                        isAdmin = roles.any { it.equals("ADMIN", ignoreCase = true) }
                                        AuthDataStore.setRoles(context, roles)
                                        AuthDataStore.setProvider(context, meResp.result?.provider ?: "GOOGLE")
                                        AuthDataStore.setUserId(context, meResp.result?.id?.toString())
                                    } catch (_: Exception) {
                                        AuthDataStore.setProvider(context, "GOOGLE")
                                        AuthDataStore.setUserId(context, null)
                                    }
                                    Toast.makeText(context, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(if (isAdmin) Screen.AdminDashboard.route else Screen.UserHome.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Đăng nhập thất bại: ${resp.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Google Sign-In bị hủy hoặc lỗi", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi: ${e.getErrorMessage()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        // register
        composable(route = Screen.Register.route) {
            RegisterScreen(
                openLoginScreen = { navController.navigate(Screen.Login.route) },
                openOtpScreen = { email ->
                    navController.navigate(Screen.OtpVerification.createRoute(email))
                }
            )
        }

        // otp verification
        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(androidx.navigation.navArgument("email") {
                type = androidx.navigation.NavType.StringType
            })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerificationScreen(
                email = email,
                onVerified = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // forgot password
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }

        // Keep old route for backward compat
        composable(route = Screen.ResetPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ResetPassword.route) { inclusive = true }
                    }
                }
            )
        }

        // admin product
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
                AdminProductScreen(
                    viewModel = viewModel,
                    isDeepLink = productId != -1L,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // product detail
        composable(route = Screen.ProductDetail.route) {
            ProductDetailScreen(
                product = Product("1", "Sinh tố bơ", 25000, imageUrl = "", description = "Bơ sáp Daklak xay nhuyễn"),
                onDismiss = { sharedHomeViewModel.onDismiss() }
            )
        }
    }
}
