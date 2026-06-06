package com.soundletter.app.data.repository

import com.soundletter.app.domain.model.MusicTrack
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MusicRepositoryImplTest {

    private fun createMockClient(handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): HttpClient {
        return HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) {
                json(Json { 
                    ignoreUnknownKeys = true 
                    isLenient = true
                })
            }
        }
    }

    @Test
    fun `searchSongs returns tracks when Jamendo API is successful`() = runTest {
        val client = createMockClient { request ->
            respond(
                content = ByteReadChannel("""{"results": [{"name": "Indie Song", "artist_name": "Artist 1", "audio": "url1", "image": "img1"}]}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val repo = MusicRepositoryImpl(client)
        val result = repo.searchSongs("test mood")
        
        assertEquals(1, result.size)
        assertEquals("Indie Song", result[0].title)
    }

    @Test
    fun `searchSongs returns fallback data when Jamendo API results are empty`() = runTest {
        val client = createMockClient { _ ->
            respond(
                content = ByteReadChannel("""{"results": []}"""),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val repo = MusicRepositoryImpl(client)
        val result = repo.searchSongs("unknown")
        
        assertEquals(1, result.size)
        assertTrue(result[0].artist.contains("Fallback"), "Harusnya return fallback")
    }

    @Test
    fun `searchSongs returns fallback data on API error 500`() = runTest {
        val client = createMockClient { _ ->
            respond(
                content = ByteReadChannel("Error"),
                status = HttpStatusCode.InternalServerError
            )
        }
        val repo = MusicRepositoryImpl(client)
        val result = repo.searchSongs("any")
        
        assertEquals(1, result.size)
        assertEquals("Jamendo Artist (Fallback)", result[0].artist)
    }

    @Test
    fun `searchSongs returns fallback data on network failure`() = runTest {
        val client = createMockClient { _ ->
            throw Exception("No Internet Connection")
        }
        val repo = MusicRepositoryImpl(client)
        val result = repo.searchSongs("offline")
        
        assertEquals(1, result.size)
        assertEquals("Creative Commons Melody", result[0].title)
    }
}
