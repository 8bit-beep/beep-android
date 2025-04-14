package com.test.beep_and.res.component.textField

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.beep_and.res.AppColors

@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (value.isEmpty()) AppColors.grey else Color.Black
    )

    val scroll = rememberScrollState()
    LaunchedEffect(value) {
        scroll.animateScrollTo(scroll.maxValue)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = animatedColor,
                shape = RoundedCornerShape(size = 10.dp)
            )
            .padding(
                vertical = 15.dp,
                horizontal = 17.dp
            )
            .heightIn(
                min = 50.dp,
                max = 100.dp
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scroll),
            textStyle = TextStyle(fontSize = 12.sp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
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
        )
    }
}