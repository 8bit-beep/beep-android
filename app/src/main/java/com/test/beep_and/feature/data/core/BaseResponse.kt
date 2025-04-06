package com.test.beep_and.feature.data.core

data class BaseResponse<T> (
    val data: T? = null,
    val status: Int
)