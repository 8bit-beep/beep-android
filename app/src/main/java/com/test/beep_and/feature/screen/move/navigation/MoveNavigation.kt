package com.test.beep_and.feature.screen.move.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.move.MoveScreen

const val MOVE_ROUTE = "move"

fun NavController.navigateToMove() = this.navigate(MOVE_ROUTE)

fun NavGraphBuilder.moveScreen(
    navigateToSignMove: () -> Unit,
    showDeleteMove: (Int) -> Unit
) {
    composable(
        route = MOVE_ROUTE,
    ) {
        MoveScreen(
            navigateToSignMove = navigateToSignMove,
            showDeleteMove = showDeleteMove
        )
    }
}