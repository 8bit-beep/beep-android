package com.test.beep_and.feature.screen.signMove

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.screen.profile.ProfileViewModel
import com.test.beep_and.feature.screen.profile.model.ProfilePendingUiState
import com.test.beep_and.feature.screen.signMove.model.SignMovePendingUiState
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.button.Button
import com.test.beep_and.res.component.button.DropDownButton
import com.test.beep_and.res.component.list.RoomList
import com.test.beep_and.res.component.list.TimeList
import com.test.beep_and.res.component.textField.OutlinedTextField
import com.test.beep_and.res.modifier.clearFocusOnClick

@Composable
fun SignMoveScreen(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel(),
    popBackStack: () -> Unit,
    signMoveViewModel: SignMoveViewModel = viewModel(),
    navigateToMove: () -> Unit
) {
    val scroll = rememberScrollState()

    val state = profileViewModel.state.collectAsState()
    val signState = signMoveViewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    var showRoomList by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    var time by remember { mutableIntStateOf(8) }

    var fixRoom by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showRoomError by remember { mutableStateOf(false) }
    var showTimeError by remember { mutableStateOf(false) }


    var reason by remember { mutableStateOf("") }

    when (state.value.profileUiState) {
        is ProfilePendingUiState.Success -> {
            fixRoom =
                (state.value.profileUiState as ProfilePendingUiState.Success).myData.fixedRoom?.name
                    ?: "고정실이 없습니다."
        }

        is ProfilePendingUiState.Error -> {

        }

        else -> {}
    }

    when (signState.value.signMoveUiState) {
        is SignMovePendingUiState.Success -> {
            navigateToMove()
        }

        else -> {
        }
    }

    val verScroll = rememberScrollState()

    var moveToRoom by remember { mutableStateOf("PROJECT3") }

    LaunchedEffect(Unit) {
        profileViewModel.getMyInfo()
    }

    LaunchedEffect(signState.value.signMoveUiState) {
        when (val signMoveState = signState.value.signMoveUiState) {
            is SignMovePendingUiState.Error -> {
                showTimeError = signMoveState.error.status == 400
            }
            else -> {}
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.login_background)
            .padding(top = 46.dp)
            .padding(horizontal = 12.dp)
            .verticalScroll(scroll)
            .clearFocusOnClick(focusManager)
    ) {
        Row {
            Image(
                painter = painterResource(R.drawable.arrow),
                contentDescription = null,
                modifier = modifier
                    .size(40.dp)
                    .clickable {
                        popBackStack()
                    }
            )
            Spacer(Modifier.width(15.dp))
            Text(
                text = "실 이동 신청",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight(600),
                )
            )
        }
        Spacer(modifier.height(37.dp))
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
                .height(425.dp)
                .padding(horizontal = 22.dp, vertical = 18.dp)
                .verticalScroll(verScroll)
        ) {
            Text(
                text = "이동 내용",
                fontSize = 24.sp,
                fontWeight = FontWeight(500),
                color = Color.Black,
            )
            Spacer(Modifier.heightIn(16.dp))
            Text(
                text = "이동 사유",
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = AppColors.dark,
            )
            Spacer(Modifier.heightIn(10.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                hint = "사유를 적어주세요.",
                error = showError
            )
            AnimatedVisibility(visible = showError) {
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "사유를 적어주세요",
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500),
                    color = AppColors.red,
                    modifier = modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
            }
            Spacer(Modifier.heightIn(16.dp))
            Text(
                text = "이동 실",
                fontSize = 16.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF7D7D7D),
            )
            Spacer(Modifier.heightIn(10.dp))
            DropDownButton(
                showString = moveToRoom,
                show = showRoomList,
                onClick = {
                    showRoomList = !showRoomList
                    focusManager.clearFocus()
                },
                modifier = modifier
                    .fillMaxWidth(),
                error = showRoomError
            )
            AnimatedVisibility(visible = showRoomError) {
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "고정 실로는 이동할 수 없습니다",
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500),
                    color = AppColors.red,
                    modifier = modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
            }
            if (showRoomList) {
                RoomList(
                    selectedRoom = {
                        moveToRoom = it.name
                        showRoomList = false
                    }
                )
            }
            Spacer(Modifier.heightIn(16.dp))
            Text(
                text = "이동 교시",
                fontSize = 16.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF7D7D7D),
            )
            Spacer(Modifier.heightIn(10.dp))
            DropDownButton(
                showString = time.toString() + "~${time + 1} 교시",
                show = showTime,
                onClick = {
                    showTime = !showTime
                    focusManager.clearFocus()
                },
                modifier = modifier
                    .fillMaxWidth(),
                error = showTimeError
            )
            AnimatedVisibility(visible = showTimeError) {
                Spacer(Modifier.height(22.dp))
                Text(
                    text = "이미 지난 교시는 신청할 수 없습니다",
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500),
                    color = AppColors.red,
                    modifier = modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
            }
            if (showTime) {
                TimeList(
                    selectedTime = {
                        time = it
                        showTime = false
                    }
                )
            }

        }
        Spacer(Modifier.heightIn(44.dp))
        Button(
            buttonText = "확인",
            onClick = {
                when {
                    moveToRoom == fixRoom -> showRoomError = true
                    reason.isBlank() -> showError = true
                    else -> {
                        signMoveViewModel.signMove(
                            moveTo = moveToRoom,
                            reason = reason,
                            moveTime = time
                        )
                        showError = false
                        showRoomError = false
                        showTimeError = false
                    }
                }
            },
            backgroundColor = AppColors.dark
        )
        Spacer(Modifier.heightIn(33.dp))
    }
}

