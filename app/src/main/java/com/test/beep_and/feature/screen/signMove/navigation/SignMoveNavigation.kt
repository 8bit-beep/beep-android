package com.test.beep_and.feature.screen.signMove.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.test.beep_and.feature.screen.signMove.SignMoveScreen

const val SIGN_MOVE_ROUTE = "signMove"

fun NavController.navigateToSignMove() = this.navigate(SIGN_MOVE_ROUTE)

fun NavGraphBuilder.signMoveScreen(
    popBackStack: () -> Unit,
    navigateToMove: () -> Unit
) {
    composable(
        route = SIGN_MOVE_ROUTE,
    ) {
        SignMoveScreen(
            popBackStack = popBackStack,
            navigateToMove = navigateToMove
        )
    }
}