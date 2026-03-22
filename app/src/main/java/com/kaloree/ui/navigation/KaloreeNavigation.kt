package com.kaloree.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaloree.ui.screens.*

@Composable
fun KaloreeNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
                onAddActivity = { navController.navigate(Screen.AddActivity.route) },
                onViewHistory = { navController.navigate(Screen.History.route) },
                onViewWeight = { navController.navigate(Screen.Weight.route) },
                onViewProfile = { navController.navigate(Screen.Profile.route) }
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
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Weight.route) {
            WeightScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
