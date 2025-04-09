package com.test.beep_and.feature.network.user.room

import com.test.beep_and.feature.network.BeepUrl
import retrofit2.http.Body
import retrofit2.http.PATCH


interface RoomService {
    @PATCH(BeepUrl.Auth.ROOM)
    suspend fun room(@Body roomName: RoomRequest)

}