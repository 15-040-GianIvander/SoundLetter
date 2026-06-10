package com.soundletter.app.data.repository

import com.soundletter.app.core.network.ApiConfig
import com.soundletter.app.domain.model.MusicTrack
import com.soundletter.app.domain.repository.MusicRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JamendoResponse(
    val results: List<JamendoTrack>
)

@Serializable
data class JamendoTrack(
    val name: String,
    @SerialName("artist_name") val artistName: String,
    val image: String,
    val audio: String
)

class MusicRepositoryImpl(
    private val httpClient: HttpClient
) : MusicRepository {

    override suspend fun searchSongs(mood: String): List<MusicTrack> {
        return try {
            // Jamendo API bekerja lebih baik dengan format tag1+tag2 untuk fuzzytags
            val formattedTags = mood.trim().replace(" ", "+")
            println("JAMENDO_LOG: Searching for tags: $formattedTags")

            val response: JamendoResponse = httpClient.get("https://api.jamendo.com/v3.0/tracks/") {
                parameter("client_id", ApiConfig.jamendoClientId)
                parameter("format", "json")
                parameter("limit", "10")
                // Menggunakan fuzzytags untuk pencarian berbasis Mood/Genre agar hasil lebih bervariasi
                parameter("fuzzytags", formattedTags)
                parameter("boost", "popularity_month")
            }.body()

            if (response.results.isEmpty()) throw Exception("No tracks found for tags")

            response.results.map { track ->
                MusicTrack(
                    title = track.name,
                    artist = track.artistName,
                    previewUrl = track.audio,
                    albumArtUrl = track.image
                )
            }
        } catch (e: Exception) {
            println("JAMENDO_LOG: Error: ${e.message}")
            // Fallback data tetap ada agar UI tidak pecah
            listOf(
                MusicTrack(
                    title = "Ambient Peace",
                    artist = "Jamendo Artist",
                    previewUrl = "https://prod-1.storage.jamendo.com/download/track/1885566/mp32/",
                    albumArtUrl = "https://picsum.photos/seed/music/300/300"
                )
            )
        }
    }
}
