package com.example.kindred.ui.background

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.common.math.LinearTransformation.horizontal


@Composable
fun SongCard(modifier: Modifier = Modifier, imgUri: Any, songName: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SongImage(imgUri)

        Spacer(Modifier.width(16.dp))

        Text(
            text = songName,
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

@Composable
fun SongImage(imgUri: Any) {
    AsyncImage(
        model = imgUri,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.clip(RoundedCornerShape(10.dp)).size(45.dp)
    )
}