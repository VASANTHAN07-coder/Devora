package com.example.zecruit.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryNeonBlue,
    secondary = SecondaryBlue,
    tertiary = Success,
    background = Background,
    surface = Surface,
    onPrimary = Background,
    onSecondary = TextPrimary,
    onTertiary = Background,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Danger
)

// The user specified a very specific dark-leaning theme, 
// so we'll use a similar scheme for light mode or just stick to the requested colors.
private val LightColorScheme = lightColorScheme(
    primary = PrimaryNeonBlue,
    secondary = SecondaryBlue,
    tertiary = Success,
    background = Background,
    surface = Surface,
    onPrimary = Background,
    onSecondary = TextPrimary,
    onTertiary = Background,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Danger
)

@Composable
fun DeviceManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}