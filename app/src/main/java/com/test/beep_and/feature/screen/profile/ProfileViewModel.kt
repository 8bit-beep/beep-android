package com.test.beep_and.feature.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.screen.profile.model.ProfilePendingUiState
import com.test.beep_and.feature.screen.profile.model.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    fun getMyInfo() {
        _state.update {
            it.copy(profileUiState = ProfilePendingUiState.Loading)
        }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.profileService.profile()
                response.data?.let { profileData ->
                    _state.update {
                        it.copy(
                            profileUiState = ProfilePendingUiState.Success(
                                myData = profileData
                            )
                        )
                    }
                } ?: run {
                    _state.update {
                        it.copy(
                            profileUiState = ProfilePendingUiState.Error
                        )
                    }
                }
            } catch (e: Exception) {
                NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                _state.update {
                    it.copy(profileUiState = ProfilePendingUiState.Error)
                }
            }
        }
    }
}