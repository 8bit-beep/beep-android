package com.test.beep_and.feature.screen.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.beep_and.BeepApplication
import com.test.beep_and.R
import com.test.beep_and.feature.network.core.remote.NetworkUtil
import com.test.beep_and.res.AppColors
import com.test.beep_and.res.component.button.AuthButton
import com.test.beep_and.res.component.textField.AuthTextField

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val idFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current


    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                LoginSideEffect.Success -> {
                    viewModel.saveTokens(context)
                    navigateToHome()
                }

                LoginSideEffect.Failed -> {
                    viewModel.updateDialog(true)
                }
            }
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.background)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 56.dp)
                .align(alignment = Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.bbick_logo),
                contentDescription = null,
                modifier = modifier
                    .width(111.dp)
                    .height(100.dp)
            )
            Spacer(Modifier.height(70.dp))
            Text(
                text = startText
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
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )
            Spacer(Modifier.height(16.dp))
            AuthTextField(
                text = password,
                onValueChange = { password = it },
                hint = "비밀번호를 입력해 주세요",
                focusRequester = passwordFocusRequester,
                keyboardOption = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions (
                    onDone = { focusManager.clearFocus() }
                )
            )
            Spacer(Modifier.height(35.dp))
            AuthButton(
                onClick = {
                    viewModel.login(id, password, NetworkUtil(context))
                },
                buttonText = "로그인"
            )
        }
    }
}