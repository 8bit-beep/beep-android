package com.test.beep_and.feature.network.signMove

import com.test.beep_and.feature.network.BeepUrl
import retrofit2.http.Body
import retrofit2.http.POST

interface SignMoveService {
    @POST(BeepUrl.Shift.POST)
    suspend fun signMove(@Body signMoveRequest: SignMoveRequest)
}