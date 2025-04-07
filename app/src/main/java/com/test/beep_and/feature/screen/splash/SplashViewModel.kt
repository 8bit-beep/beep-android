package com.test.beep_and.feature.screen.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.data.user.getUser.getAccToken
import com.test.beep_and.feature.data.user.getUser.getRefToken
import com.test.beep_and.feature.data.user.saveUser.saveAccToken
import com.test.beep_and.feature.data.user.saveUser.saveRefToken
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.token.AccTokenRequest
import com.test.beep_and.feature.screen.auth.login.LoginSideEffect
import com.test.beep_and.feature.screen.auth.login.TextState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import retrofit2.HttpException


class SplashViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TextState())
    val uiState = _uiState.asStateFlow()


    private val _uiEffect = MutableSharedFlow<LoginSideEffect>()
    val uiEffect: SharedFlow<LoginSideEffect> = _uiEffect.asSharedFlow()


    private fun updateToken(access: String, refresh: String) {
        _uiState.update { it.copy(access = access, refresh = refresh) }
    }

    fun updateError(error: String) {
        _uiState.update { it.copy(error = error) }
    }

    fun updateDialog(show: Boolean) {
        _uiState.update { it.copy(showDialog = show) }
    }

    fun refreshToken(
        context: Context,
        navigateToLogin: () -> Unit,
        navigateToHome: () -> Unit
    ) {
        Log.d("이거", "refreshToken: ${getRefToken(context)}")
        if (getAccToken(context) == null || getAccToken(context)?.isEmpty() == true) {
            navigateToLogin()
        } else {
            _uiState.update { it.copy(loadingState = false) }
            viewModelScope.launch {
                try {
                    kotlinx.coroutines.delay(5000)
                    Log.d("왜됨1", "refreshToken: ${getRefToken(context)}")
                    Log.d("왜됨1", "refreshToken: ${getAccToken(context)}")
                    val response = getRefToken(context)?.let {
                        AccTokenRequest(
                            it
                        )
                    }?.let { RetrofitClient.tokenService.token(it) }

                    if (response != null) {
                        Log.d("왜됨", "refreshToken: ${response.refreshToken}")
                        updateToken(response.accessToken, response.refreshToken)
                    }

                    _uiEffect.emit(LoginSideEffect.Success)
                    navigateToHome()
                } catch (e: HttpException) {
                    _uiEffect.emit(LoginSideEffect.Failed)
                    NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                    updateDialog(true)
                    when (e.code()) {
                        401 -> updateError("401")
                        400 -> updateError("400")
                        406 -> updateError("406")
                    }
                }
            }
        }
    }
}