package com.example.kindred

import android.Manifest
import android.animation.ObjectAnimator
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.kindred.ui.ExperimentalOnBoardingScreen
import com.example.kindred.ui.background.DemoScreen
import com.example.kindred.ui.background.Playlist
import com.example.kindred.ui.theme.KindredTheme
import com.example.kindred.ui.theme.SplashViewModel
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {

    private val TAG = "OBSERVE"
    private val splashViewModel by viewModels<SplashViewModel>()
    var exoplayer: ExoPlayer? by mutableStateOf(null)
    var mediaControl: MutableState<MediaController?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition(fun(): Boolean { return !splashViewModel.isLoaded.value })
            setOnExitAnimationListener { screen ->
                val animatorX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.8f,
                    0.0f
                )
                animatorX.interpolator = OvershootInterpolator()
                animatorX.duration = 500L
                animatorX.doOnEnd { screen.remove() }
                val animatorY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.8f,
                    0.0f
                )
                animatorY.interpolator = OvershootInterpolator()
                animatorY.duration = 500L
                animatorY.doOnEnd { screen.remove() }

                animatorX.start()
                animatorY.start()
            }
        }
        super.onCreate(savedInstanceState)
        startForegroundService(Intent(this, MediaService::class.java))
        val repo = MusicRepository(this.applicationContext)
        enableEdgeToEdge()
        setContent {
            var isLoading by remember { mutableStateOf(false) }
            mediaControl = remember { mutableStateOf<MediaController?>(null) }
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
                    lifecycleScope.launch(Dispatchers.IO) {
                        val fetchedSongs = repo.fetchDeviceMusics()
                        withContext(Dispatchers.Main) {
                            songs = fetchedSongs
                        }
                    }
                } else {
                    android.util.Log.d("MusicRepository", "Permission ain't granted!")
                }
            }
            var permissionRequested by remember { mutableStateOf(false) }

            LaunchedEffect(flag.value) {
                if (flag.value && !permissionRequested) {
                    permissionRequested = true
                    val p = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    launcher.launch(p)
                }
            }
            KindredTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when {
                        !flag.value -> {
                            splashViewModel.setIsLoaded(false)
                            ExperimentalOnBoardingScreen { done ->
                                flag.value = done
                                file.writeBytes(byteArrayOf(1))
                            }
                        }

                        songs.isNotEmpty() && mediaControl != null -> {
                            splashViewModel.setIsLoaded(true)
//                            DemoScreen(songs = songs, player = mediaControl!!.value!!)
                            Playlist(songs = songs)
                        }

                        else -> {
                            splashViewModel.setIsLoaded(false)
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

    override fun onStart() {
        super.onStart()

        val sessionToken = SessionToken(this, ComponentName(this, MediaService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaControl?.value = controllerFuture.get()
            },
            MoreExecutors.directExecutor()
        )

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
