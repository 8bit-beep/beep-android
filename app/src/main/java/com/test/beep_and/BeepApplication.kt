package com.test.beep_and

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.test.beep_and.feature.network.core.remote.DodamRetrofitClient
import com.test.beep_and.feature.network.core.remote.RetrofitClient
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BeepApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        DodamRetrofitClient.init()
        context = applicationContext
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext() = context
    }
}