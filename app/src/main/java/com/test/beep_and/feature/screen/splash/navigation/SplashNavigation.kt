package com.test.beep_and.feature.screen.splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.gson.annotations.Until
import com.test.beep_and.feature.screen.auth.login.LoginScreen
import com.test.beep_and.feature.screen.splash.SplashScreen

const val SPLASH_ROUTE = "splash"

fun NavController.navigateToSplash() = this.navigate(SPLASH_ROUTE)

fun NavGraphBuilder.splashScreen(
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit
) {
    composable(
        route = SPLASH_ROUTE,
    ) {
        SplashScreen(
            navigateToHome = navigateToHome,
            navigateToLogin = navigateToLogin
        )
    }
}