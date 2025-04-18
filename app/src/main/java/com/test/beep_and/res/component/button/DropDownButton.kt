package com.test.beep_and.res.component.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.test.beep_and.R
import com.test.beep_and.feature.network.user.model.Room
import com.test.beep_and.res.AppColors

@Composable
fun DropDownButton(
    modifier: Modifier = Modifier,
    show: Boolean,
    onClick: () -> Unit,
    showString: String,
    error: Boolean = false
) {
    val rotateImage by animateFloatAsState(
        targetValue = if (show) 180F else 0F,
        animationSpec = tween(durationMillis = 50)
    )
    val roomPars = Room().parseRoomName
    Row(
        modifier = modifier
            .height(55.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, if (error) AppColors.red else Color.Black, RoundedCornerShape(10.dp))
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = roomPars(showString))
        Image(
            painter = painterResource(R.drawable.polygon_3),
            contentDescription = null,
            modifier = Modifier
                .rotate(rotateImage)
                .size(13.dp)
        )
    }
}

