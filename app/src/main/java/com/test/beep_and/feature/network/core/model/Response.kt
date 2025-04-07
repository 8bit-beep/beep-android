package com.test.beep_and.feature.network.core.model


data class DefaultResponse<T>(
    val state: String,
    val data: T? = null,
)


