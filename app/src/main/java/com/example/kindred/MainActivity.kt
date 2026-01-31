package com.example.kindred

import android.Manifest
import android.R.attr.onClick
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.example.kindred.ui.theme.KindredTheme
import kotlin.contracts.contract

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = MusicRepository(this.applicationContext)
        enableEdgeToEdge()
        setContent {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if(isGranted){
                    android.util.Log.d("MusicRepository","Permission Granted!")
                }else {
                    android.util.Log.d("MusicRepository","Permission ain't granted!")
                }
            }
            KindredTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onClick = { launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
                    ) { repo.fetchDeviceMusics() }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onClick: () -> Unit, onGetMusic: () -> Unit) {
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