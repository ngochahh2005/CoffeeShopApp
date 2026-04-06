package com.example.coffeeshopapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.coffeeshopapp.presentation.utils.CartPositionStore
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.coffeeshopapp.presentation.navigation.Screen
import com.example.coffeeshopapp.presentation.theme.FooterBackgroundColor
import com.example.coffeeshopapp.presentation.theme.LabelColor

@Composable
fun Footer(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Box(modifier = modifier
        .fillMaxWidth()
        .height(48.dp)
        .background(color = FooterBackgroundColor)
    ) {
        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FooterIcon(
                iconSelected = Icons.Default.Home,
                iconUnselected = Icons.Outlined.Home,
                isSelected = currentRoute == Screen.UserHome.route,
            ) {
                navController.navigate(Screen.UserHome.route) {
                    // avoid multiple copies and restore state
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            FooterIcon(
                iconSelected = Icons.Filled.Favorite,
                iconUnselected = Icons.Outlined.FavoriteBorder,
                isSelected = currentRoute == Screen.Favourites.route
            ) {
                navController.navigate(Screen.Favourites.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            // Shopping bag: capture its position for fly-to-cart animation
            IconButton(onClick = {
                navController.navigate(Screen.Cart.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(
                    imageVector = if (currentRoute == Screen.Cart.route) Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    tint = LabelColor,
                    modifier = Modifier
                        .fillMaxSize(.75f)
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInRoot()
                            CartPositionStore.update(Offset(pos.x, pos.y))
                        }
                )
            }

            FooterIcon(
                iconSelected = Icons.Default.Person,
                iconUnselected = Icons.Outlined.Person,
                isSelected = currentRoute == Screen.Profile.route
            ) {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}

@Composable
fun FooterIcon(
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = null,
            tint = LabelColor,
            modifier = Modifier.fillMaxSize(.75f)
        )
    }
}