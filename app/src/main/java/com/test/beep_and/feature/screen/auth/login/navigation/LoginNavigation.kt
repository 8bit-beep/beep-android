package com.test.beep_and.feature.screen.auth.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.auth.login.LoginScreen
import com.test.beep_and.feature.screen.splash.navigation.SPLASH_ROUTE

const val LOGIN_ROUTE = "login"

fun NavController.navigateToLogin() {
    this.navigate(LOGIN_ROUTE) {
        popUpTo(SPLASH_ROUTE) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen(
    navigateToHome: () -> Unit
) {
    composable(
        route = LOGIN_ROUTE,
    ) {
        LoginScreen(
            navigateToHome = navigateToHome
        )
    }
}