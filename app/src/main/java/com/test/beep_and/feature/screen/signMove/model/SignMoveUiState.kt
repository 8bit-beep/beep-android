package com.test.beep_and.feature.screen.signMove.model

data class SignMoveUiState(
    val signMoveUiState: SignMovePendingUiState = SignMovePendingUiState.Loading
)

data class ErrorResponse(
    val code: String?,
    val status: Int?,
    val message: String?
)

sealed class SignMovePendingUiState {
    data object Loading : SignMovePendingUiState()
    data object Success : SignMovePendingUiState()
    data class Error(
        val error: ErrorResponse
    ) : SignMovePendingUiState()
}