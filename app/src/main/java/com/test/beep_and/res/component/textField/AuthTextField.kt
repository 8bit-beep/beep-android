package com.test.beep_and.res.component.textField

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.beep_and.res.AppColors

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardOption: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester,
    showError: Boolean,
    isPassword: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val borderWidth by animateDpAsState(
        targetValue = if (showError) 1.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "borderWidth"
    )

    val focusState = remember { mutableStateOf(false) }
    val passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .shadow(
                    elevation = if (showError) 0.dp else 5.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color(0x05000000),
                    ambientColor = Color(0x05000000)
                )
                .then(
                    if (showError) {
                        Modifier.border(
                            width = borderWidth,
                            color = AppColors.red,
                            shape = RoundedCornerShape(20.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 15.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            focusState.value = it.isFocused
                            onFocusChanged(it.isFocused)
                        },
                    value = text,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(fontSize = 12.sp),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                text = hint,
                                color = AppColors.placeholder,
                                fontSize = 12.sp
                            )
                        }
                        innerTextField()
                    },
                    singleLine = true,
                    keyboardOptions = keyboardOption,
                    keyboardActions = keyboardActions,
                )
            }
        }
    }
}
