package com.test.beep_and.feature.network.attend

import com.test.beep_and.feature.network.BeepUrl
import retrofit2.http.Body
import retrofit2.http.POST

interface AttendService {
    @POST(BeepUrl.Attends.POST_ATTENDS)
    suspend fun attend(@Body room: AttendRequest)

    @POST(BeepUrl.Attends.CANCEL_ATTENDS)
    suspend fun cancelAttend()
}
