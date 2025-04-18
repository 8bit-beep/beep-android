package com.test.beep_and.feature.screen.signMove

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.signMove.SignMoveRequest
import com.test.beep_and.feature.screen.signMove.model.ErrorResponse
import com.test.beep_and.feature.screen.signMove.model.SignMovePendingUiState
import com.test.beep_and.feature.screen.signMove.model.SignMoveUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SignMoveViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignMoveUiState())
    val state = _state.asStateFlow()

    fun signMove(
        reason: String,
        moveTo: String,
        moveTime: Int
    ) {
        viewModelScope.launch {
            try {
                _state.update {
                    it.copy(signMoveUiState = SignMovePendingUiState.Loading)
                }
                RetrofitClient.signMoveService.signMove(
                    SignMoveRequest(
                        reason = reason,
                        room = moveTo,
                        period = moveTime,
                        date = LocalDateTime.now().toString()
                    )
                )
                _state.update {
                    it.copy(signMoveUiState = SignMovePendingUiState.Success)
                }
            } catch (e: Exception) {
                val status = NetworkErrorHandler.getStatus(e)

                _state.update {
                    it.copy(
                        signMoveUiState = SignMovePendingUiState.Error(
                            ErrorResponse(
                                status = status,
                                code = "unknown",
                                message = "errorMessage"
                            )
                        )
                    )
                }
            }
        }
    }
}