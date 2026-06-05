package com.soundletter.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)

class GeminiService(private val httpClient: HttpClient) {
    // Gunakan ApiConfig yang membungkus BuildKonfig
    private val apiKey = ApiConfig.geminiApiKey.trim().replace("\"", "").replace("'", "")
    
    // MENGGUNAKAN MODEL gemini-2.5-flash-lite SESUAI INSTRUKSI
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent"

    suspend fun getSongRecommendations(message: String): String {
        val prompt = "Based on this message: '$message', recommend 1 popular song. Format: Title - Artist. ONLY give the title and artist, no extra words."
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            )
        )

        return try {
            val response: GeminiResponse = httpClient.post(baseUrl) {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (result.isNullOrBlank()) "Hati-Hati di Jalan - Tulus" else result.trim()
        } catch (e: Exception) {
            println("AUDIO_LOG: Gemini 2.5 Flash Lite failed: ${e.message}")
            "Tak Kan Ada Cinta yang Lain - Dewa 19"
        }
    }
}
