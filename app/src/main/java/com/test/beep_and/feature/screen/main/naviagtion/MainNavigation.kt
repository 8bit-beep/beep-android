package com.test.beep_and.feature.screen.main.naviagtion

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.main.MainScreen

const val MAIN_ROUTE = "main"

fun NavController.navigateToMain(
    navOptions: NavOptions? = androidx.navigation.navOptions {
        launchSingleTop = true
    },
) = navigate(MAIN_ROUTE, navOptions)

@ExperimentalFoundationApi
fun NavGraphBuilder.mainScreen(
    navHostController: NavHostController
) {
    composable(route = MAIN_ROUTE) {
        MainScreen(
            navHostController = navHostController
        )
    }
}
