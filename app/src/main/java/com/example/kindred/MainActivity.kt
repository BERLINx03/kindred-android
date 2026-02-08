package com.example.kindred

import android.Manifest
import android.R.attr.contentDescription
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
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
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
//            val file = File(filesDir, "flag.bit")
//            file.writeBytes(byteArrayOf(if (flag) 1 else 0))
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
            //TODO("onBoarding screen -> this is an very early experimental version so if anything (which will) went wrong or you have any kinda of suggestion don't hesitate issuing it or text me on x or discord")
            KindredTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        onClick = { launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                    ) {
                        songs = repo.fetchDeviceMusics()
                    }
                        DemoScreen(songs = songs, player = exoplayer)
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
@Composable
fun Greeting(
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