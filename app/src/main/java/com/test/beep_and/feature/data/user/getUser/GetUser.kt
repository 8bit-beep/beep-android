package com.test.beep_and.feature.data.user.getUser

import android.content.Context
import com.test.beep_and.feature.data.user.dataStore
import com.test.beep_and.feature.data.user.saveUser.ACC_TOKEN
import com.test.beep_and.feature.data.user.saveUser.REF_TOKEN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun getRefToken(context: Context): String? {
    return runBlocking {
        val preferences = context.dataStore.data.first()
        preferences[REF_TOKEN]
    }
}
fun getAccToken(context: Context): String? {
    return runBlocking {
        val preferences = context.dataStore.data.first()
        preferences[ACC_TOKEN]
    }
}


