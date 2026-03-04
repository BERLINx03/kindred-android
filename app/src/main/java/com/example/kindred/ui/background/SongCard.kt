package com.example.kindred.ui.background

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import com.example.kindred.R
import com.example.kindred.Song


@Composable
fun SongCard(modifier: Modifier = Modifier, song: Song, onSongClicked: (Song) -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Image){
            onSongClicked(song)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SongImage(song)

        Spacer(Modifier.width(16.dp))

        Text(
            text = song.displayName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = null
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SongImage(song: Song) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val imgSize = screenWidth * 0.13f
    ElevatedCard(
        modifier = Modifier
            .size(imgSize),
        elevation = CardDefaults.cardElevation(
            2.dp
        ),
        shape = RoundedCornerShape(5.dp)
    ) {
        // As Card doesn't have layout
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.songArtwork ?: song.albumArtwork)
                    .crossfade(true)
                    .error(R.drawable.apple_music_symbol_only)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize(.7f),
            )
        }
    }
}