package com.example.kindred

import android.content.ContentResolver
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class MusicRepository(val context: Context) {
    private val tag = "MusicRepository"
    private val contentResolver: ContentResolver = context.contentResolver
    private val uri = Media.EXTERNAL_CONTENT_URI

    private val albumUri = "content://media/external/audio/albumart".toUri()
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
    fun fetchDeviceMusics(): Flow<List<Song>> = flow {
        Log.d(
            tag,
            "This is the music pass to append ids to: ${Media.EXTERNAL_CONTENT_URI} and this is the internal version ${Media.INTERNAL_CONTENT_URI}"
        )

        Log.d(tag, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.toString())
        contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        ).use { cursor ->
            if (cursor != null) {
                val idColumn = cursor.getColumnIndex(Media._ID)
                Log.d(tag, "id column is $idColumn")
                val titleColumn = cursor.getColumnIndex(Media.TITLE)
                Log.d(tag, "title Column is $titleColumn")
                val displayTitleColumn = cursor.getColumnIndex(Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndex(Media.DATA)
                val durationC = cursor.getColumnIndex(Media.DURATION)
                Log.d(tag, "duration Column is $durationC")
                val albumC = cursor.getColumnIndex(Media.ALBUM_ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val displayName = cursor.getString(displayTitleColumn)
                    val data = cursor.getString(dataColumn)
                    val duration = cursor.getLong(durationC)
                    val albumId = cursor.getLong(albumC)
                    val albumImgUri = ContentUris.withAppendedId(albumUri, albumId)
                    Log.d(tag, "Album id is $albumId")
                    Log.d(tag, "duration = ${helperFormat(duration)}")
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
        Log.i(tag, "${songs.size}")
        songs.forEach { song ->
            Log.d(tag, "Song: $song")
        }
        emit(songs)
    }.flowOn(Dispatchers.IO)

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

    return "${if (hours.toInt() == 0) "" else if(hours < 10) "0$hours:" else "$hours:"}${if (minutes < 10) "0$minutes" else minutes}:${if (seconds < 10) "0$seconds" else seconds}"
}

data class Song(
    val albumArtwork: Uri,
    val songArtwork: Bitmap?,
    val song: Uri,
    val title: String,
    val displayName: String,
    val data: String
)