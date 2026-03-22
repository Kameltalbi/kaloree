package com.kaloree.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object AddMeal : Screen("add_meal")
    object AddActivity : Screen("add_activity")
    object History : Screen("history")
    object Weight : Screen("weight")
    object Profile : Screen("profile")
}
