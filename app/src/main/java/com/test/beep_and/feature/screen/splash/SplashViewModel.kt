package com.test.beep_and.feature.screen.splash

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.data.user.clearAccToken
import com.test.beep_and.feature.data.user.getUser.getAccToken
import com.test.beep_and.feature.data.user.getUser.getRefToken
import com.test.beep_and.feature.data.user.saveUser.saveAccToken
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.token.AccTokenRequest
import com.test.beep_and.feature.screen.splash.model.SplashPendingUiState
import com.test.beep_and.feature.screen.splash.model.SplashUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SplashViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())

    fun refreshToken(
        context: Context,
        navigateToLogin: () -> Unit,
        navigateToHome: () -> Unit
    ) {
        if (getAccToken(context) == null || getAccToken(context)?.isEmpty() == true) {
            navigateToLogin()
        } else {
            _uiState.update { it.copy(splashUiState = SplashPendingUiState.Loading) }

            viewModelScope.launch {
                try {
                    val response = getRefToken(context)?.let {
                        AccTokenRequest(it)
                    }?.let { RetrofitClient.tokenService.token(it) }

                    if (response != null) {
                        _uiState.update {
                            it.copy(splashUiState = SplashPendingUiState.Success)
                        }
                        clearAccToken(context)
                        saveAccToken(context, response.data?.accessToken)
                        navigateToHome()
                    }
                } catch (e: NoConnectivityException) {
                    Toast.makeText(context, "인터넷 연결을 확인해 주세요", Toast.LENGTH_LONG).show()
                    navigateToLogin()
                    _uiState.update {
                        it.copy(
                            splashUiState = SplashPendingUiState.NetworkError(
                                "인터넷 연결이 없습니다. 네트워크 상태를 확인해주세요."
                            )
                        )
                    }
                    Log.e("Network", "No connectivity: ${e.message}")
                } catch (e: HttpException) {
                    _uiState.update {
                        it.copy(
                            splashUiState = SplashPendingUiState.Error(
                                e.message
                            )
                        )
                    }
                    val error = NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                    Log.d("Network", "HTTP error: $error")
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            splashUiState = SplashPendingUiState.Error(
                                e.message ?: "알 수 없는 오류가 발생했습니다."
                            )
                        )
                    }
                    Log.e("Network", "Error: ${e.message}", e)
                }
            }
        }
    }
}