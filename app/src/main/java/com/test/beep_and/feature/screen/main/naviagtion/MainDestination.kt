package com.test.beep_and.feature.screen.main.naviagtion

import androidx.compose.runtime.Composable
import com.test.beep_and.feature.screen.home.navigation.HOME_ROUTE
import com.test.beep_and.feature.screen.move.navigation.MOVE_ROUTE
import com.test.beep_and.feature.screen.profile.navigation.PROFILE_ROUTE
import com.test.beep_and.R

enum class MainDestination(
    val iconRes: Int,
    val route: String,
    val label: String
) {
    HOME(R.drawable.hoe, HOME_ROUTE, "홈"),
    MOVE(R.drawable.move, MOVE_ROUTE,"실 이동"),
    PROFILE(R.drawable.person, PROFILE_ROUTE, "프로필");

    @Composable
    fun getIcon(): Int {
        return iconRes
    }
}