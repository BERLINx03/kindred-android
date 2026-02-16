package com.example.kindred.ui.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kindred.Song

@Composable
fun Playlist(modifier: Modifier = Modifier, songs: List<Song>, color: Color = DividerDefaults.color, onSongClicked: (Song) -> Unit) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize().padding(16.dp)
    ) {
        items(songs.size){ i ->

            SongCard(
                modifier = modifier.padding(2.dp),
                song = songs[i],
                onSongClicked = onSongClicked
            )

            Canvas(modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = color,
                    strokeWidth = 1.dp.toPx(),
                    start = Offset(60.dp.toPx(), 1.dp.toPx() / 2),
                    end = Offset(size.width, 1.dp.toPx() / 2),
                )
            }
        }
    }
}