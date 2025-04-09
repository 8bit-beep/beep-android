package com.test.beep_and.res.component.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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

@Composable
fun HomeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAttendClick: () -> Unit,
    buttonText: String = "출석하기",
    loading: Boolean = false,
    isAttended: Boolean
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97F else 1.0F,
        animationSpec = tween(durationMillis = 50),
        label = "scaleAnim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .fillMaxWidth()
                    .height(55.dp)
                    .background(
                        color = if (isAttended) AppColors.red else AppColors.serve_color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (isAttended) {
                                    onAttendClick()
                                } else {
                                    onClick()
                                }
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (loading) {
                    LoadingDots()
                } else {
                    Text(
                        text = if (isAttended) "퇴실하기" else buttonText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight(800),
                        color = Color.White
                    )
                }
            }
        }
    }
}

