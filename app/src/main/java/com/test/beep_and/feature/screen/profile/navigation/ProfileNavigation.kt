package com.test.beep_and.feature.screen.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.profile.ProfileScreen

const val PROFILE_ROUTE = "profile"

fun NavGraphBuilder.profileScreen(
    navigateToLogin: () -> Unit
) {
    composable(
        route = PROFILE_ROUTE,
    ) {
        ProfileScreen(
            navigateToLogin = navigateToLogin
        )
    }
}