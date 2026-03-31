package com.example.coffeeshopapp.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.coffeeshopapp.presentation.screen.auth.ForgotPasswordScreen
import com.example.coffeeshopapp.presentation.screen.auth.LoginScreen
import com.example.coffeeshopapp.presentation.screen.auth.RegisterScreen
import com.example.coffeeshopapp.presentation.screen.user.CartScreen
import com.example.coffeeshopapp.presentation.screen.user.FavouritesScreen
import com.example.coffeeshopapp.presentation.screen.user.ProfileScreen
import com.example.coffeeshopapp.presentation.screen.user.home.HomeScreen

@Composable
fun NavGraph(innerPadding: PaddingValues, navController: NavHostController) {
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
        composable(route = Screen.UserHome.route) {
            HomeScreen(
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
            FavouritesScreen()
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