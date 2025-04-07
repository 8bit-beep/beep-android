package com.test.beep_and.feature.screen.auth.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.data.user.saveUser.saveAccToken
import com.test.beep_and.feature.data.user.saveUser.saveRefToken
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.DodamRetrofitClient
import com.test.beep_and.feature.network.login.DAuthLoginRequest
import com.test.beep_and.feature.network.login.LoginRequest
import com.test.beep_and.feature.network.core.remote.NetworkUtil
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

data class TextState(
    val email: String = "",
    val password: String = "",
    val access: String = "",
    val refresh: String = "",
    val error: String = "",
    val showDialog: Boolean = false,
    val loadingState: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface LoginSideEffect {
    data object Success : LoginSideEffect
    data object Failed : LoginSideEffect
}

class LoginViewModel : ViewModel() {
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = "")
    }

    fun updateDialog(show: Boolean) {
        _uiState.update { it.copy(showDialog = show) }
    }

    private fun updateLoadingState(show: Boolean) {
        _uiState.update { it.copy(loadingState = show) }
    }

    private fun extractCodeFromLocation(location: String): String? {
        val codePattern = "code=(.+?)&"
        val regex = Regex(codePattern)
        val matchResult = regex.find(location)

        return matchResult?.groupValues?.getOrNull(1)
    }

    fun login(id: String, password: String, networkUtil: NetworkUtil) {
        if (id.length <= 255 && password.length <= 255) {
            if (!networkUtil.isNetworkConnected()) {
                updateLoadingState(true)
            } else {
                _uiState.update { it.copy(loadingState = false) }
                viewModelScope.launch {
                    try {
                        val json = """
                        {
                          "id": "$id",
                          "pw": "$password",
                          "clientId": "575fe863c46f4126a9c17e4af4b82d5d351bdff5507d454086a88edd19afa723",
                          "redirectUrl": "https://beep.cher1shrxd.me/callback/dauth"
                        }
                    """.trimIndent()

                        val requestBody = json.toRequestBody("application/json".toMediaType())

                        val response = DodamRetrofitClient.dAuthService.dAuthLogin(requestBody)

                        val loginData = response.data?.let { extractCodeFromLocation(it.location)?.let { code ->
                            LoginRequest(code)
                        } }

                        val tokenResponse = loginData?.let { RetrofitClient.loginService.login(it) }

                        if (tokenResponse != null) {
                            updateToken(tokenResponse.accessToken, tokenResponse.refreshToken)
                        }

                        _uiEffect.emit(LoginSideEffect.Success)
                        updateDialog(false)
                    } catch (e: HttpException) {
                        _uiEffect.emit(LoginSideEffect.Failed)
                        NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                        updateDialog(true)
                        when (e.code()) {
                            401 -> updateError("아이디 또는 비밀번호가 일치하지 않습니다.")
                            400 -> updateError("유효하지 않은 정보 입니다.")
                            406 -> updateError("현재 서버가 동작하지 않습니다. 잠시후 다시 시도해 주세요.")
                        }
                    }
                }
            }
        }
    }
    fun saveTokens(context: Context) {
        saveAccToken(context, uiState.value.access)
        saveRefToken(context, uiState.value.refresh)
    }
}
