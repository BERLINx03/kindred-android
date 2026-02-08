package com.example.kindred.ui.background

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.kindred.Song
import kotlinx.coroutines.delay

@Composable
fun DemoScreen(
    modifier: Modifier = Modifier,
    songs: List<Song>
) {
    val colors = listOf(
        Color(0xFFFF5A5A),
        Color(0xFFFFBE3D),
        Color(0xFFD3FF5A),
        Color(0xFF5AFFB8),
        Color(0xFF5AFAFF),
        Color(0xFF5A9CFF),
        Color(0xFF6A5AFF),
        Color(0xFFC55AFF),
        Color(0xFFFF5A94),
    )
    val darkColors = listOf(
        Color(0xFFBD3030),
        Color(0xFFAF8024),
        Color(0xFF83A525),
        Color(0xFF2B8D63),
        Color(0xFF288D91),
        Color(0xFF294E85),
        Color(0xFF2E248D),
        Color(0xFF5C1F7E),
        Color(0xFF812344),
    )

    val colorIndex = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        colorIndex.intValue += 1
    }
    LaunchedEffect(colorIndex.intValue) {
        delay(2000)
        if (colorIndex.intValue < darkColors.lastIndex) {
            colorIndex.intValue += 1
        } else {
            colorIndex.intValue = 0
        }
    }
    val animatedColor by animateColorAsState(
        targetValue = colors[colorIndex.intValue],
        animationSpec = tween(2000)
    )
    val animatedDarkColor by animateColorAsState(
        targetValue = darkColors[colorIndex.intValue],
        animationSpec = tween(2000)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        animatedColor,
                        animatedDarkColor
                    )
                )
            )
    )
}

@Composable
@Preview
fun BackgroundPreview() {
    DemoScreen(songs = emptyList<Song>())
}