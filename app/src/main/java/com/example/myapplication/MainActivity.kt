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
    private val deepLinkWebPairToken = mutableStateOf<String?>(null)
    private val deepLinkStudentAccess = mutableStateOf<Pair<String, String>?>(null)
    private val deepLinkInviteCode = mutableStateOf<String?>(null)

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
        deepLinkWebPairToken.value = intent.webPairDeepLinkToken()
        deepLinkStudentAccess.value = intent.studentAccessDeepLink()
        deepLinkInviteCode.value = intent.inviteDeepLinkCode()
        setContent {
            AgeGoTheme {
                AgeGoApp(
                    initialEventId = deepLinkEventId.value,
                    initialWebPairToken = deepLinkWebPairToken.value,
                    onWebPairTokenConsumed = { deepLinkWebPairToken.value = null },
                    initialStudentAccessPhone = deepLinkStudentAccess.value?.first,
                    initialStudentAccessCode = deepLinkStudentAccess.value?.second,
                    initialInviteCode = deepLinkInviteCode.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkEventId.value = intent.eventDeepLinkId()
        deepLinkWebPairToken.value = intent.webPairDeepLinkToken()
        deepLinkStudentAccess.value = intent.studentAccessDeepLink()
        deepLinkInviteCode.value = intent.inviteDeepLinkCode()
    }

    private fun Intent?.eventDeepLinkId(): String? = this?.data
        ?.takeIf { it.scheme == "agego" && it.host == "event" }
        ?.pathSegments?.firstOrNull()

    private fun Intent?.webPairDeepLinkToken(): String? = this?.data
        ?.takeIf { it.scheme == "agego" && it.host == "web-pair" }
        ?.pathSegments?.firstOrNull()

    private fun Intent?.studentAccessDeepLink(): Pair<String, String>? = this?.data
        ?.takeIf { it.scheme == "agego" && it.host == "student-access" }
        ?.pathSegments
        ?.takeIf { it.size >= 2 }
        ?.let { it[0] to it[1] }

    private fun Intent?.inviteDeepLinkCode(): String? = this?.data
        ?.takeIf { it.scheme == "agego" && it.host == "invite" }
        ?.pathSegments?.firstOrNull()
}
