package com.example.kindred.ui.viewmodel

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel: ViewModel() {

    private val _currentPlayingIndex = MutableStateFlow(0)
    val currentPlayingIndex = _currentPlayingIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPos = MutableStateFlow(0L)
    val currentPos = _currentPos.asStateFlow()


    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration = _totalDuration.asStateFlow()

    private val _progress = MutableStateFlow(0L)
    //For the ui progress bar
    val progress = _progress.asStateFlow()
    private var positionJob: Job? = null

    fun initPlayer(player: Player) {
        positionJob?.cancel()
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _totalDuration.value = player.duration.coerceAtLeast(0L)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _currentPlayingIndex.value = player.currentMediaItemIndex
                _totalDuration.value = player.duration.coerceAtLeast(0L)
            }
        })

        viewModelScope.launch {
            while (true) {
                _currentPos.value = player.currentPosition
                delay(500)
            }
        }
    }

    fun setCurrentPlayingIndex(i: Int){
        _currentPlayingIndex.value = i
    }

    fun setProgress(progress: Long){
        _progress.value = progress
    }
}