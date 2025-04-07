package com.test.beep_and.res.component.textField

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
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
    focusRequester: FocusRequester
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .shadow(
                elevation = 5.dp,
                spotColor = Color(0x05000000),
                ambientColor = Color(0x05000000)
            )
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        BasicTextField(
            modifier = modifier
                .align(alignment = Alignment.CenterStart)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = text,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 12.sp),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        color = AppColors.placeholder,
                        fontSize = 12.sp,
                        modifier = modifier
                            .align(alignment = Alignment.CenterStart)
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

@Composable
@Preview
fun AuthTextFieldPreview() {
    var text by remember { mutableStateOf("") }
    val focut = FocusRequester()
    AuthTextField(
        text = text,
        onValueChange = { text = it },
        hint = "아이디를 입력하세요",
        focusRequester = focut
    )
}