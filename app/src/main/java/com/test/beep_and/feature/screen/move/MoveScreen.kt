package com.test.beep_and.feature.screen.move

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.network.user.model.Room
import com.test.beep_and.feature.screen.move.model.MovePendingUiState
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.button.Button
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MoveScreen(
    modifier: Modifier = Modifier,
    viewModel: MoveViewModel = viewModel(),
    navigateToSignMove: () -> Unit,
    showDeleteMove: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                viewModel.getMyMove()
                delay(300)
                isRefreshing = false
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getMyMove()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.login_background)
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 46.dp, start = 12.dp, end = 12.dp)
        ) {
            item {
                Text(text = "실 이동", fontSize = 30.sp)
                Spacer(modifier = Modifier.height(30.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 22.dp, vertical = 18.dp)
                ) {
                    Text(text = "신청 목록", fontSize = 25.sp)
                    Spacer(Modifier.height(32.dp))

                    when (val moveState = state.moveUiState) {
                        is MovePendingUiState.Success -> {
                            if (moveState.myMove.isNotEmpty()) {
                                moveState.myMove.forEach { move ->
                                    MoveCard(
                                        nowRoom = move.fixedRoom,
                                        moveTo = move.shiftRoom,
                                        time = move.period,
                                        reason = move.reason,
                                        isAttended = true,
                                        deleteId = move.id.toInt(),
                                        onClick = {},
                                        deleteOnClick = showDeleteMove
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            } else {
                                Text(
                                    text = "신청 목록이 없습니다.",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFFB7B7B7),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Button(
            onClick = navigateToSignMove,
            backgroundColor = AppColors.dark,
            buttonText = "신청하기",
            modifier = Modifier
                .padding(horizontal = 13.dp)
                .padding(bottom = 20.dp)
                .align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun MoveCard(
    modifier: Modifier = Modifier,
    nowRoom: String,
    moveTo: String,
    time: Int,
    deleteId: Int,
    reason: String,
    isAttended: Boolean,
    onClick: () -> Unit,
    deleteOnClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val room = Room()

    Box(
        modifier = modifier
            .animateContentSize()
            .fillMaxWidth()
            .background(
                color = AppColors.componentBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(vertical = 13.dp, horizontal = 16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { expanded = !expanded },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row {
                    Text(
                        text = room.parseRoomName(nowRoom),
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        color = Color.Black,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "->",
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        color = Color.Black,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = room.parseRoomName(moveTo),
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600),
                        color = Color.Black,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = "$time~${time + 1}교시 · ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight(500),
                        color = Color.Black,
                    )
                    Text(
                        text = if (isAttended) "승인됨" else "거절,",
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        color = if (isAttended) AppColors.main else AppColors.red,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = reason,
                    fontSize = 12.sp,
                    fontWeight = FontWeight(300),
                    color = Color.Black,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Image(
                painter = painterResource(R.drawable.delete),
                contentDescription = "Delete",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(13.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onClick()
                        deleteOnClick(deleteId)
                    }
            )
        }
    }
}

@Composable
fun DeleteMove(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    visible: Boolean,
    onClick: () -> Unit,
    deleteMove: (Int) -> Unit,
    deleteId: Int
) {
    var isSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        isSheetVisible = visible
    }

    val transition = updateTransition(targetState = isSheetVisible, label = "move_sheet_transition")
    val offsetY by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "sheet_slide"
    ) { isVisible ->
        if (isVisible) 0.dp else 600.dp
    }
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97F else 1.0F,
        animationSpec = tween(durationMillis = 50),
        label = "scaleAnim"
    )

    val isVisible = offsetY < 600.dp
    if (isVisible) {
        Box(
            modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Column(
                modifier = modifier
                    .offset(y = offsetY)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .align(alignment = Alignment.Center)
                    .height(167.dp)
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "실 이동을 삭제하겠습니까?",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight(600),
                            color = Color.Black,
                        )
                    )
                    Text(
                        text = "삭제하면 다시 되돌릴 수 없습니다.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF7D7D7D),
                        )
                    )
                }
                Row {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(45.dp)
                            .background(
                                color = AppColors.grey,
                                shape = RoundedCornerShape(size = 8.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        tryAwaitRelease()
                                        isPressed = false
                                        onDismiss()
                                    }
                                )
                            }
                            .scale(scale)
                    ) {
                        Text(
                            text = "취소",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight(500),
                                color = Color.Black,
                            ),
                            modifier = modifier
                                .align(alignment = Alignment.Center)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(45.dp)
                            .background(
                                color = AppColors.red,
                                shape = RoundedCornerShape(size = 8.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        tryAwaitRelease()
                                        isPressed = false
                                        onClick()
                                        deleteMove(deleteId)
                                        onDismiss()
                                    }
                                )
                            }
                            .scale(scale)
                    ) {
                        Text(
                            text = "삭제",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight(500),
                                color = Color.White,
                            ),
                            modifier = modifier
                                .align(alignment = Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}
