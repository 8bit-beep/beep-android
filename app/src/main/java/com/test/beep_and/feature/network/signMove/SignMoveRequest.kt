package com.test.beep_and.feature.network.signMove

data class SignMoveRequest(
    val room: String,
    val reason: String,
    val period: Int,
    val date: String
)
