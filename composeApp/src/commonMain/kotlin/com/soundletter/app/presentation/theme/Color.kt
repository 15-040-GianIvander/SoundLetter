package com.soundletter.app.presentation.theme

import androidx.compose.ui.graphics.Color

object SoundLetterColors {
    // Brand Accent Color (Sky Blue) - HANYA untuk aksen
    val SkyBlue = Color(0xFF00BFFF)
    val SkyBlueDark = Color(0xFF0099CC)
    
    // Core Backgrounds (Aturan Ketat: Putih Murni & Hitam Pekat)
    val PureWhite = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF000000)
    val DarkSurface = Color(0xFF121212)
    
    // Text Colors
    val TextPrimaryLight = Color(0xFF000000)
    val TextSecondaryLight = Color(0xFF424242)
    val TextPrimaryDark = Color(0xFFFFFFFF)
    val TextSecondaryDark = Color(0xFFBDBDBD)
    
    // UI Elements (GlassCard) - Dibuat sangat tipis agar background tetap dominan
    val GlassBackground = Color(0x0D00BFFF) 
    val GlassBorder = Color(0x1A00BFFF)

    // Helper untuk kompatibilitas layar lama (Sekarang mengembalikan warna solid)
    fun getBackgroundGradient(isDarkMode: Boolean): List<Color> {
        return if (isDarkMode) listOf(PureBlack, PureBlack) else listOf(PureWhite, PureWhite)
    }
}
