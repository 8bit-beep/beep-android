package com.test.beep_and.feature.screen.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.data.user.saveUser.saveAccToken
import com.test.beep_and.feature.data.user.saveUser.saveRefToken
import com.test.beep_and.feature.network.core.NetworkErrorHandler
import com.test.beep_and.feature.network.core.remote.BeepRetrofitClient
import com.test.beep_and.feature.network.core.remote.NetworkUtil
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import com.test.beep_and.feature.network.login.LoginRequest
import com.test.beep_and.feature.screen.auth.login.model.LoginPendingUiState
import com.test.beep_and.feature.screen.auth.login.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()


    private fun extractCodeFromLocation(location: String): String? {
        val codePattern = "code=(.+?)&"
        val regex = Regex(codePattern)
        val matchResult = regex.find(location)

        return matchResult?.groupValues?.getOrNull(1)
    }

    fun login(id: String, password: String, networkUtil: NetworkUtil, context: Context) {
        if (id.length <= 255 && password.length <= 255) {
            if (!networkUtil.isNetworkConnected()) {
                _state.update {
                    it.copy(
                        loginUiState = LoginPendingUiState.Error(
                            "인터넷 연결을 확인해 주세요"
                        )
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        loginUiState = LoginPendingUiState.Loading
                    )
                }
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

                        val response = BeepRetrofitClient.dAuthService.dAuthLogin(requestBody)

                        val loginData = response.data?.let { extractCodeFromLocation(it.location)?.let { code ->
                            LoginRequest(code)
                        } }

                        val tokenResponse = loginData?.let { RetrofitClient.loginService.login(it) }

                        if (tokenResponse != null) {
                            _state.update {
                                it.copy(
                                    loginUiState = LoginPendingUiState.Success
                                )
                            }
                            saveAccToken(context, tokenResponse.accessToken)
                            saveRefToken(context, tokenResponse.refreshToken)
                        }
                    } catch (e: Exception) {
                        val error = NetworkErrorHandler.handle(BeepApplication.getContext(), e)
                        _state.update {
                            it.copy(
                                loginUiState = LoginPendingUiState.Error(
                                    error
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
