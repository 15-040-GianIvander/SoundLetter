package com.soundletter.app.core.network

/**
 * Unified Configuration for SoundLetter.
 * Kita panggil BuildKonfig menggunakan Full Path tanpa import agar compiler tidak bingung 
 * saat class sedang di-generate oleh plugin BuildKonfig.
 */
object ApiConfig {
    // Memanggil langsung dari package yang didefinisikan di build.gradle.kts
    val geminiApiKey: String 
        get() = com.soundletter.app.BuildKonfig.GEMINI_API_KEY

    val jamendoClientId: String 
        get() = com.soundletter.app.BuildKonfig.JAMENDO_CLIENT_ID
}
