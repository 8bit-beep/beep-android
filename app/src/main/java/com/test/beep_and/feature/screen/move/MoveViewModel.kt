package com.test.beep_and.feature.screen.move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.screen.move.model.DeleteMovePendingUiState
import com.test.beep_and.feature.screen.move.model.DeleteUiState
import com.test.beep_and.feature.screen.move.model.MovePendingUiState
import com.test.beep_and.feature.screen.move.model.MoveUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoveViewModel : ViewModel() {
    private val _state = MutableStateFlow(MoveUiState())
    val state = _state.asStateFlow()

    private val _delState = MutableStateFlow(DeleteUiState())
    val delState = _delState.asStateFlow()


    fun getMyMove() {
        try {
            _state.update {
                it.copy(moveUiState = MovePendingUiState.Loading)
            }
            viewModelScope.launch {
                val response = RetrofitClient.moveService.getMyMove()
                _state.update {
                    it.copy(
                        moveUiState = MovePendingUiState.Success(
                            response
                        )
                    )
                }
            }

        } catch (e: Exception) {
            _state.update {
                it.copy(
                    moveUiState = MovePendingUiState.Error
                )
            }
            NetworkErrorHandler.handle(BeepApplication.getContext(), e)
        }
    }

    fun deleteMyMove(id: Int) {
        _delState.update {
            it.copy(deleteUiState = DeleteMovePendingUiState.Loading)
        }
        viewModelScope.launch {
            try {
                RetrofitClient.moveService.deleteMyMove(id)
                _delState.update {
                    it.copy(deleteUiState = DeleteMovePendingUiState.Success)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        moveUiState = MovePendingUiState.Error
                    )
                }
                NetworkErrorHandler.handle(BeepApplication.getContext(), e, true)
            }
        }
    }
}
