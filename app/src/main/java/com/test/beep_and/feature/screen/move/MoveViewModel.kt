package com.test.beep_and.feature.screen.move

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.screen.move.model.DeleteMovePendingUiState
import com.test.beep_and.feature.screen.move.model.MovePendingUiState
import com.test.beep_and.feature.screen.move.model.MoveUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MoveViewModel: ViewModel() {
    private val _state = MutableStateFlow(MoveUiState())
    val state = _state.asStateFlow()


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

        } catch (e: NoConnectivityException) {
            Log.e("Network", "No connectivity: ${e.message}")
        } catch (e: HttpException) {
            _state.update {
                it.copy(moveUiState = MovePendingUiState.Error)
            }
            val error = NetworkErrorHandler.handle(BeepApplication.getContext(), e)
            Log.d("Network", "HTTP error: $error")
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    moveUiState = MovePendingUiState.Error
                )
            }
            Log.e("Network", "Error: ${e.message}", e)
        }
    }

    fun deleteMyMove(id: Int) {
        try {
            _state.update {
                it.copy(deleteUiState = DeleteMovePendingUiState.Loading)
            }
            viewModelScope.launch {
                RetrofitClient.moveService.deleteMyMove(id)
                _state.update {
                    it.copy(deleteUiState = DeleteMovePendingUiState.Success)
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
}