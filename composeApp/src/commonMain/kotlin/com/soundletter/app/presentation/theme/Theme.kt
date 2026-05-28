package com.soundletter.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Sky Blue Branding
private val SkyBluePrimary = Color(0xFF00A9E0)
private val SkyBlueSecondary = Color(0xFF007A99)
private val SkyBlueTertiary = Color(0xFF005F73)

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC1E8FF),
    onPrimaryContainer = Color(0xFF001E2C),
    secondary = SkyBlueSecondary,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF76D1FF),
    onPrimary = Color(0xFF003549),
    primaryContainer = Color(0xFF004C69),
    onPrimaryContainer = Color(0xFFC1E8FF),
    secondary = Color(0xFFB0CCDE),
    onSecondary = Color(0xFF193444),
    background = Color(0xFF191C1E),
    surface = Color(0xFF191C1E),
    onBackground = Color(0xFFE1E2E5),
    onSurface = Color(0xFFE1E2E5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun SoundLetterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
