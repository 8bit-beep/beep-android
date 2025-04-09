package com.test.beep_and.feature.screen.auth.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.beep_and.feature.data.user.saveUser.saveAccToken
import com.test.beep_and.feature.data.user.saveUser.saveRefToken
import com.test.beep_and.feature.network.core.remote.BeepRetrofitClient
import com.test.beep_and.feature.network.core.remote.NetworkUtil
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
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
import retrofit2.HttpException


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
                        loginUiState = LoginPendingUiState.Loading
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
                    } catch (e: NoConnectivityException) {
                        Toast.makeText(context, "인터넷 연결을 확인해 주세요", Toast.LENGTH_LONG).show()
                        _state.update {
                            it.copy(
                                loginUiState = LoginPendingUiState.NetworkError(
                                    "인터넷 연결이 없습니다. 네트워크 상태를 확인해주세요."
                                )
                            )
                        }
                    } catch (e: HttpException) {
                        _state.update {
                            it.copy(
                                loginUiState = LoginPendingUiState.Error(
                                    when (e.code()) {
                                        401 ->"아이디 또는 비밀번호가 일치하지 않습니다."
                                        400 -> "유효하지 않은 정보 입니다."
                                        406 -> "현재 서버가 동작하지 않습니다. 잠시후 다시 시도해 주세요."
                                        else -> "오류가 발생했습니다 ${e.message}"
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
