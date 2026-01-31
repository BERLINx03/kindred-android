package com.example.kindred

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log

class MusicRepository(val context: Context) {
    val contentResolver = context.contentResolver
    private val uri = Media.EXTERNAL_CONTENT_URI
    private val projection = arrayOf<String>(
        Media._ID,
        Media.TITLE,
        Media.DISPLAY_NAME,
        Media.DATA,
        Media.DURATION
    )

    val selection = "duration < 360000"
    private val songs = mutableListOf<Music>()
    fun fetchDeviceMusics(): List<Music> {
        android.util.Log.d(
            "MusicRepository",
            "This is the music pass to append ids to: ${Media.EXTERNAL_CONTENT_URI}"
        )
        contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        ).use { cursor ->
            if (cursor != null) {
                val idColumn = cursor.getColumnIndex(Media._ID)
                Log.d("MusicRepository", "id column is $idColumn")
                val titleColumn = cursor.getColumnIndex(Media.TITLE)
                android.util.Log.d("MusicRepository", "title Column is $titleColumn")
                val displayTitleColumn = cursor.getColumnIndex(Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndex(Media.DATA)
                val durationC = cursor.getColumnIndex(Media.DURATION)
                android.util.Log.d("MusicRepository", "duration Column is $durationC")
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val displayName = cursor.getString(displayTitleColumn)
                    val data = cursor.getString(dataColumn)
                    val duration = cursor.getLong(durationC)
                    Log.d("MusicRepository", "duration = ${helperFormat(duration)}")
                    val uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id)
                    songs.add(Music(id, uri, title, displayName, data))
                }
            }
        }
        Log.i("MusicRepository", "${songs.size}")
        songs.forEach { song ->
            Log.d("MusicRepository", "Song: $song")
        }
        return songs
    }
}

fun helperFormat(time: Long): String {
    val hours = time / (1000 * 60 * 60)
    var reminder = time % (1000 * 60 * 60)
    val minutes = reminder / (1000 * 60)
    reminder %= (1000 * 60)
    val seconds = reminder / 1000

    return "$hours:$minutes:$seconds"
}

data class Music(
    val id: Long,
    val uri: Uri,
    val title: String,
    val displayName: String,
    val data: String
)