package com.test.beep_and.feature.screen.signMove.model

data class SignMoveUiState(
    val signMoveUiState: SignMovePendingUiState = SignMovePendingUiState.Loading
)

sealed interface SignMovePendingUiState {
    data object Success: SignMovePendingUiState
    data object Loading: SignMovePendingUiState
    data object Error: SignMovePendingUiState
}