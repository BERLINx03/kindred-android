package com.example.kindred.ui.background

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import com.example.kindred.R
import com.example.kindred.Song
import com.example.kindred.helperFormat
import com.example.kindred.ui.Controller
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("ConfigurationScreenWidthHeight", "RestrictedApi")
@Composable
fun DemoScreen(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    player: ExoPlayer
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
    val config = LocalConfiguration.current
    val pagerState = rememberPagerState { songs.size }
    val currentPlayingIndex = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        songs.forEach {
            player.addMediaItem(MediaItem.fromUri(it.song))
        }
    }
    val playing = remember {
        mutableStateOf(false)
    }
    val currentPos = remember {
        mutableLongStateOf(0L)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }

    //For the ui progress bar
    val progress = remember {
        mutableStateOf(IntSize(0, 0))
    }

    LaunchedEffect(player.isPlaying) {
        playing.value = player.isPlaying
    }

    LaunchedEffect(player.currentPosition) {
        currentPos.longValue = player.currentPosition
    }
    LaunchedEffect(player.duration) {
        if (player.duration > 0) totalDuration.longValue = player.duration
    }
    LaunchedEffect(player.currentMediaItemIndex) {
        currentPlayingIndex.intValue = player.currentMediaItemIndex
        pagerState.animateScrollToPage(player.currentMediaItemIndex, animationSpec = tween(500))
    }

    player.prepare()
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
    val precent = currentPos.longValue.toFloat() /
            totalDuration.longValue.coerceAtLeast(1).toFloat()
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
    ) {
        LaunchedEffect(pagerState.currentPage) {
            currentPlayingIndex.intValue = pagerState.currentPage
            player.seekTo(pagerState.currentPage, 0)
        }
        LaunchedEffect(config.screenWidthDp) {
            Log.d("UI", "${config.screenWidthDp}")
        }
        val pageSize = (config.screenWidthDp / (1.7)).dp
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            val textColor by animateColorAsState(
                targetValue = if (animatedColor.luminance() > .5f) Color(0xff414141) else White,
                animationSpec = tween(2000)
            )
            AnimatedContent(targetState = currentPlayingIndex.intValue, transitionSpec = {
                (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
            }) {
                Text(
                    text = songs[it].title,
                    fontSize = 42.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(pageSize),
                contentPadding = PaddingValues(horizontal = 85.dp)
            ) { page ->
                Card(
                    modifier = modifier
                        .size(pageSize)
                        .graphicsLayer {
                            val pageOffset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                            val alphaLerp = lerp(
                                start = 0.4f,
                                stop = 1f,
                                amount = 1f - pageOffset.coerceIn(0f, 1f)
                            )

                            val scaleLerp = lerp(
                                start = 0.5f,
                                stop = 1f,
                                amount = 1f - pageOffset.coerceIn(0f, 0.5f)
                            )

                            alpha = alphaLerp
                            scaleX = scaleLerp
                            scaleY = scaleLerp

                        },
                    shape = CircleShape,
                    border = BorderStroke(3.dp, White)
                ) {
                    AsyncImage(
                        model = songs[page].songArtwork ?: songs[page].albumArtwork,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.height(54.dp))
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = helperFormat(player.currentPosition),
                    color = textColor,
                    modifier = Modifier.width(55.dp),
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(8.dp)
                        .padding(horizontal = 8.dp)
                        .clip(CircleShape)
                        .background(White)
                        .onGloballyPositioned {
                            progress.value = it.size
                        }
                        .pointerInput(Unit) {
                            detectTapGestures {
                                val xPos = it.x.toLong()
                                val seekPos =
                                    (xPos * totalDuration.longValue) / progress.value.width.toLong()
                                player.seekTo(seekPos.coerceIn(0, totalDuration.longValue))
                            }
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, _ ->
                                change.consume()
                                val xPos = change.position.x.toLong()
                                val seekPos =
                                    (xPos * totalDuration.longValue) / progress.value.width.toLong()
                                player.seekTo(seekPos.coerceIn(0, totalDuration.longValue))
                            }
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(precent)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(Color(0xff414141))
                    )
                }
                Text(
                    text = if (player.duration > 0) {
                        helperFormat(player.duration)
                    } else {
                        "--:--"
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(55.dp),
                    color = textColor
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Controller(
                    icon = R.drawable.ic_fast_rewind,
                    size = 30.dp
                ) {
                    player.seekToPreviousMediaItem()
                }
                Controller(
                    icon = if (playing.value) R.drawable.ic_pause else R.drawable.ic_play,
                    size = 40.dp
                ) {
                    if (playing.value) player.pause()
                    else player.play()
                }
                Controller(
                    icon = R.drawable.ic_fast_forward,
                    size = 30.dp
                ) {
                    player.seekToNextMediaItem()
                }

            }
        }
    }
}

@Composable
@Preview
fun BackgroundPreview() {
    DemoScreen(songs = emptyList<Song>(), player = ExoPlayer.Builder(LocalContext.current).build())
}