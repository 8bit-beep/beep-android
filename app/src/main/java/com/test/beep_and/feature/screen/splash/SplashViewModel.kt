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
import com.test.beep_and.feature.network.core.remote.NetworkUtil
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.token.AccTokenRequest
import com.test.beep_and.feature.screen.splash.model.SplashPendingUiState
import com.test.beep_and.feature.screen.splash.model.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException


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
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            splashUiState = SplashPendingUiState.Error
                        )
                    }
                    if (e is IOException || !NetworkUtil(context).isNetworkConnected()) {
                        Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                        navigateToLogin()
                    } else {
                        NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                    }
                }
            }
        }
    }
}