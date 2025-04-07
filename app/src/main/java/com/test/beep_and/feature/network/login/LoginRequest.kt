package com.test.beep_and.feature.network.login

import java.lang.ProcessBuilder.Redirect

data class DAuthLoginRequest (
    val id : String,
    val pw : String,
    val clientId: String = "575fe863c46f4126a9c17e4af4b82d5d351bdff5507d454086a88edd19afa723",
    val redirectUrl: String = "https://beep.cher1shrxd.me/callback/dauth"
)

data class LoginRequest (
    val code: String
)