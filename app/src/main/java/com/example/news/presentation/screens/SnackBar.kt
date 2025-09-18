package com.example.news.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SnackBar(
    message: String,
    color: Color = Color(0xFF731515),
    durationMillis: Long = 2000,
    isAnimation: Boolean = true
) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        delay(durationMillis)
        isVisible = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if(isAnimation){
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = androidx.compose.animation.core.EaseIn
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 300)
                ),
                modifier = Modifier
                    .align(Alignment.TopCenter)
            ) {
                SnackBarContent(message = message, color = color)
            }
        }
        else{
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
@Composable
fun SnackBarContent(
    message: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}