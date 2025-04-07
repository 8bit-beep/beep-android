package com.test.beep_and.feature.screen.splash.model



data class SplashUiState(
    val splashUiState: SplashPendingUiState = SplashPendingUiState.Loading,
)

sealed interface SplashPendingUiState {
    data object Success: SplashPendingUiState
    data object Loading : SplashPendingUiState
    data class Error(
        val error: String?
    ) : SplashPendingUiState
}
