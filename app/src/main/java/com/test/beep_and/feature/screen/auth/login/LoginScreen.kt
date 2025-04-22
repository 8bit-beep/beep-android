package com.test.beep_and.feature.screen.auth.login

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.R
import com.test.beep_and.feature.network.core.remote.NetworkUtil
import com.test.beep_and.feature.screen.auth.login.model.LoginPendingUiState
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.button.AuthButton
import com.test.beep_and.res.component.textField.AuthTextField
import com.test.beep_and.res.component.textField.AuthTextFieldType
import com.test.beep_and.res.modifier.FocusClearableContainer

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    BackHandler {}
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val idFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val isButtonEnabled = id.isNotEmpty() && password.isNotEmpty()

    val scrollState = rememberScrollState()

    val passwordFieldInFocus = remember { mutableStateOf(false) }

    val keyboardState by keyboardAsState()
    val isKeyboardOpen = keyboardState == Keyboard.Opened

    when (state.loginUiState) {
        is LoginPendingUiState.Success -> {
            loading = false
            navigateToHome()
        }

        is LoginPendingUiState.Error -> {
            loading = false
            error = (state.loginUiState as LoginPendingUiState.Error).error.toString()
        }

        LoginPendingUiState.Loading -> {
            loading = true
        }

        else -> {}
    }
    LaunchedEffect(passwordFieldInFocus.value, isKeyboardOpen) {
        if (passwordFieldInFocus.value && isKeyboardOpen) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    val startText = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = AppColors.dodam
            )
        ) {
            append("도담도담")
        }
        append(" 계정으로 시작하기")
    }

    FocusClearableContainer(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.background)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp)
                .padding(bottom = if (isKeyboardOpen) 200.dp else 40.dp)
                .align(alignment = Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.bbick_logo),
                contentDescription = null,
                modifier = modifier
                    .width(70.dp)
                    .height(60.dp)
            )
            Spacer(Modifier.height(40.dp))
            Text(
                text = startText,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                textAlign = TextAlign.Left
            )
            Spacer(Modifier.height(19.dp))
            AuthTextField(
                text = id,
                onValueChange = { id = it },
                hint = "아이디를 입력해 주세요",
                focusRequester = idFocusRequester,
                keyboardOption = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions (
                    onNext = {
                        passwordFocusRequester.requestFocus()
                        passwordFieldInFocus.value = true
                    }
                ),
                showError = error.isNotEmpty(),
                onFocusChanged = {
                    passwordFieldInFocus.value = false
                }
            )
            Spacer(Modifier.height(16.dp))
            AuthTextField(
                type = AuthTextFieldType.PASSWORD,
                text = password,
                onValueChange = { password = it },
                hint = "비밀번호를 입력해 주세요",
                focusRequester = passwordFocusRequester,
                keyboardOption = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions (
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()

                        if (isButtonEnabled) {
                            viewModel.login(id, password, NetworkUtil(context), context)
                        }
                    }
                ),
                showError = error.isNotEmpty(),
                onFocusChanged = {
                    passwordFieldInFocus.value = it
                },
            )
            Spacer(Modifier.height(28.dp))
            AuthButton(
                onClick = {
                    viewModel.login(id, password, NetworkUtil(context), context)
                },
                buttonText = "로그인",
                loading = loading,
                error = error,
                enabled = isButtonEnabled
            )
        }
    }
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

enum class Keyboard {
    Opened, Closed
}