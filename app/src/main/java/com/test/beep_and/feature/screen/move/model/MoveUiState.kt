package com.test.beep_and.feature.screen.move.model

import com.test.beep_and.feature.network.move.MoveResponse

data class MoveUiState(
    val moveUiState: MovePendingUiState = MovePendingUiState.Default,
    val deleteUiState: DeleteMovePendingUiState = DeleteMovePendingUiState.Loading
)

sealed interface MovePendingUiState {
    data class Success(
        val myMove: List<MoveResponse>
    ): MovePendingUiState
    data object Loading: MovePendingUiState
    data object Error: MovePendingUiState
    data object Default: MovePendingUiState
}

sealed interface DeleteMovePendingUiState {
    data object Success: DeleteMovePendingUiState
    data object Loading: DeleteMovePendingUiState
    data class Error(
        val error: String?
    ): DeleteMovePendingUiState
}