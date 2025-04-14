package com.test.beep_and.feature.network.move

data class MoveResponse (
    val id: String,
    val studentId: String,
    val username: String,
    val fixedRoom: String,
    val shiftRoom: String,
    val period: Int,
    val reason: String,
    val status: String
)
