package com.test.beep_and.feature.network.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.test.beep_and.feature.network.core.remote.NetworkException
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

object NetworkErrorHandler {
    fun handle(context: Context, throwable: Throwable): String? {
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
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        return errorMessage
    }
}
