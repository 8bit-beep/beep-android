package com.test.beep_and.feature.network.core.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.test.beep_and.feature.network.BeepUrl
import com.test.beep_and.feature.network.login.DAuthService
import com.test.beep_and.feature.network.login.LoginService
import com.test.beep_and.feature.network.token.TokenService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        val gson = GsonBuilder().setLenient().create()

        val client = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(RequestInterceptor(NetworkUtil(context)))
            .addInterceptor(ResponseInterceptor())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BeepUrl.BASE_URL + "/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun getRetrofit(): Retrofit {
        return retrofit ?: throw IllegalStateException("RetrofitClient is not initialized")
    }

    val loginService: LoginService by lazy {
        getRetrofit().create(LoginService::class.java)
    }

    val tokenService: TokenService by lazy {
        getRetrofit().create(TokenService::class.java)
    }

}

object DodamRetrofitClient {
    private var retrofit: Retrofit? = null

    fun init() {
        val gson = GsonBuilder().setLenient().create()

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BeepUrl.DODAM_URL + "/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun getRetrofit(): Retrofit {
        return retrofit ?: throw IllegalStateException("DodamRetrofitClient is not initialized")
    }

    val dAuthService: DAuthService by lazy {
        getRetrofit().create(DAuthService::class.java)
    }
}

