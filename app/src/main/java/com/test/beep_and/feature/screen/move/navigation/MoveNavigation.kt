package com.test.beep_and.feature.screen.move.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.move.MoveScreen

const val MOVE_ROUTE = "move"

fun NavGraphBuilder.moveScreen() {
    composable(
        route = MOVE_ROUTE,
    ) {
        MoveScreen()
    }
}