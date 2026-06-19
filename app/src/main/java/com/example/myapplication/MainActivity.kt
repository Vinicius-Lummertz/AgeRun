package com.example.myapplication

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.example.myapplication.data.RepositoryProvider
import com.example.myapplication.ui.theme.AgeGoTheme
import com.example.myapplication.ui.theme.PurpleBackground
import com.example.myapplication.ui.theme.PurpleDeep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RepositoryProvider.init(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 7001)
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(PurpleBackground.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(PurpleDeep.toArgb())
        )
        setContent { AgeGoTheme { AgeGoApp() } }
    }
}
