package com.kaloree.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaloree.ui.screens.*
import com.kaloree.ui.theme.PrimaryGreen

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Accueil", Icons.Default.Home, Screen.Dashboard.route),
    BottomNavItem("Historique", Icons.Default.DateRange, Screen.History.route),
    BottomNavItem("Poids", Icons.Default.Star, Screen.Weight.route),
    BottomNavItem("Profil", Icons.Default.Person, Screen.Profile.route)
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(item.icon, contentDescription = item.label)
                            },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryGreen,
                                selectedTextColor = PrimaryGreen,
                                indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddMeal = { navController.navigate(Screen.AddMeal.route) },
                    onAddActivity = { navController.navigate(Screen.AddActivity.route) }
                )
            }

            composable(Screen.AddMeal.route) {
                AddMealScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onMealAdded = { navController.popBackStack() }
                )
            }

            composable(Screen.AddActivity.route) {
                AddActivityScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onActivityAdded = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen()
            }

            composable(Screen.Weight.route) {
                WeightScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
