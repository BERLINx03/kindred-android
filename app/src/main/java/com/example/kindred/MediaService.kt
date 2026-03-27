package com.example.kindred

import android.content.Intent
import android.media.AudioAttributes.USAGE_MEDIA
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@UnstableApi
class MediaService : MediaSessionService() {

    private companion object Players {
        lateinit var mediaSession: MediaSession
        lateinit var player: ExoPlayer
    }

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer
            .Builder(this)
            .build()
        player.setAudioAttributes(
            AudioAttributes.Builder().setContentType(AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )
        mediaSession = MediaSession.Builder(this, player).build()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
//        stopSelf()
    }

    override fun onDestroy() {
        mediaSession.run {
            release()
            player.release()
        }
        super.onDestroy()
    }
}