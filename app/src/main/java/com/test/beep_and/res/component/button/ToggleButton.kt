package com.test.beep_and.res.component.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.beep_and.res.AppColors


@Composable
fun ToggleButton(
    modifier: Modifier = Modifier,
    visible: Boolean = false,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (visible) AppColors.main else AppColors.grey,
        label = "BackgroundColor"
    )
    val innerCircleColor by animateColorAsState(
        targetValue = if (visible) Color.White else AppColors.main,
        label = "InnerCircleColor"
    )
    val offsetX by animateDpAsState(
        targetValue = if (visible) 17.dp else 0.dp,
        label = "OffsetX"
    )

    Row(
        modifier = modifier
            .height(30.dp)
            .width(50.dp)
            .background(shape = CircleShape, color = backgroundColor)
            .padding(horizontal = 3.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(27.dp)
                .offset(x = offsetX)
                .background(shape = CircleShape, color = innerCircleColor)
        )
    }
}


@Composable
@Preview
fun ToggleButtonPreview() {
    ToggleButton()
}