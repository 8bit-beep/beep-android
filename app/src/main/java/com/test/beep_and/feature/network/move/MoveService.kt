package com.test.beep_and.feature.network.move

import com.test.beep_and.feature.network.BeepUrl
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface MoveService {
    @GET(BeepUrl.Shift.GET_MY)
    suspend fun getMyMove(): List<MoveResponse>

    @DELETE(BeepUrl.Shift.DEL)
    suspend fun deleteMyMove(@Path("shiftId") id: Int)
}