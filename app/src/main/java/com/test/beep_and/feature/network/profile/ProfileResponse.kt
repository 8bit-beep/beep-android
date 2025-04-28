package com.test.beep_and.feature.network.profile

import com.test.beep_and.feature.network.user.model.RoomModel

data class ProfileResponse (
    val username: String,
    val email: String,
    val grade: Int,
    val cls: Int,
    val num: Int,
    val fixedRoom: RoomModel?,
    val status: String,
    val role: String,
    val profileImage: String?
)

