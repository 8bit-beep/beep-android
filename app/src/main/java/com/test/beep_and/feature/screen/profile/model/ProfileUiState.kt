package com.test.beep_and.feature.screen.profile.model

import com.test.beep_and.feature.network.profile.ProfileResponse

data class ProfileUiState(
    val profileUiState: ProfilePendingUiState = ProfilePendingUiState.Default,
)

sealed interface ProfilePendingUiState {
    data class Success(
        val myData: ProfileResponse
    ): ProfilePendingUiState
    data object Loading: ProfilePendingUiState
    data class Error(
        val error: String?
    ): ProfilePendingUiState
    data object Default: ProfilePendingUiState
}