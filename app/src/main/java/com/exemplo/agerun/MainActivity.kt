package com.exemplo.agerun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.exemplo.agerun.ui.theme.AgeRunTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgeRunTheme {
                var authResponse by remember { mutableStateOf<AuthResponse?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (authResponse == null) {
                        AuthScreen(
                            modifier = Modifier.padding(innerPadding),
                            onAuthenticated = { authResponse = it },
                        )
                    } else {
                        AgeRunHomeScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
