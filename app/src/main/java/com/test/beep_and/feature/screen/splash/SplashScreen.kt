package com.test.beep_and.feature.screen.splash

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.data.core.nfc.getNfc
import com.test.beep_and.feature.data.core.nfc.saveNfc
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
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        delay(300)
        visible = true
    }

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

    LaunchedEffect(true) {
        Log.d("nfc", "SplashScreen: ${getNfc(context)}")
        if (getNfc(context) == null) {
            saveNfc(context, false)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.main),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.beep_logo),
            contentDescription = null,
            modifier = modifier
                .width(136.dp)
                .height(122.dp)
        )
        Spacer(Modifier.height(8.dp))
        AnimatedVisibility(visible = visible) {
            Text(
                text = "인원체크를 간편하게",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight(600),
                    color = Color.White,
                )
            )
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
@Preview
fun SplashPreview() {
    SplashScreen(
        navigateToLogin = {},
        navigateToHome = {}
    )
}
