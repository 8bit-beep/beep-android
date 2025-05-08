package com.test.beep_and.feature.data.core.nfc

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "nfc")