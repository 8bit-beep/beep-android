package com.test.beep_and.feature.network.token

data class AccTokenResponse (
    val accessToken: String,
    val refreshToken: String?
)