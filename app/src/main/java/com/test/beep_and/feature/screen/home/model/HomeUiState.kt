package com.test.beep_and.feature.screen.home.model


sealed class HomePendingUiState {
    data object Loading : HomePendingUiState()
    data class Success(val room: String) : HomePendingUiState()
    data class Error(val message: String) : HomePendingUiState()
    data object Default: HomePendingUiState()
}

sealed class RoomPendingUiState {
    data object Loading: RoomPendingUiState()
    data object Success: RoomPendingUiState()
    data class Error(val message: String) : RoomPendingUiState()
}