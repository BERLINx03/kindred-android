package com.example.kindred

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import com.example.kindred.ui.ExperimentalOnBoardingScreen
import com.example.kindred.ui.background.DemoScreen
import com.example.kindred.ui.theme.KindredTheme
import java.io.File

class MainActivity : ComponentActivity() {

    lateinit var exoplayer: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = MusicRepository(this.applicationContext)
        exoplayer = ExoPlayer.Builder(this).build()
        enableEdgeToEdge()
        setContent {
            val file = File(filesDir, "flag.bit")
            val flag = remember {
                mutableStateOf(if (file.exists()) file.readBytes()[0] == 1.toByte() else false)
            }
            var songs by remember {
                mutableStateOf<List<Song>>(emptyList())
            }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    songs = repo.fetchDeviceMusics()
                } else {
                    android.util.Log.d("MusicRepository", "Permission ain't granted!")
                }
            }
            var permissionRequested by remember { mutableStateOf(false) }

            LaunchedEffect(flag.value) {
                if (flag.value && !permissionRequested) {
                    permissionRequested = true
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            KindredTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        !flag.value -> {
                            ExperimentalOnBoardingScreen { done ->
                                flag.value = done
                                file.writeBytes(byteArrayOf(1))
                            }
                        }
                        songs.isNotEmpty() -> {
                            DemoScreen(songs = songs, player = exoplayer)
                        }
                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

fun openGivenProfile(context: Context, profileUrl: String, packageName: String) {
    val pm = context.packageManager
    val isInstalled = try {
        pm.getPackageInfo(packageName, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = profileUrl.toUri()
        if (isInstalled) {
            setPackage(packageName)
        }
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "Cannot open profile", Toast.LENGTH_SHORT).show()
    }
}
