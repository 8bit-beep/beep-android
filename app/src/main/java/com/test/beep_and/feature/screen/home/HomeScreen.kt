package com.test.beep_and.feature.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.beep_and.res.AppColors
import com.test.beep_and.R

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.login_background)
            .padding(horizontal = 35.dp)
            .padding(top = 16.dp)
    ) {
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
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(
                                color = AppColors.serve_color,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Text(
                            text = "출석하기",
                            modifier = modifier
                                .padding(vertical = 18.dp)
                                .align(alignment = Alignment.Center),
                            color = Color.White,
                            fontSize = 20.sp,
                            )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun HomePreview() {
    HomeScreen()
}