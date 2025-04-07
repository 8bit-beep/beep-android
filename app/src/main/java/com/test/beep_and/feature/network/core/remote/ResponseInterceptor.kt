package com.test.beep_and.feature.network.core.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val statusCode = response.code

        when (statusCode) {
            400 -> {
                Log.e("API 오류", "잘못된 요청입니다.")
            }
            401, 402 -> {
//                val newTokenResponse = runBlocking {
//                    try {
//                        clearToken(context)
//                        getRefToken(context)?.let { refToken ->
//                            val tokenData = AccTokenRequest(refToken)
//                            val tokenResponse = RetrofitClient.tokenService.token(tokenData)
//                            saveAccToken(context, tokenResponse.accessToken)
//                            saveRefToken(context, tokenResponse.refreshToken)
//                            tokenResponse.accessToken
//                        }
//                    } catch (e: Exception) {
//                        Log.e("토큰 갱신 오류", e.message ?: "토큰 갱신 실패")
//                        null
//                    }
//                }

//                return newTokenResponse?.let {
//                    chain.proceed(
//                        request.newBuilder()
//                            .header("Authorization", "Bearer $it")
//                            .build()
//                    )
//                } ?: response
            }
            403 -> {
                Log.e("인터셉터", "권한이 없습니다.")
            }
        }
        return response
    }
}
