package com.example.kindred

import android.Manifest
import android.R.attr.contentDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.example.kindred.ui.theme.KindredTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = MusicRepository(this.applicationContext)
        enableEdgeToEdge()
        setContent {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    android.util.Log.d("MusicRepository", "Permission Granted!")
                } else {
                    android.util.Log.d("MusicRepository", "Permission ain't granted!")
                }
            }
            var songs by remember {
                mutableStateOf<List<Song>>(repo.fetchDeviceMusics())
            }
            KindredTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onClick = { launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                    ) {
                        songs = repo.fetchDeviceMusics()
                    }

                    Column {
                        AsyncImage(
                            model = songs[0].songArtwork,
                            contentDescription = null
                        )
                        AsyncImage(
                            model = songs[0].albumArtwork,
                            contentDescription = null
                        )

                        AsyncImage(
                            model = songs[0].songArtwork,
                            contentDescription = null
                        )
                    }
//                        DemoScreen(songs = songs)
                }
            }

        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onGetMusic: () -> Unit
) {
    Column {
        Button(onClick) {
            Text(
                text = "Grant Permissions",
                modifier = modifier
            )
        }
        Button(onGetMusic) {
            Text(
                text = "Get Music",
                modifier = modifier
            )
        }
    }
}