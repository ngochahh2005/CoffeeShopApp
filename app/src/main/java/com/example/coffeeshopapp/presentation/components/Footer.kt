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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
                navController.navigate(Screen.UserHome.route)
            }

            FooterIcon(
                iconSelected = Icons.Filled.Favorite,
                iconUnselected = Icons.Outlined.FavoriteBorder,
                isSelected = currentRoute == Screen.Favourites.route
            ) {
                navController.navigate(Screen.Favourites.route)
            }

            FooterIcon(
                iconSelected = Icons.Default.ShoppingBag,
                iconUnselected = Icons.Outlined.ShoppingBag,
                isSelected = currentRoute == Screen.Cart.route
            ) {
                navController.navigate(Screen.Cart.route)
            }

            FooterIcon(
                iconSelected = Icons.Default.Person,
                iconUnselected = Icons.Outlined.Person,
                isSelected = currentRoute == Screen.Profile.route
            ) {
                navController.navigate(Screen.Profile.route)
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
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = null,
            tint = LabelColor,
            modifier = Modifier.fillMaxSize(.75f)
        )
    }
}