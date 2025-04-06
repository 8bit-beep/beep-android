package com.test.beep_and.feature.screen.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.test.beep_and.feature.screen.home.navigation.HOME_ROUTE
import com.test.beep_and.feature.screen.home.navigation.homeScreen
import com.test.beep_and.feature.screen.main.naviagtion.MainDestination
import com.test.beep_and.feature.screen.move.navigation.moveScreen
import com.test.beep_and.feature.screen.profile.navigation.profileScreen
import com.test.beep_and.res.AppColors

@Composable
fun MainScreen(navHostController: NavHostController) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(mainNavController) }
    ) { paddingValues ->
        NavHost(
            navController = mainNavController,
            startDestination = HOME_ROUTE,
            modifier = Modifier.padding(paddingValues)
        ) {
            homeScreen()
            profileScreen()
            moveScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: HOME_ROUTE

    val items = listOf(
        MainDestination.MOVE,
        MainDestination.HOME,
        MainDestination.PROFILE
    )

    Row(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .height(105.dp)
            .padding(horizontal = 70.dp)
            .padding(top = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEach { destination ->
            BottomCard(
                icon = destination.getIcon(),
                label = destination.label,
                isSelected = destination.route == currentRoute,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomCard(
    isSelected: Boolean,
    icon: Int,
    label: String = "",
    onClick: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedColor by animateColorAsState(
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 100,
        ),
        targetValue = if (isSelected) AppColors.main else AppColors.dark
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = tween(durationMillis = 250),
        label = "scale"
    )

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onClick()
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            colorFilter = ColorFilter.tint(animatedColor)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = label,
            color = animatedColor
        )
    }
}