package com.test.beep_and.feature.screen.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.home.HomeScreen
import com.test.beep_and.feature.screen.splash.navigation.SPLASH_ROUTE

const val HOME_ROUTE = "home"

fun NavController.navigateToHome() {
    this.navigate(HOME_ROUTE) {
        popUpTo(SPLASH_ROUTE) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.homeScreen() {
    composable(
        route = HOME_ROUTE,
    ) {
        HomeScreen()
    }
}