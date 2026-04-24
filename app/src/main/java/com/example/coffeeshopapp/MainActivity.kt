package com.example.coffeeshopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshopapp.presentation.components.Footer
import com.example.coffeeshopapp.presentation.navigation.NavGraph
import com.example.coffeeshopapp.presentation.navigation.Screen
import com.example.coffeeshopapp.presentation.navigation.screenWithBottomBar
import com.example.coffeeshopapp.presentation.theme.CoffeeShopAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffeeShopAppTheme {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val isAdminRoute = currentRoute?.startsWith("admin") == true || currentRoute?.contains("Admin") == true

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        if (isAdminRoute) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = true,
                    drawerContent = {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            ModalDrawerSheet(
                                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.75f),
                                drawerContainerColor = Color(0xFFFFFFFF)
                            ) {
                                Spacer(Modifier.height(32.dp))
                                Text(
                                    "Menu Quản Trị",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF553722)
                                )
                                HorizontalDivider(color = Color(0xFFEEEEEE))

                                val navItem = @Composable { label: String, route: String, icon: ImageVector ->
                                    NavigationDrawerItem(
                                        label = { Text(label, fontWeight = FontWeight.Medium) },
                                        selected = currentRoute == route,
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            if (currentRoute != route) navController.navigate(route)
                                        },
                                        icon = { Icon(icon, null, tint = Color(0xFF553722)) },
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        colors = NavigationDrawerItemDefaults.colors(
                                            unselectedContainerColor = Color.Transparent,
                                            selectedContainerColor = Color(0xFFF7F8FA)
                                        )
                                    )
                                }

                                navItem("Tổng quan", Screen.AdminDashboard.route, Icons.Default.Dashboard)
                                navItem("Danh mục", Screen.AdminCategory.route, Icons.Default.Category)
                                navItem("Topping", Screen.AdminToppings.route, Icons.Default.Cookie)
                                navItem("Sản phẩm", Screen.AdminProduct.createRoute(-1L), Icons.Default.Inventory2)
                                navItem("Người dùng", Screen.AdminUsers.route, Icons.Default.People)
                                navItem("Đơn hàng", Screen.AdminOrders.createRoute("PENDING"), Icons.AutoMirrored.Filled.ReceiptLong)
                            }
                        }
                    }
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        AppContent(navController, currentRoute, isAdminRoute, drawerState, scope)
                    }
                }
            }
        } else {
            AppContent(navController, currentRoute, isAdminRoute, drawerState, scope)
        }
}

@Composable
private fun AppContent(
    navController: NavHostController,
    currentRoute: String?,
    isAdminRoute: Boolean,
    drawerState: androidx.compose.material3.DrawerState,
    scope: CoroutineScope
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in screenWithBottomBar) {
                Footer(navController)
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            NavGraph(innerPadding, navController)

            if (isAdminRoute) {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        null,
                        tint = Color(0xFF553722),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}
