package com.test.beep_and.feature.network.token

import com.test.beep_and.feature.network.BeepUrl
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {
    @POST(BeepUrl.DAuth.REFRESH)
    suspend fun token(@Body refresh: AccTokenRequest): AccTokenResponse
}