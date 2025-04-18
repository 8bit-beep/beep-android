package com.test.beep_and

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.test.beep_and.feature.screen.auth.login.navigation.loginScreen
import com.test.beep_and.feature.screen.auth.login.navigation.navigateToLogin
import com.test.beep_and.feature.screen.home.HomeViewModel
import com.test.beep_and.feature.screen.home.model.RoomPendingUiState
import com.test.beep_and.feature.screen.home.navigation.HOME_ROUTE
import com.test.beep_and.feature.screen.home.navigation.homeScreen
import com.test.beep_and.feature.screen.home.navigation.navigateToHome
import com.test.beep_and.feature.screen.main.BottomNavigationBar
import com.test.beep_and.feature.screen.move.DeleteMove
import com.test.beep_and.feature.screen.move.MoveViewModel
import com.test.beep_and.feature.screen.move.model.DeleteMovePendingUiState
import com.test.beep_and.feature.screen.move.navigation.moveScreen
import com.test.beep_and.feature.screen.move.navigation.navigateToMove
import com.test.beep_and.feature.screen.profile.ProfileViewModel
import com.test.beep_and.feature.screen.profile.RoomSelectBottomSheet
import com.test.beep_and.feature.screen.profile.model.ProfilePendingUiState
import com.test.beep_and.feature.screen.profile.navigation.profileScreen
import com.test.beep_and.feature.screen.signMove.navigation.navigateToSignMove
import com.test.beep_and.feature.screen.signMove.navigation.signMoveScreen
import com.test.beep_and.feature.screen.splash.navigation.SPLASH_ROUTE
import com.test.beep_and.feature.screen.splash.navigation.splashScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


