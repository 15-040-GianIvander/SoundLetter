package com.soundletter.app.data.local.entity

import com.soundletter.app.data.local.NoteEntity
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import com.soundletter.app.domain.model.NoteColor
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteMapperTest {

    @Test
    fun testToDomainMapping() {
        val now = 1715950000000L
        val entity = NoteEntity(
            id = 1L,
            recipient = "Gian",
            sender = "Anon",
            content = "Unit testing content",
            song_title = "Creative Commons Melody",
            song_artist = "Jamendo Artist",
            song_preview_url = "https://test.com/audio.mp3",
            song_album_art_url = "https://test.com/image.jpg",
            category = "GENERAL",
            color = "DEFAULT",
            is_pinned = 1L,
            created_at = now,
            updated_at = now
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.recipient, domain.recipient)
        assertEquals("Creative Commons Melody", domain.songTitle)
        assertEquals(NoteCategory.GENERAL, domain.category)
        assertEquals(true, domain.isPinned)
    }

    @Test
    fun testToEntityValuesMapping() {
        val now = Instant.fromEpochMilliseconds(1715950000000L)
        val note = Note(
            id = 2L,
            recipient = "Dzaky",
            sender = "Atalie",
            content = "Testing domain to entity",
            songTitle = "Indie Song",
            songArtist = "Artist A",
            songPreviewUrl = "https://test.com/preview.mp3",
            songAlbumArtUrl = "https://test.com/art.jpg",
            category = NoteCategory.PERSONAL,
            color = NoteColor.BLUE,
            isPinned = false,
            createdAt = now,
            updatedAt = now
        )

        val entity = note.toEntityValues()

        assertEquals(note.recipient, entity.recipient)
        assertEquals("PERSONAL", entity.category)
        assertEquals("BLUE", entity.color)
        assertEquals(0L, entity.is_pinned)
        assertEquals(now.toEpochMilliseconds(), entity.created_at)
    }
}
