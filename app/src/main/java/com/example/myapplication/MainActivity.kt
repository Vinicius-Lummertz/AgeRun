package com.example.myapplication

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.mutableStateOf
import com.example.myapplication.data.RepositoryProvider
import com.example.myapplication.ui.theme.AgeGoTheme
import com.example.myapplication.ui.theme.PurpleBackground
import com.example.myapplication.ui.theme.PurpleDeep

class MainActivity : ComponentActivity() {
    private val deepLinkEventId = mutableStateOf<String?>(null)

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
        deepLinkEventId.value = intent.eventDeepLinkId()
        setContent { AgeGoTheme { AgeGoApp(initialEventId = deepLinkEventId.value) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkEventId.value = intent.eventDeepLinkId()
    }

    private fun Intent?.eventDeepLinkId(): String? = this?.data
        ?.takeIf { it.scheme == "agego" && it.host == "event" }
        ?.pathSegments?.firstOrNull()
}
