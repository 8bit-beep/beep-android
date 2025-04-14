package com.test.beep_and.feature.screen.profile

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefreshIndicator
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.data.user.clearToken
import com.test.beep_and.feature.network.user.model.Room
import com.test.beep_and.feature.screen.home.HomeViewModel
import com.test.beep_and.feature.screen.home.model.RoomPendingUiState
import com.test.beep_and.feature.screen.profile.model.ProfilePendingUiState
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.brush.shimmerEffect
import com.test.beep_and.res.component.button.DropDownButton
import com.test.beep_and.res.component.list.RoomList
import com.test.beep_and.res.component.loading.RefreshableScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    homeViewModel: HomeViewModel,
    navigateToLogin: () -> Unit,
    showRoomSheet: (Boolean) -> Unit,
    selectedRoom: String
) {
    val state by viewModel.state.collectAsState()
    val roomState by homeViewModel.roomState.collectAsState()
    val context = LocalContext.current
    val roomList = Room()

    var isPressed by remember { mutableStateOf(false) }
    var isChangePressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97F else 1.0F,
        animationSpec = tween(durationMillis = 50)
    )
    when (val profileState = state.profileUiState) {
        is ProfilePendingUiState.Success -> profileState.myData.fixedRoom?.name ?: "실이 없습니다"
        else -> {}
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                viewModel.getMyInfo()
                delay(300)
                isRefreshing = false
            }
        }
    )

    LaunchedEffect(roomState) {
        when (roomState.roomUiState) {
            RoomPendingUiState.Success -> {
                viewModel.getMyInfo()
            }

            RoomPendingUiState.Loading -> {
            }

            else -> {}
        }
    }

    val fixRoomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        }
    )
    val showFixRoomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.getMyInfo()
    }

    if (showFixRoomSheet) {
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = fixRoomSheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "출석체크를 할 실을 선택해 주세요",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                roomList.roomList.forEach { roomModel ->
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        homeViewModel.room(selectedRoom)
                                        isChangePressed = true
                                        tryAwaitRelease()
                                        isChangePressed = false
                                    }
                                )
                            }
                            .padding(vertical = 16.dp)
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = roomModel.club,
                            modifier = modifier.width(100.dp)
                        )
                        Text(
                            text = roomList.parseRoomName(roomModel.name)
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.login_background)
            .padding(top = 46.dp)
            .padding(horizontal = 12.dp)
            .pullRefresh(pullRefreshState)
    ) {
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
        )
        Text(
            text = "프로필",
            fontSize = 30.sp
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = modifier
                    .padding(top = 96.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 31.dp, vertical = 38.dp)
            ) {
                when (val profileState = state.profileUiState) {
                    ProfilePendingUiState.Loading, is ProfilePendingUiState.Error -> {
                        repeat(4) {
                            if (it > 0) Spacer(Modifier.height(26.dp))
                            ProfileCard(left = "", right = "", loading = true)
                        }
                    }

                    is ProfilePendingUiState.Success -> {
                        val profileData = profileState.myData

                        val studentId =
                            "${profileData.grade}학년 ${profileData.cls}반 ${profileData.num}번"
                        ProfileCard(
                            left = "학번",
                            right = studentId
                        )
                        Spacer(Modifier.height(26.dp))

                        val email = profileData.email
                        ProfileCard(
                            left = "이메일",
                            right = email
                        )
                        Spacer(Modifier.height(26.dp))

                        val roomInfo = profileData.fixedRoom?.name ?: "실이 없습니다"
                        ProfileCard(
                            left = "실",
                            right = roomList.parseRoomName(roomInfo),
                            icon = painterResource(R.drawable.pencil),
                            onClick = {
                                showRoomSheet(true)
                            }
                        )
                    }

                    ProfilePendingUiState.Default -> {
                        repeat(4) {
                            if (it > 0) Spacer(Modifier.height(26.dp))
                            ProfileCard(left = "", right = "", loading = true)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .scale(scale)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(start = 30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                clearToken(context = context)
                                navigateToLogin()
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "로그아웃",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = AppColors.red,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    left: String,
    right: String,
    loading: Boolean = false,
    icon: Painter? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = if (loading) shimmerEffect() else Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent, Color.Transparent
                    )
                )
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(left)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onClick()
                }
        ) {
            if (loading) {
                Spacer(modifier = Modifier.width(100.dp))
            } else {
                Text(right)
                icon?.let {
                    Spacer(modifier = Modifier.width(7.dp))
                    Icon(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RoomSelectBottomSheet(
    visible: Boolean,
    roomName: String,
    onDismiss: () -> Unit,
    onSelectRoom: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        isSheetVisible = visible
    }

    val transition = updateTransition(targetState = isSheetVisible, label = "room_sheet_transition")
    val offsetY by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "sheet_slide"
    ) { isVisible ->
        if (isVisible) 0.dp else 600.dp
    }

    val isVisible = offsetY < 600.dp

    var showDrop by remember { mutableStateOf(false) }
    var dropVisible by remember { mutableStateOf(false) }
    val roomPars = Room().parseRoomName

    LaunchedEffect(showDrop) {
        dropVisible = showDrop
    }

    fun changeShow() {
        showDrop = !showDrop
    }

    if (isVisible) {
        Box(
            modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 20.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        ) {
            Column(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .fillMaxWidth()
                    .heightIn(
                        max = 550.dp
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(vertical = 20.dp, horizontal = 22.dp)
            ) {
                Text(
                    text = "실 수정",
                    fontSize = 24.sp,
                    fontWeight = FontWeight(700),
                    color = AppColors.dark,
                )
                Spacer(modifier.height(30.dp))
                DropDownButton(
                    showString = roomPars(roomName),
                    show = dropVisible,
                    onClick = {
                        changeShow()
                    },
                    modifier = modifier
                        .fillMaxWidth()
                )

                if (dropVisible) {
                    RoomList(
                        selectedRoom = { room ->
                            onSelectRoom(room.name)
                            changeShow()
                        },
                    )
                }

                Spacer(modifier.height(30.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .background(
                            color = AppColors.dark,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onConfirm()
                            onDismiss()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "변경하기", color = Color.White)
                }
            }
        }
    }
}


