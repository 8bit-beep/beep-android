package com.test.beep_and.feature.network.login

data class DAuthLoginResponse (
    val name: String,
    val profileImage: String,
    val location: String
)

data class LoginResponse (
    val accessToken: String,
    val refreshToken: String
)