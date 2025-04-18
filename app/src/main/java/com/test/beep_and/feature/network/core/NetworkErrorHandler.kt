package com.test.beep_and.feature.network.core

import android.content.Context
import android.widget.Toast
import retrofit2.HttpException

object NetworkErrorHandler {
    fun handle(context: Context, throwable: Throwable, isToast: Boolean = false): String? {
        val errorMessage = when (throwable) {
            is HttpException -> {
                try {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    val jsonObject = org.json.JSONObject(errorBody ?: "")
                    jsonObject.getString("message")
                } catch (e: Exception) {
                    throwable.message() ?: "알 수 없는 오류가 발생했습니다."
                }
            }

            else -> throwable.message ?: "알 수 없는 오류가 발생했습니다."
        }
        if (isToast) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
        return errorMessage
    }

    fun getStatus(throwable: Throwable): Int {
        return if (throwable is HttpException) {
            try {
                val errorBody = throwable.response()?.errorBody()?.string()
                val jsonObject = org.json.JSONObject(errorBody ?: "")
                jsonObject.optInt("status", -1)
            } catch (e: Exception) {
                -1
            }
        } else {
            -1
        }
    }
}
