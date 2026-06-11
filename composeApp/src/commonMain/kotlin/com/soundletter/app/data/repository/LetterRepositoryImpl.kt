package com.soundletter.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.data.local.entity.toDomain
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import com.soundletter.app.domain.model.NoteColor
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class LetterRepositoryImpl(
    private val database: SoundLetterDatabase
) : LetterRepository {
    
    private val queries = database.noteQueries

     private val dummyGlobalLetters: List<Note> = listOf(
        Note(
            id = -101,
            recipient = "Gian Ivander",
            sender = "Pengagum Rahasia",
            content = "Semangat revisi skripsinya ya! Lagu akustik ini tenang banget, pas buat nemenin kamu fokus di perpus.",
            songTitle = "Acoustic Guitar Instrumental",
            songArtist = "Audionautix",
            songPreviewUrl = "https://www.jamendo.com/track/1885918/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/101/300/300",
            category = NoteCategory.STUDY,
            color = NoteColor.BLUE,
            createdAt = Clock.System.now().minus(1, DateTimeUnit.HOUR)
        ),
        Note(
            id = -102,
            recipient = "Atalie Salsabila",
            sender = "Teman Sekelas",
            content = "Woi! Inget gak lagu ini pas kita menang lomba? Energinya gila banget tiap dengerin ini!",
            songTitle = "Happy Rock Energetic",
            songArtist = "BenSound",
            songPreviewUrl = "https://www.jamendo.com/track/1885915/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/102/300/300",
            category = NoteCategory.PERSONAL,
            color = NoteColor.PINK,
            createdAt = Clock.System.now().minus(3, DateTimeUnit.HOUR)
        ),
        Note(
            id = -103,
            recipient = "Muhammad Dzaky",
            sender = "Si Paling Spotify",
            content = "Coba dengerin melodi piano ini. Vibesnya dapet banget buat kontemplasi sore di teras.",
            songTitle = "Deep Blue Ocean",
            songArtist = "Scott Holmes",
            songPreviewUrl = "https://www.jamendo.com/track/1885912/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/103/300/300",
            category = NoteCategory.GENERAL,
            color = NoteColor.PURPLE,
            createdAt = Clock.System.now().minus(5, DateTimeUnit.HOUR)
        ),
        Note(
            id = -104,
            recipient = "Pak Andi",
            sender = "Mahasiswa Bimbingan",
            content = "Izin titip lagu instrumen yang menenangkan untuk menemani Bapak memeriksa laporan praktikum.",
            songTitle = "Summer Dreams",
            songArtist = "Corporate Music",
            songPreviewUrl = "https://www.jamendo.com/track/1885909/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/104/300/300",
            category = NoteCategory.WORK,
            color = NoteColor.GREEN,
            createdAt = Clock.System.now().minus(8, DateTimeUnit.HOUR)
        ),
        Note(
            id = -105,
            recipient = "Putri Amelia",
            sender = "Sahabat Lama",
            content = "Lagi sedih ya? Dengerin ini deh, melodi cinematic-nya bikin tenang. Cheer up, Putri!",
            songTitle = "Cinematic Piano Solo",
            songArtist = "AShamaluevMusic",
            songPreviewUrl = "https://www.jamendo.com/track/1885906/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/105/300/300",
            category = NoteCategory.IDEAS,
            color = NoteColor.YELLOW,
            createdAt = Clock.System.now().minus(12, DateTimeUnit.HOUR)
        ),
        Note(
            id = -106,
            recipient = "Budi Santoso",
            sender = "Kating Teknik",
            content = "Selamat wisuda, Bang! Lagu pop ini energinya positif banget, pas buat ngerayain hari besar abang.",
            songTitle = "Energetic Pop Stars",
            songArtist = "Liborio Conti",
            songPreviewUrl = "https://www.jamendo.com/track/1885900/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/106/300/300",
            category = NoteCategory.TODO,
            color = NoteColor.ORANGE,
            createdAt = Clock.System.now().minus(24, DateTimeUnit.HOUR)
        ),
        Note(
            id = -107,
            recipient = "Sobat Ngoding",
            sender = "Wibu Idaman",
            content = "Buat nemenin debugging malem ini. Ambient music biar tetep chill meski error StackOverflow numpuk.",
            songTitle = "Ambient Gold",
            songArtist = "AudioCoffee",
            songPreviewUrl = "https://www.jamendo.com/track/1885903/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/107/300/300",
            category = NoteCategory.STUDY,
            color = NoteColor.DEFAULT,
            createdAt = Clock.System.now().minus(48, DateTimeUnit.HOUR)
        ),
        Note(
            id = -108,
            recipient = "Maba ITERA",
            sender = "Admin Lab",
            content = "Selamat datang di kampus hijau! Nikmati sunset di embung sambil dengerin melodi tenang ini.",
            songTitle = "Slow Motion Sunset",
            songArtist = "Fesliyan Studios",
            songPreviewUrl = "https://www.jamendo.com/track/1885897/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/108/300/300",
            category = NoteCategory.GENERAL,
            color = NoteColor.RED,
            createdAt = Clock.System.now().minus(72, DateTimeUnit.HOUR)
        ),
        Note(
            id = -109,
            recipient = "Anak Kost",
            sender = "Ibu Kantin",
            content = "Jangan lupa makan ya nak, tugas emang banyak tapi kesehatan nomor satu. Ini lagu penyemangat buatmu.",
            songTitle = "Wild West Adventure",
            songArtist = "Twin Musicom",
            songPreviewUrl = "https://www.jamendo.com/track/1885894/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/109/300/300",
            category = NoteCategory.WORK,
            color = NoteColor.PURPLE,
            createdAt = Clock.System.now().minus(96, DateTimeUnit.HOUR)
        ),
        Note(
            id = -110,
            recipient = "Stranger",
            sender = "Secret Admirer",
            content = "Have a great day! Semoga lagu Night Life ini bikin harimu jauh lebih berwarna dan penuh inspirasi.",
            songTitle = "Night Life City",
            songArtist = "Kevin MacLeod",
            songPreviewUrl = "https://www.jamendo.com/track/1885891/get/stream",
            songAlbumArtUrl = "https://picsum.photos/seed/110/300/300",
            category = NoteCategory.PERSONAL,
            color = NoteColor.PINK,
            createdAt = Clock.System.now().minus(120, DateTimeUnit.HOUR)
        )
    )

    override fun getGlobalLetters(): Flow<List<Note>> = flowOf(dummyGlobalLetters)

    override fun getLetters(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchLetters(query: String): Flow<List<Note>> {
        return getLetters().map { localNotes ->
            val filteredLocal = localNotes.filter { 
                it.recipient.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true) ||
                it.songTitle?.contains(query, ignoreCase = true) == true
            }
            val filteredDummy = dummyGlobalLetters.filter { 
                it.recipient.contains(query, ignoreCase = true) || 
                it.sender.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true) ||
                it.songTitle?.contains(query, ignoreCase = true) == true
            }
            (filteredLocal + filteredDummy).distinctBy { it.id }
        }
    }

    override suspend fun sendLetter(letter: Note): Boolean {
        queries.insertNote(
            recipient = letter.recipient,
            sender = letter.sender,
            content = letter.content,
            song_title = letter.songTitle,
            song_artist = letter.songArtist,
            song_preview_url = letter.songPreviewUrl,
            song_album_art_url = letter.songAlbumArtUrl,
            category = letter.category.name,
            color = letter.color.name,
            is_pinned = if (letter.isPinned) 1L else 0L,
            created_at = letter.createdAt.toEpochMilliseconds(),
            updated_at = letter.updatedAt.toEpochMilliseconds()
        )
        return true
    }

    override suspend fun getLetterById(id: Long): Note? {
        if (id < 0) return dummyGlobalLetters.find { it.id == id }
        return queries.getNoteById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun deleteLetter(id: Long) = queries.deleteNoteById(id)

    override suspend fun clearHistory() = queries.deleteAllNotes()
}
