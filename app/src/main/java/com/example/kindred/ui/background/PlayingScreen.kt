package com.example.kindred.ui.background

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.example.kindred.R
import com.example.kindred.Song
import com.example.kindred.helperFormat
import com.example.kindred.ui.Controller
import com.example.kindred.ui.viewmodel.PlayerViewModel

@Composable
fun PlayingScreen(
    viewModel: PlayerViewModel,
    song: Song,
    player: Player,
) {
    val config = LocalConfiguration.current
    val imgSize = (config.screenWidthDp / (1.7)).dp

    val isPlaying = viewModel.isPlaying.collectAsState().value
    val currentPos = viewModel.currentPos.collectAsState().value
    val progress = viewModel.progress.collectAsState().value
    val totalDuration = viewModel.totalDuration.collectAsState().value

    val precent = currentPos.toFloat() /
            totalDuration.coerceAtLeast(1).toFloat()
    LaunchedEffect(Unit) {
        player.run {
            addMediaItem(MediaItem.fromUri(song.song))
            prepare()
        }

    }
    LaunchedEffect(player.isPlaying) {
        viewModel.setIsPlaying(player.isPlaying)
    }

    LaunchedEffect(player.currentPosition) {
        viewModel.setCurrentPosition(player.currentPosition)
    }
    LaunchedEffect(player.duration) {
        if (player.duration > 0) viewModel.setDuration(player.duration)
    }
    LaunchedEffect(player.currentMediaItemIndex) {
        viewModel.setCurrentPlayingIndex(player.currentMediaItemIndex)
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = song.title,
            fontSize = 42.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            shape = CircleShape,
            border = BorderStroke(3.dp, White),
            modifier = Modifier.size(imgSize)
        ) {
            AsyncImage(
                model = song.songArtwork ?: song.albumArtwork,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(54.dp))
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = helperFormat(player.currentPosition),
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
                        viewModel.setProgress(it.size)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures {
                            val xPos = it.x.toLong()
                            val seekPos =
                                (xPos * totalDuration) / progress.width.toLong()
                            player.seekTo(seekPos.coerceIn(0, totalDuration))
                        }
                    }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, _ ->
                            change.consume()
                            val xPos = change.position.x.toLong()
                            val seekPos =
                                (xPos * totalDuration) / progress.width.toLong()
                            player.seekTo(seekPos.coerceIn(0, totalDuration))
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
                icon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                size = 40.dp
            ) {
                if (isPlaying) player.pause()
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