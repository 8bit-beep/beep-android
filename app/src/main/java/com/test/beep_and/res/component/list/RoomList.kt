package com.test.beep_and.res.component.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.beep_and.feature.network.user.model.Room
import com.test.beep_and.feature.network.user.model.RoomModel
import com.test.beep_and.res.AppColors
import kotlinx.coroutines.delay

@Composable
fun RoomList(
    selectedRoom: (RoomModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val room = Room()
    val roomList = room.roomList

    val shape = RoundedCornerShape(10.dp)
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .border(1.dp, AppColors.grey, shape)
            .clip(shape)
            .background(Color.White)
            .verticalScroll(scroll)
    )
    {
        roomList.forEachIndexed { index, roomModel ->
            key(roomModel.name) {
                var visible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(index * 30L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 150)
                    ) + fadeIn(animationSpec = tween(150)),
                    exit = slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 150)
                    ) + fadeOut(animationSpec = tween(150))
                )
                {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRoom(roomModel)
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = room.parseRoomName(roomModel.name),
                                fontSize = 16.sp,
                                fontWeight = FontWeight(400),
                                color = Color.Black
                            )
                        }

                        if (index != roomList.lastIndex) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = AppColors.grey
                            )
                        }
                    }
                }
            }
        }
    }
}





