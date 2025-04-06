package com.test.beep_and.feature.data.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.test.beep_and.feature.data.user.saveUser.ACC_TOKEN
import com.test.beep_and.feature.data.user.saveUser.REF_TOKEN

suspend fun clearToken(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(ACC_TOKEN)
        preferences.remove(REF_TOKEN)
    }

}

