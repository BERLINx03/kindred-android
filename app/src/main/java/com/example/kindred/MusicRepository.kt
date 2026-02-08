package com.example.kindred

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.util.Log
import androidx.core.net.toUri

const val TAG = "MusicRepository"

class MusicRepository(val context: Context) {
    val contentResolver = context.contentResolver
    private val uri = Media.EXTERNAL_CONTENT_URI

    private val albumArtUri = "content://media/external/audio/albumart".toUri()
    private val projection = arrayOf<String>(
        Media._ID,
        Media.TITLE,
        Media.DISPLAY_NAME,
        Media.DATA,
        Media.DURATION,
        Media.ALBUM_ID
    )

    val selection = "duration < 360000"
    private val songs = mutableListOf<Song>()
    fun fetchDeviceMusics(): List<Song> {
        android.util.Log.d(
            TAG,
            "This is the music pass to append ids to: ${Media.EXTERNAL_CONTENT_URI} and this is the internal version ${Media.INTERNAL_CONTENT_URI}"
        )

        Log.d(TAG, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.toString())
        contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        ).use { cursor ->
            if (cursor != null) {
                val idColumn = cursor.getColumnIndex(Media._ID)
                Log.d(TAG, "id column is $idColumn")
                val titleColumn = cursor.getColumnIndex(Media.TITLE)
                android.util.Log.d(TAG, "title Column is $titleColumn")
                val displayTitleColumn = cursor.getColumnIndex(Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndex(Media.DATA)
                val durationC = cursor.getColumnIndex(Media.DURATION)
                android.util.Log.d(TAG, "duration Column is $durationC")
                val albumC = cursor.getColumnIndex(Media.ALBUM_ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val displayName = cursor.getString(displayTitleColumn)
                    val data = cursor.getString(dataColumn)
                    val duration = cursor.getLong(durationC)
                    val albumId = cursor.getLong(albumC)
                    val albumImgUri = ContentUris.withAppendedId(albumArtUri, albumId)
                    Log.d(TAG, "Album id is $albumId")
                    Log.d(TAG, "duration = ${helperFormat(duration)}")
                    val songUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id)
                    val songArtwork = fetchSongArtwork(songUri, context)
                    songs.add(
                        Song(
                            albumImgUri,
                            songArtwork,
                            songUri,
                            title,
                            displayName,
                            data
                        )
                    )
                }
            }
        }
        Log.i(TAG, "${songs.size}")
        songs.forEach { song ->
            Log.d(TAG, "Song: $song")
        }
        return songs
    }

    /*
    * `fetchSongArtwork` tries to fetch the image embedded image, if not existed use the album image
    * */
    fun fetchSongArtwork(song: Uri, context: Context): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, song)
            val art = retriever.embeddedPicture

            if (art != null) {
                BitmapFactory.decodeByteArray(art, 0, art.size)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
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

data class Song(
    val albumArtwork: Uri,
    val songArtwork: Bitmap?,
    val song: Uri,
    val title: String,
    val displayName: String,
    val data: String
)