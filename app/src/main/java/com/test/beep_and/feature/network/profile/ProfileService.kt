package com.test.beep_and.feature.network.profile

import com.test.beep_and.feature.data.core.BaseResponse
import com.test.beep_and.feature.network.BeepUrl
import retrofit2.http.GET


interface ProfileService {
    @GET(BeepUrl.Auth.GET_INFO)
    suspend fun profile(): BaseResponse<ProfileResponse>
}