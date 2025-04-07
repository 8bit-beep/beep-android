package com.test.beep_and

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.test.beep_and.feature.screen.auth.login.navigation.loginScreen
import com.test.beep_and.feature.screen.auth.login.navigation.navigateToLogin
import com.test.beep_and.feature.screen.home.navigation.HOME_ROUTE
import com.test.beep_and.feature.screen.home.navigation.homeScreen
import com.test.beep_and.feature.screen.home.navigation.navigateToHome
import com.test.beep_and.feature.screen.main.BottomNavigationBar
import com.test.beep_and.feature.screen.move.navigation.moveScreen
import com.test.beep_and.feature.screen.profile.navigation.profileScreen
import com.test.beep_and.feature.screen.splash.navigation.SPLASH_ROUTE
import com.test.beep_and.feature.screen.splash.navigation.splashScreen
import com.test.beep_and.res.AppColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun App(navHostController: NavHostController = rememberNavController()) {
    val currentRoute = remember { mutableStateOf(HOME_ROUTE) }
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(navHostController) {
        navHostController.currentBackStackEntryFlow
            .map { it.destination.route ?: HOME_ROUTE }
            .distinctUntilChanged()
            .collect {
                currentRoute.value = it
            }
    }

    LaunchedEffect(currentRoute.value) {
        when (currentRoute.value) {
            SPLASH_ROUTE -> systemUiController.setSystemBarsColor(AppColors.dark)
            else -> systemUiController.setSystemBarsColor(Color.White)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute.value in listOf("move", "home", "profile")) {
                BottomNavigationBar(navController = navHostController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = SPLASH_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            homeScreen()
            moveScreen()
            profileScreen()
            loginScreen(
                navigateToHome = navHostController::navigateToHome
            )
            splashScreen(
                navigateToLogin = navHostController::navigateToLogin,
                navigateToHome = navHostController::navigateToHome
            )
        }
    }
}
