package com.test.beep_and.feature.network.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.test.beep_and.feature.network.core.remote.NetworkException
import com.test.beep_and.feature.network.core.remote.NoConnectivityException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NetworkErrorHandler {
    fun handle(context: Context, throwable: Throwable) {
        CoroutineScope(Dispatchers.Main).launch {
            val message = when (throwable) {
                is NoConnectivityException -> throwable.message ?: "인터넷 연결을 확인해주세요."
                is NetworkException -> throwable.message ?: "네트워크 오류가 발생했습니다."
                else -> "오류가 발생했습니다."
            }
            Log.d("예잇", "handle: $throwable")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}