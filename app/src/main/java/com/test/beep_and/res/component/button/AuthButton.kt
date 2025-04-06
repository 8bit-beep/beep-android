package com.test.beep_and.res.component.button

import androidx.collection.intSetOf
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.loading.LoadingDots
import kotlin.math.tanh

@Composable
fun AuthButton(
    onClick: () -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if(isPressed) 0.97F else 1.0F,
        animationSpec = tween(durationMillis = 50)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .fillMaxSize()
            .background(
                color = AppColors.dodam,
                shape = RoundedCornerShape(20.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onClick()
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            LoadingDots()
        } else {
            Text(
                text = buttonText,
                fontSize = 20.sp,
                fontWeight = FontWeight(800),
                color = Color.White
            )
        }
    }
}