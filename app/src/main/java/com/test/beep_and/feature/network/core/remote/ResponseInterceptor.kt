package com.test.beep_and.feature.network.core.remote

import android.util.Log
import com.test.beep_and.BeepApplication
import com.test.beep_and.feature.data.user.clearToken
import com.test.beep_and.feature.data.user.getUser.getRefToken
import com.test.beep_and.feature.data.user.saveUser.saveAccToken
import com.test.beep_and.feature.data.user.saveUser.saveRefToken
import com.test.beep_and.feature.network.token.AccTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val statusCode = response.code
        val context = BeepApplication.getContext()

        when (statusCode) {
            400 -> {
                Log.e("API 오류", "잘못된 요청입니다.")
            }

            401, 402 -> {

                val refToken = getRefToken(context)

                if (refToken.isNullOrEmpty()) {
                    return response
                }

                val newTokenResponse = runBlocking {
                    try {
                        clearToken(context)

                        val tokenData = AccTokenRequest(refToken)

                        try {
                            val tokenResponse = RetrofitClient.tokenService.token(tokenData)

                            if (tokenResponse.data?.accessToken != null) {
                                saveAccToken(context, tokenResponse.data.accessToken)
                                saveRefToken(context, tokenResponse.data.refreshToken)
                                tokenResponse.data.accessToken
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                if (newTokenResponse != null) {
                    response.close()

                    try {
                        val newRequest = request.newBuilder()
                            .removeHeader("Authorization")
                            .header("Authorization", "Bearer $newTokenResponse")
                            .build()
                        return chain.proceed(newRequest)
                    } catch (e: Exception) {
                        return Response.Builder()
                            .request(request)
                            .protocol(Protocol.HTTP_1_1)
                            .code(500)
                            .message("Internal Error")
                            .body(ResponseBody.create(null, ""))
                            .build()
                    }
                } else {
                    return response
                }
            }

            403 -> {
                Log.e("인터셉터", "권한이 없습니다.")
            }
        }
        return response
    }
}
