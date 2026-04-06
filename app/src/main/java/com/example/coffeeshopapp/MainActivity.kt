package com.example.coffeeshopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshopapp.presentation.components.Footer
import com.example.coffeeshopapp.presentation.navigation.NavGraph
import com.example.coffeeshopapp.presentation.navigation.screenWithBottomBar
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    CoffeeShopAppTheme {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Scaffold(
            bottomBar = {
                if (currentRoute in screenWithBottomBar) {
                    Footer(navController)
                }
            }
        ) { innerPadding ->
            NavGraph(innerPadding, navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}