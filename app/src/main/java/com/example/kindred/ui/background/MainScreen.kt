package com.example.kindred.ui.background

import android.util.Log
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.kindred.Song
import com.example.kindred.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

private const val TAG1 = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PlayerViewModel, player: Player, songs: List<Song>) {
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val index = viewModel.currentPlayingIndex.collectAsState().value
    val currentSong = remember(index) { songs.getOrNull(index) }

    LaunchedEffect(Unit) {
        songs.forEach {
            player.addMediaItem(MediaItem.fromUri(it.song))
        }
        player.prepare()
    }

    BottomSheetScaffold(
        sheetContent = {
            PlayingScreen(viewModel, currentSong!!, player)
        },
        sheetPeekHeight = 0.dp,
        scaffoldState = sheetState
    ) {
        Playlist(
            songs = songs,
            onSongClicked = { song ->
                Log.d(TAG1, "MainScreen: ${song.displayName} ")
                val index = songs.indexOf(song)
                if (index != -1) {
                    player.seekTo(index, 0)
                    player.play()
                }
                scope.launch {
                    sheetState.bottomSheetState.expand()
                }
            }
        )
    }
}