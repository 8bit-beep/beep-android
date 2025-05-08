package com.test.beep_and.feature.data.core.nfc

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.runBlocking

val NFC = booleanPreferencesKey("nfc")

fun saveNfc(context: Context, nfc: Boolean) {
    runBlocking {
        try {
            context.dataStore.edit { preferences ->
                preferences[NFC] = nfc
            }
        } catch (e: Exception) {
            Log.d("토큰오류", "$e")
        }
    }
}
