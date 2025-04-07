package com.test.beep_and.feature.network.core.model


data class Response<T>(
    val status: Int,
    val state: String,
    val message: String,
    val data: T? = null,
)

data class DefaultResponse<T>(
    val state: String,
    val data: T? = null,
)


