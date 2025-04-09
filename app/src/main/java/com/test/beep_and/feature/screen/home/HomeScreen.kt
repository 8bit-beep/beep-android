package com.test.beep_and.feature.screen.home

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.data.user.getUser.getAccToken
import com.test.beep_and.feature.network.user.model.Room
import com.test.beep_and.feature.screen.home.model.HomePendingUiState
import com.test.beep_and.feature.screen.home.model.RoomPendingUiState
import com.test.beep_and.feature.screen.profile.ProfileViewModel
import com.test.beep_and.feature.screen.profile.model.ProfilePendingUiState
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.animation.NfcScanAnimation
import com.test.beep_and.res.component.button.HomeButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isNfcScannerVisible by viewModel.isNfcScannerVisible.observeAsState(false)
    val isScanningNow by viewModel.isScanningNow.observeAsState(false)
    val attendanceStatus by viewModel.attendanceStatus.observeAsState()
    val roomStatus by viewModel.roomStatus.observeAsState()
    val roomList = Room()
    val scope = rememberCoroutineScope()
    val state by profileViewModel.state.collectAsState()
    val showFixRoomSheet = (state.profileUiState as? ProfilePendingUiState.Success)
        ?.myData?.fixedRoom == null
    val scrollState = rememberScrollState()
    val tagData = (attendanceStatus as? HomePendingUiState.Success)?.room



    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val fixRoomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        }
    )
    val attendText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 17.sp,
            )
        ) {
            append("출석 상태")
        }
        withStyle(
            style = SpanStyle(
                fontSize = 17.sp,
                color = AppColors.dark
            )
        ) {
            append("0")
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
                                viewModel.room(roomModel.name)
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

    LaunchedEffect(roomStatus) {
        when (roomStatus) {
            is RoomPendingUiState.Success -> {
                Toast.makeText(context, "실 선택 성공", Toast.LENGTH_SHORT).show()
                scope.launch {
                    fixRoomSheetState.hide()
                }
            }

            is RoomPendingUiState.Error -> {
                Toast.makeText(context, "오류가 발생했습니다 \n다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    DisposableEffect(Unit) {
        profileViewModel.getMyInfo()
        viewModel.initNfcAdapter(context)
        onDispose {
            activity?.let { viewModel.stopNfcScan(it) }
        }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.login_background)
            .padding(horizontal = 35.dp)
            .padding(top = 16.dp)
    ) {
        Log.d("토큰", "홈에서 출력하는 토큰: ${getAccToken(context)}")
        Column {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.bbick_logo),
                    contentDescription = null,
                    modifier = modifier
                        .width(49.dp)
                        .height(44.dp)
                )
                Image(
                    painter = painterResource(R.drawable.what),
                    modifier = modifier
                        .size(30.dp),
                    contentDescription = null
                )
            }
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .scrollable(scrollState, orientation = Orientation.Vertical)
                    .padding(horizontal = 22.dp, vertical = 33.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "출석체크",
                        fontSize = 25.sp,
                        modifier = modifier
                            .fillMaxWidth()
                    )
                    Image(
                        painter = painterResource(R.drawable.and),
                        contentDescription = null,
                        modifier = modifier
                            .width(212.dp)
                            .height(235.dp)
                            .padding(bottom = 30.dp)
                    )
                    Spacer(Modifier.height(20.dp))
                    HomeButton(
                        onClick = {
                            activity?.let { viewModel.startNfcScan(it) }
                        },
                        isAttended = (state.profileUiState as ProfilePendingUiState.Success).myData.status == "ATTEND",
                        onAttendClick = {
                            activity?.let { viewModel.cancelAttend() }
                        }
                    )
                    AnimatedVisibility(
                        visible = (state.profileUiState as ProfilePendingUiState.Success).myData.status == "ATTEND",
                        enter = slideInVertically (
                            initialOffsetY = { -it }
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { -it }
                        )
                    ) {
                        Column(
                            modifier = modifier
                                .padding(vertical = 33.dp, horizontal = 22.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "출석 현황",
                                fontSize = 25.sp,
                                fontWeight = FontWeight(700),
                                color = AppColors.dark,
                            )
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = attendText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight(600),
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "출석 장소: ${tagData?.let { roomList.parseRoomName(it) }}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight(600),
                            )
                        }
                    }
                }
            }
        }
    }

    if (isNfcScannerVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                activity?.let { viewModel.stopNfcScan(it) }
                viewModel.resetAttendanceStatus()
            },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            NfcScannerContent(
                isScanningNow = isScanningNow,
                attendanceStatus = attendanceStatus,
                onClose = {
                    activity?.let { viewModel.stopNfcScan(it) }
                    viewModel.resetAttendanceStatus()
                },
            )
        }
    }
}

@Composable
fun NfcScannerContent(
    isScanningNow: Boolean,
    attendanceStatus: HomePendingUiState?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        when (attendanceStatus) {
            is HomePendingUiState.Loading -> {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "출석 처리 중",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = AppColors.dark
                )
            }

            is HomePendingUiState.Success -> {
                Image(
                    painter = painterResource(R.drawable.vector),
                    contentDescription = null,
                    Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "출석이 완료됐습니다",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.dark
                    )

                ) {
                    Text("확인")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            is HomePendingUiState.Error -> {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "오류",
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = attendanceStatus.message,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.dark
                    )

                ) {
                    Text("확인")
                }
            }

            null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "스캔 준비 완료",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기"
                        )
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.height(130.dp), contentAlignment = Alignment.Center) {
                    if (isScanningNow) {
                        NfcScanAnimation()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "기기를 단말기 근처에 가져다 대세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("취소")
                }
            }

            HomePendingUiState.Default -> {
            }
        }
    }
}



