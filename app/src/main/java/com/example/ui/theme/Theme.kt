package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDarkBackground,
    primaryContainer = NavySurfaceVariant,
    onPrimaryContainer = GoldLight,
    secondary = BlueBiometric,
    onSecondary = NavyDarkBackground,
    secondaryContainer = NavySurfaceCard,
    onSecondaryContainer = TextPrimary,
    tertiary = RubyEmergency,
    onTertiary = TextPrimary,
    tertiaryContainer = RubySubtle,
    onTertiaryContainer = RubyEmergency,
    background = NavyDarkBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = RubyEmergency,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // PulseGuard AI is tailored for dark biometric monitoring aesthetic
    dynamicColor: Boolean = false, // Keep consistent medical deep navy branding
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
