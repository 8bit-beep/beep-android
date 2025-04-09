package com.test.beep_and.feature.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.data.user.clearToken
import com.test.beep_and.feature.network.user.model.Room
import com.test.beep_and.feature.screen.home.HomeViewModel
import com.test.beep_and.feature.screen.profile.model.ProfilePendingUiState
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.brush.shimmerEffect
import com.test.beep_and.res.component.list.RoomList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    navigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val roomList = Room()
    var showRoomSelect by remember { mutableStateOf(false) }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97F else 1.0F,
        animationSpec = tween(durationMillis = 50)
    )
    val rotate by animateFloatAsState(
        targetValue = if (showRoomSelect) 180F else 0F,
        animationSpec = tween(durationMillis = 50)
    )


    val fixRoomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        }
    )
    val showFixRoomSheet by remember { mutableStateOf(false) }
    var showRoomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getMyInfo()
    }

    AnimatedVisibility(
        visible = showRoomSheet
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5F))
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .align(alignment = Alignment.Center)
                    .padding(vertical = 33.dp, horizontal = 22.dp)
            ) {
                Text(
                    text = "실 변경하기"
                )
                Spacer(Modifier.height(38.dp))
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                        .border(width = 1.dp, color = Color.Black)
                        .padding(start = 16.dp)
                        .clickable {
                            showRoomSelect = !showRoomSelect
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = (state.profileUiState as ProfilePendingUiState.Success).myData.fixedRoom?.name
                            ?: "실이 없습니다"
                    )
                    Image(
                        painter = painterResource(R.drawable.polygon_3),
                        contentDescription = null,
                        modifier = modifier
                            .rotate(rotate)
                    )
                }
                if (showRoomSelect) {
                    RoomList(
                        selectedRoom = {
                            homeViewModel.room(it.name)
                        },
                    )
                }
                Spacer(Modifier.height(38.dp))
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text(
                        modifier = modifier
                            .align(alignment = Alignment.Center)
                            .background(
                                color = AppColors.dark,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        text = "변경하기"
                    )
                }
            }
        }
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
                            .clickable {
                                homeViewModel.room(roomModel.name)
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
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        Text(
            modifier = modifier
                .padding(start = 14.dp),
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
                        shape = RoundedCornerShape(20.dp)
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

                        ProfileCard(left = "이름", right = profileData.username)
                        Spacer(Modifier.height(26.dp))

                        val studentId =
                            "${profileData.grade}학년${profileData.cls}반${profileData.num}번"
                        ProfileCard(left = "학번", right = studentId)
                        Spacer(Modifier.height(26.dp))

                        val roomInfo = profileData.fixedRoom?.name ?: "실이 없습니다"
                        ProfileCard(
                            left = "실",
                            right = roomList.parseRoomName(roomInfo),
                            icon = painterResource(R.drawable.pencil),
                            onClick = {

                            }
                        )
                    }

                    ProfilePendingUiState.Default -> {
                    }
                }
                ProfileCard(
                    left = "실",
                    right = roomList.parseRoomName("roomInfo"),
                    icon = painterResource(R.drawable.pencil),
                    onClick = {
                        showRoomSheet = !showRoomSheet
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .scale(scale)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(start = 30.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "로그아웃",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(500),
                    color = AppColors.red,
                    textAlign = TextAlign.Center,
                    modifier = modifier
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
                        }
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
                .clickable {
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
