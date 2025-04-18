package com.test.beep_and.feature.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.res.AppColors

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
            .background(color = AppColors.main)
    ) {
        Image(
            painter = painterResource(R.drawable.white_logo),
            contentDescription = null,
            modifier = modifier
                .width(186.dp)
                .height(215.dp)
                .align(alignment = Alignment.Center)
        )
    }
}
