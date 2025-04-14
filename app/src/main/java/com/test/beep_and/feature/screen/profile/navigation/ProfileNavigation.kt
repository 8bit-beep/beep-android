package com.test.beep_and.feature.screen.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.home.HomeViewModel
import com.test.beep_and.feature.screen.move.navigation.MOVE_ROUTE
import com.test.beep_and.feature.screen.profile.ProfileScreen
import com.test.beep_and.feature.screen.profile.ProfileViewModel
import com.test.beep_and.feature.screen.signMove.navigation.SIGN_MOVE_ROUTE

const val PROFILE_ROUTE = "profile"
fun NavController.navigateToProfile() = this.navigate(PROFILE_ROUTE)

fun NavGraphBuilder.profileScreen(
    navigateToLogin: () -> Unit,
    showRoomSheet: (Boolean) -> Unit,
    selectedRoom: String,
    viewModel: ProfileViewModel,
    homeViewModel: HomeViewModel
) {
    composable(
        route = PROFILE_ROUTE,
    ) {
        ProfileScreen(
            navigateToLogin = navigateToLogin,
            showRoomSheet = showRoomSheet,
            selectedRoom = selectedRoom,
            viewModel = viewModel,
            homeViewModel = homeViewModel
        )
    }
}