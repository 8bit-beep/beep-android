package com.test.beep_and.res.component.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.loading.LoadingDots

@Composable
fun AuthButton(
    onClick: () -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    error: String?,
    enabled: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97F else 1.0F,
        animationSpec = tween(durationMillis = 50),
        label = "scaleAnim"
    )

    val buttonColor by animateColorAsState(
        targetValue = if (enabled) AppColors.dodam else AppColors.dodam.copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 300),
        label = "buttonColorAnim"
    )

    Box(
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !error.isNullOrEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                Text(
                    text = error ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 28.dp),
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .scale(scale)
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(
                        color = buttonColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                                if (enabled) {
                                    onClick()
                                }
                            }
                        )
                    }
                ,
                contentAlignment = Alignment.Center,
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
    }
}
