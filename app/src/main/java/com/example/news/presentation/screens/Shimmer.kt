package com.example.news.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.*

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerAnim"
    )

    val colors = listOf(
        Color.LightGray.copy(alpha = 0.9f),
        Color.White.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.9f)
    )

    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(x = translateAnim.value - 200f, y = 50f),
        end = Offset(x = translateAnim.value, y = - 200f)
    )
    return this.background(brush)
}

@Composable
fun Modifier.showOnLoading(isLoading: Boolean): Modifier {
    return if (isLoading) {
        this.shimmerEffect()
    } else {
        this
    }
}
