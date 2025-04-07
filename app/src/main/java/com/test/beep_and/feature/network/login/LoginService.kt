package com.test.beep_and.feature.network.login

import com.test.beep_and.feature.network.BeepUrl
import com.test.beep_and.feature.network.core.model.DefaultResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST(BeepUrl.DAuth.LOGIN)
    suspend fun login(@Body code: LoginRequest): LoginResponse
}

interface DAuthService {
    @POST(BeepUrl.DAuth.D_LOGIN)
    suspend fun dAuthLogin(@Body body: RequestBody): DefaultResponse<DAuthLoginResponse>
}
