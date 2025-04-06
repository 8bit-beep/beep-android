package com.test.beep_and

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.test.beep_and.feature.screen.home.navigation.HOME_ROUTE
import com.test.beep_and.feature.screen.home.navigation.homeScreen
import com.test.beep_and.feature.screen.main.BottomNavigationBar
import com.test.beep_and.feature.screen.move.navigation.moveScreen
import com.test.beep_and.feature.screen.profile.navigation.profileScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun App(
    navHostController: NavHostController = rememberNavController(),
) {
    val currentRoute = remember { mutableStateOf(HOME_ROUTE) }

    LaunchedEffect(navHostController) {
        navHostController.currentBackStackEntryFlow
            .map { it.destination.route ?: HOME_ROUTE }
            .distinctUntilChanged()
            .collect {
                currentRoute.value = it
            }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navHostController)

        }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = HOME_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            homeScreen()
            moveScreen()
            profileScreen()
        }
    }
}
