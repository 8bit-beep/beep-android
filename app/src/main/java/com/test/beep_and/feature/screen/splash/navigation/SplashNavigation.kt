package com.test.beep_and.feature.screen.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.splash.SplashScreen

const val SPLASH_ROUTE = "splash"

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