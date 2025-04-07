package com.test.beep_and.feature.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.test.beep_and.App
import com.test.beep_and.R
import com.test.beep_and.res.AppColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = viewModel(),
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    val context = LocalContext.current


    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )
        viewModel.refreshToken(
            context = context,
            navigateToLogin = navigateToLogin,
            navigateToHome = navigateToHome
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.dark)
    ) {
        Image(
            painter = painterResource(R.drawable.white_logo),
            contentDescription = null,
            modifier = modifier
                .align(alignment = Alignment.Center)
        )
    }
}
