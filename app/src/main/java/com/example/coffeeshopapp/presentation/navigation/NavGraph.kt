package com.example.coffeeshopapp.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.coffeeshopapp.presentation.screen.auth.ForgotPasswordScreen
import com.example.coffeeshopapp.presentation.screen.auth.LoginScreen
import com.example.coffeeshopapp.presentation.screen.auth.RegisterScreen
import com.example.coffeeshopapp.presentation.screen.user.CartScreen
import com.example.coffeeshopapp.presentation.screen.user.favorite.FavouritesScreen
import com.example.coffeeshopapp.presentation.screen.user.ProfileScreen
import com.example.coffeeshopapp.presentation.screen.user.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coffeeshopapp.presentation.viewmodel.HomeViewModel

@SuppressLint("RestrictedApi")
@Composable
fun NavGraph(innerPadding: PaddingValues, navController: NavHostController) {
    val sharedHomeViewModel: HomeViewModel = viewModel()

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = Modifier.padding(innerPadding),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable(route = Screen.UserHome.route) { backStackEntry ->
            // create ViewModel scoped to the UserHome backStackEntry so loadData() runs when Home is shown
            val homeViewModel: HomeViewModel = viewModel(backStackEntry)
            HomeScreen(
                viewModel = homeViewModel,
                openFavouritesScreen = {
                    navController.navigate(Screen.Favourites.route)
                },
                openCartScreen = {
                    navController.navigate(Screen.Cart.route)
                },
                openProfileScreen = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(route = Screen.Favourites.route) {
            val homeBackStackEntry = remember(it) {
                navController.currentBackStack.value.find { entry ->
                    entry.destination.route == Screen.UserHome.route
                }
            }

            // Nếu tìm thấy entry của UserHome thì dùng, không thì tạo mới (viewModel())
            val homeViewModel: HomeViewModel = if (homeBackStackEntry != null) {
                viewModel(homeBackStackEntry)
            } else {
                viewModel()
            }

            FavouritesScreen(viewModel = homeViewModel)
        }

        composable(route = Screen.Cart.route) {
            CartScreen()
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen()
        }

        composable(route = Screen.Login.route) {
            LoginScreen(
                openHomeScreen = {
                    navController.navigate(Screen.UserHome.route)
                },
                openRegisterScreen = {
                    navController.navigate(Screen.Register.route)
                },
                openResetPasswordScreen = {
                    navController.navigate(Screen.ResetPassword.route)
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                openLoginScreen = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.ResetPassword.route) {
            ForgotPasswordScreen()
        }
    }
}