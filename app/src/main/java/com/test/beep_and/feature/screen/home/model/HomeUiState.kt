package com.test.beep_and.feature.screen.home.model


data class RoomUiState(
    val roomUiState: RoomPendingUiState = RoomPendingUiState.Default,
)

data class HomeUiState(
    val homeUiState: HomePendingUiState = HomePendingUiState.Default
)

data class CancelUiState(
    val cancelUiState: CancelPendingUiState = CancelPendingUiState.Default
)

data class MaiActivityUiState(
    val mainActivityUiState: MainActivityPendingUiState = MainActivityPendingUiState.Default
)

sealed class HomePendingUiState {
    data object Loading : HomePendingUiState()
    data class Success(val room: String) : HomePendingUiState()
    data class Error(val message: String) : HomePendingUiState()
    data object Default: HomePendingUiState()
}

sealed class RoomPendingUiState {
    data object Loading: RoomPendingUiState()
    data object Success: RoomPendingUiState()
    data object Error: RoomPendingUiState()
    data object Default: RoomPendingUiState()
}

sealed class CancelPendingUiState {
    data object Loading: CancelPendingUiState()
    data object Success: CancelPendingUiState()
    data object Error: CancelPendingUiState()
    data object Default: CancelPendingUiState()
}

sealed class MainActivityPendingUiState {
    data object Loading: MainActivityPendingUiState()
    data object Success: MainActivityPendingUiState()
    data class Error(val message: String) : MainActivityPendingUiState()
    data object Default: MainActivityPendingUiState()
}