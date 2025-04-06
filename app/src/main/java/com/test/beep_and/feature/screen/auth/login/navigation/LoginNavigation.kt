package com.test.beep_and.feature.screen.auth.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.auth.login.LoginScreen

const val LOGIN_ROUTE = "login"

fun NavController.navigateToLogin() = this.navigate(LOGIN_ROUTE)

fun NavGraphBuilder.loginScreen() {
    composable(
        route = LOGIN_ROUTE,
    ) {
        LoginScreen()
    }
}