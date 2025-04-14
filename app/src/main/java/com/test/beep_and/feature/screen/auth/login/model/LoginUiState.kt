package com.test.beep_and.feature.screen.auth.login.model

import android.util.Log


data class LoginUiState(
    val loginUiState: LoginPendingUiState = LoginPendingUiState.Default,
)

sealed interface LoginPendingUiState {
    data object Success: LoginPendingUiState
    data object Loading: LoginPendingUiState
    data class Error(
        val error: String?
    ): LoginPendingUiState
    data object Default: LoginPendingUiState
}
