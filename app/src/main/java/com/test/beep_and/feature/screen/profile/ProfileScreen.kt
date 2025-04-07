package com.test.beep_and.feature.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.test.beep_and.feature.data.user.clearToken

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        clearToken(context)
    }
}