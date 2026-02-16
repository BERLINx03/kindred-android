package com.example.kindred

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

@UnstableApi
class MediaService: MediaSessionService(){

    private lateinit var mediaSession: MediaSession
    lateinit var player: ExoPlayer
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
//        player.addListener(object : Player.Listener{
//            override fun onEvents(player: Player?, events: Player.Events?) {
//                when(events){
//
//                }
//            }
//        })
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