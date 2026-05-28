package com.soundletter.app.presentation.theme

import androidx.compose.ui.graphics.Color

object SoundLetterColors {
    val GlassBackground = Color(0x33FFFFFF)
    val GlassBorder = Color(0x66FFFFFF)
    
    // Dark mode gradient
    val DarkBackgroundGradient = listOf(
        Color(0xFF001E2C),
        Color(0xFF005F73),
        Color(0xFF00A9E0)
    )
    
    // Light mode gradient - softer blue gradient, not plain white
    val LightBackgroundGradient = listOf(
        Color(0xFFE3F2FD),
        Color(0xFFBBDEFB),
        Color(0xFF90CAF9)
    )

    fun getBackgroundGradient(isDarkMode: Boolean): List<Color> {
        return if (isDarkMode) DarkBackgroundGradient else LightBackgroundGradient
    }
}