@Composable
fun App(navHostController: NavHostController = rememberNavController()) {
    val currentRoute = remember { mutableStateOf(HOME_ROUTE) }
    val systemUiController = rememberSystemUiController()

    var showDeleteMove by remember { mutableIntStateOf(-1) }
    var showRoomSheet by remember { mutableStateOf(false) }
    var showRoomForce by remember { mutableStateOf(false) }
    var selectedRoomName by remember { mutableStateOf("실을 선택해 주세요") }
    var fixedRoom by remember { mutableStateOf("") }

    val homeViewModel: HomeViewModel = viewModel()
    val moveVieModel: MoveViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val state by profileViewModel.state.collectAsState()
    val moveState by moveVieModel.delState.collectAsState()
    val roomState by homeViewModel.roomState.collectAsState()

    val context = LocalContext.current

    val previousRoomState = remember { mutableStateOf<RoomPendingUiState?>(null) }

    LaunchedEffect(roomState.roomUiState) {
        when (val currentRoomState = roomState.roomUiState) {
            is RoomPendingUiState.Success -> {
                if (previousRoomState.value != currentRoomState) {
                    Toast.makeText(context, "실 변경에 성공했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            is RoomPendingUiState.Error -> {
                if (previousRoomState.value != currentRoomState) {
                    Toast.makeText(context, "실 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {}
        }
        previousRoomState.value = roomState.roomUiState
    }

    LaunchedEffect(moveState.deleteUiState) {
        when (moveState.deleteUiState) {
            is DeleteMovePendingUiState.Success -> {
                Toast.makeText(context, "실 이동 요청 삭제에 성공했습니다", Toast.LENGTH_SHORT).show()
            }

            is DeleteMovePendingUiState.Error -> {
                Toast.makeText(context, "실 이동 요청 삭제에 실패했습니다", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        try {
            profileViewModel.getMyInfo()
        } catch (e: Exception) {
            Toast.makeText(context, "정보를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show()
            navHostController.navigateToLogin()
        }
    }


    LaunchedEffect(state.profileUiState) {
        when (val profileState = state.profileUiState) {
            is ProfilePendingUiState.Success -> {
                val profileData = profileState.myData
                if (profileData.fixedRoom?.name == null) {
                    fixedRoom = "고정실이 없습니다"
                    selectedRoomName = "고정실이 없습니다"
                    showRoomForce = true
                } else {
                    fixedRoom = profileData.fixedRoom.name
                    selectedRoomName = profileData.fixedRoom.name
                    showRoomForce = false
                }
            }

            is ProfilePendingUiState.Error -> {
            }

            else -> {}
        }
    }

    SideEffect {
        systemUiController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }


    LaunchedEffect(navHostController) {
        navHostController.currentBackStackEntryFlow
            .map { it.destination.route ?: HOME_ROUTE }
            .distinctUntilChanged()
            .collect {
                currentRoute.value = it
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (currentRoute.value in listOf("move", "home", "profile", "signMove")) {
                    BottomNavigationBar(navController = navHostController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navHostController,
                startDestination = SPLASH_ROUTE,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { getEnterTransition(initialState, targetState) },
                exitTransition = { getExitTransition(initialState, targetState) },
                popEnterTransition = { getPopEnterTransition(initialState, targetState) },
                popExitTransition = { getPopExitTransition(initialState, targetState) },
            ) {
                homeScreen()
                moveScreen(
                    navigateToSignMove = navHostController::navigateToSignMove,
                    showDeleteMove = { showDeleteMove = it }
                )
                profileScreen(
                    navigateToLogin = navHostController::navigateToLogin,
                    showRoomSheet = { showRoomSheet = it },
                    selectedRoom = selectedRoomName,
                    viewModel = profileViewModel,
                    homeViewModel = homeViewModel,
                )
                loginScreen(
                    navigateToHome = {
                        profileViewModel.getMyInfo()
                        navHostController.navigateToHome()
                    }
                )
                splashScreen(
                    navigateToLogin = navHostController::navigateToLogin,
                    navigateToHome = navHostController::navigateToHome
                )
                signMoveScreen(
                    popBackStack = navHostController::popBackStack,
                    navigateToMove = navHostController::navigateToMove
                )
            }
        }



        RoomSelectBottomSheet(
            visible = showRoomSheet,
            roomName = selectedRoomName,
            onDismiss = {
                showRoomSheet = false
            },
            onSelectRoom = {
                selectedRoomName = it
            },
            onConfirm = {
                if (selectedRoomName != fixedRoom) {
                    homeViewModel.room(selectedRoomName)
                    showRoomSheet = false
                } else {
                    Toast.makeText(context, "같은 실로는 변경할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(alignment = Alignment.Center),
            title = if (showRoomForce) "실 등록" else "실 수정",
            buttonText = if (showRoomForce) "등록하기" else "변경하기"
        )

        RoomSelectBottomSheet(
            visible = showRoomForce,
            roomName = selectedRoomName,
            onDismiss = { },
            onSelectRoom = {
                selectedRoomName = it
            },
            onConfirm = {
                if (selectedRoomName != fixedRoom) {
                    homeViewModel.room(selectedRoomName)
                    showRoomForce = false
                } else {
                    Toast.makeText(context, "같은 실로는 변경할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(alignment = Alignment.Center),
            title = "실 등록",
            buttonText = "등록하기"
        )

        DeleteMove(
            visible = showDeleteMove != -1,
            onDismiss = { showDeleteMove = -1 },
            deleteId = showDeleteMove,
            onClick = {
                moveVieModel.deleteMyMove(showDeleteMove)
                showDeleteMove = -1
            },
            deleteMove = { showDeleteMove = it },
        )
    }
}

enum class TransitionDirection {
    LEFT, RIGHT, UP, DOWN, CUSTOM
}

private val transitionMap = mapOf(
    "home" to mapOf(
        "profile" to TransitionDirection.RIGHT,
        "move" to TransitionDirection.LEFT
    ),
    "profile" to mapOf(
        "home" to TransitionDirection.LEFT,
        "move" to TransitionDirection.LEFT
    ),
    "move" to mapOf(
        "home" to TransitionDirection.RIGHT,
        "profile" to TransitionDirection.RIGHT,
        "signMove" to TransitionDirection.CUSTOM
    ),
    "signMove" to mapOf(
        "home" to TransitionDirection.RIGHT,
        "profile" to TransitionDirection.RIGHT,
        "move" to TransitionDirection.CUSTOM
    )
)

private fun getTransitionDirection(from: String?, to: String?): TransitionDirection? {
    return transitionMap[from]?.get(to)
}

fun getEnterTransition(initial: NavBackStackEntry, target: NavBackStackEntry): EnterTransition {
    val from = initial.destination.route
    val to = target.destination.route

    return when {
        from == "move" && to == "signMove" -> slideInVertically { it } + fadeIn()
        from == "signMove" && to == "move" -> slideInVertically { -it } + fadeIn()
        getTransitionDirection(from, to) == TransitionDirection.LEFT -> slideInHorizontally { -it } + fadeIn()
        getTransitionDirection(from, to) == TransitionDirection.RIGHT -> slideInHorizontally { it } + fadeIn()
        getTransitionDirection(from, to) == TransitionDirection.UP -> slideInVertically { -it } + fadeIn()
        getTransitionDirection(from, to) == TransitionDirection.DOWN -> slideInVertically { it } + fadeIn()
        else -> EnterTransition.None
    }
}

fun getExitTransition(initial: NavBackStackEntry, target: NavBackStackEntry): ExitTransition {
    val from = initial.destination.route
    val to = target.destination.route

    return when {
        from == "move" && to == "signMove" -> slideOutVertically { -it } + fadeOut()
        from == "signMove" && to == "move" -> slideOutVertically { it } + fadeOut()
        getTransitionDirection(from, to) == TransitionDirection.LEFT -> slideOutHorizontally { it } + fadeOut()
        getTransitionDirection(from, to) == TransitionDirection.RIGHT -> slideOutHorizontally { -it } + fadeOut()
        getTransitionDirection(from, to) == TransitionDirection.UP -> slideOutVertically { -it } + fadeOut()
        getTransitionDirection(from, to) == TransitionDirection.DOWN -> slideOutVertically { it } + fadeOut()
        else -> ExitTransition.None
    }
}



fun getPopEnterTransition(initial: NavBackStackEntry, target: NavBackStackEntry): EnterTransition {
    return getEnterTransition(initial, target)
}

fun getPopExitTransition(initial: NavBackStackEntry, target: NavBackStackEntry): ExitTransition {
    return getExitTransition(initial, target)
}

