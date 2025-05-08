package com.test.beep_and.feature.data.core.nfc

import android.content.Context import com.test.beep_and.feature.data.user.saveUser.ACC_TOKEN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun getNfc(context: Context): Boolean? {
    return runBlocking {
        val preferences = context.dataStore.data.first()
        preferences[NFC]
    }
}