package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R

val PurpleBackground = Color(0xFF1E0E38)
val PurpleSurface = Color(0xFF371665)
val PurpleDeep = Color(0xFF10071F)
val Lime = Color(0xFFAFE217)
val LimeMuted = Color(0xFF485B17)
val NavigationPurple = Color(0xFF5F2EC8)

private val AgeGoColors = darkColorScheme(
    primary = Lime,
    onPrimary = PurpleDeep,
    secondary = NavigationPurple,
    background = PurpleBackground,
    onBackground = Color.White,
    surface = PurpleSurface,
    onSurface = Color.White,
    surfaceVariant = PurpleDeep,
    onSurfaceVariant = Color(0xFFCAC2D4),
    error = Color(0xFFFF6B6B)
)

private val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

private val AgeGoTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = PoppinsFamily),
        displayMedium = displayMedium.copy(fontFamily = PoppinsFamily),
        displaySmall = displaySmall.copy(fontFamily = PoppinsFamily),
        headlineLarge = headlineLarge.copy(fontFamily = PoppinsFamily),
        headlineMedium = headlineMedium.copy(fontFamily = PoppinsFamily),
        headlineSmall = headlineSmall.copy(fontFamily = PoppinsFamily),
        titleLarge = titleLarge.copy(fontFamily = PoppinsFamily),
        titleMedium = titleMedium.copy(fontFamily = PoppinsFamily),
        titleSmall = titleSmall.copy(fontFamily = PoppinsFamily),
        bodyLarge = bodyLarge.copy(fontFamily = PoppinsFamily),
        bodyMedium = bodyMedium.copy(fontFamily = PoppinsFamily),
        bodySmall = bodySmall.copy(fontFamily = PoppinsFamily),
        labelLarge = labelLarge.copy(fontFamily = PoppinsFamily),
        labelMedium = labelMedium.copy(fontFamily = PoppinsFamily),
        labelSmall = labelSmall.copy(fontFamily = PoppinsFamily)
    )
}

@Composable
fun AgeGoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AgeGoColors, typography = AgeGoTypography, content = content)
}
