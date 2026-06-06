package com.soundletter.app.core.network

/**
 * Unified Configuration for SoundLetter.
 * NOTE: Kita gunakan hardcoded sementara agar build tidak FAILED saat generate BuildKonfig.
 * Setelah build sukses, Anda bisa mengaktifkan kembali pemanggilan BuildKonfig.
 */
object ApiConfig {
    // Masukkan Gemini Key Anda di sini jika ingin langsung jalan
    val geminiApiKey: String = "" 

    // Jamendo ID sudah gua set sesuai permintaan lu
    val jamendoClientId: String = "cbb32072"
}
