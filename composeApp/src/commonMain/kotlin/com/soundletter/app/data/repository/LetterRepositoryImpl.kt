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

    // 1. GENERATOR 50 DATA DUMMY GLOBAL (Seolah-olah dari user lain)
    private val dummyGlobalLetters: List<Note> = List(50) { index ->
        val variation = index % 10 // Kita buat 10 variasi konten agar lebih beragam
        Note(
            id = -(index + 100).toLong(),
            recipient = when (variation) {
                0 -> "Gian Ivander"
                1 -> "Atalie Salsabila"
                2 -> "Muhammad Dzaky"
                3 -> "Bapak Dosen"
                4 -> "Putri"
                5 -> "Budi"
                6 -> "Sobat Ngoding"
                7 -> "Maba ITERA"
                8 -> "Admin Lab"
                else -> "Stranger"
            },
            sender = when (variation) {
                0 -> "Pengagum Rahasia"
                1 -> "Teman Sekelas"
                2 -> "Si Paling Spotify"
                3 -> "Mahasiswa Rajin"
                4 -> "Anonymous"
                5 -> "Kating"
                6 -> "Wibu Idaman"
                7 -> "Anak Kost"
                8 -> "SoundLetter User"
                else -> "Secret Admirer"
            },
            content = when (variation) {
                0 -> "Lagu ini vibesnya lu banget. Semangat ngerjain tugasnya!"
                1 -> "Dengerin ini pas lagi ujan di Lampung, beuh mantap."
                2 -> "Makasih ya tadi udah dibantu di perpus. Ini lagu buat nemenin lu balik."
                3 -> "Izin titip lagu buat Bapak/Ibu. Sehat selalu."
                4 -> "Tadi liat lu di kantin, kelihatannya lagi capek. Cheer up!"
                5 -> "Lagu ini underrated banget, coba dengerin deh."
                6 -> "Gua tau lu suka genre ginian. Hope you like it!"
                7 -> "Jangan begadang mulu, dengerin lagu ini biar tenang tidurnya."
                8 -> "Tugas emang banyak, tapi mental health nomor satu. Rehat sejenak."
                else -> "Have a great day, whoever you are!"
            },
            songTitle = when (variation) {
                0 -> "Evaluasi"
                1 -> "Bertaut"
                2 -> "Dan..."
                3 -> "Secukupnya"
                4 -> "Rumah ke Rumah"
                5 -> "Sorai"
                6 -> "Gajah"
                7 -> "Tak Kan Ada Cinta yang Lain"
                8 -> "Rehat"
                else -> "Hati-Hati di Jalan"
            },
            songArtist = when (variation) {
                0 -> "Hindia"
                1 -> "Nadin Amizah"
                2 -> "Sheila on 7"
                3 -> "Hindia"
                4 -> "Hindia"
                5 -> "Nadin Amizah"
                6 -> "Tulus"
                7 -> "Dewa 19"
                8 -> "Kunto Aji"
                else -> "Tulus"
            },
            // Menggunakan Picsum untuk simulasi Album Art agar UI terlihat nyata dan estetik
            songAlbumArtUrl = "https://picsum.photos/seed/${index + 50}/300/300",
            category = NoteCategory.entries[index % NoteCategory.entries.size],
            color = NoteColor.entries[index % NoteColor.entries.size],
            createdAt = Clock.System.now().minus(index * 2, DateTimeUnit.HOUR),
            updatedAt = Clock.System.now()
        )
    }

    // 2. Return data dummy sebagai Global Feed
    override fun getGlobalLetters(): Flow<List<Note>> {
        return flowOf(dummyGlobalLetters)
    }

    // 3. Ambil riwayat lokal dari SQLDelight
    override fun getLetters(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    /**
     * Hybrid Search: Mencari dari Database Lokal DAN 50 Data Dummy Global
     */
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
                it.songTitle?.contains(query, ignoreCase = true) == true ||
                it.songArtist?.contains(query, ignoreCase = true) == true
            }
            
            // Gabungkan hasil dan hilangkan duplikasi berdasarkan ID
            (filteredLocal + filteredDummy).distinctBy { it.id }
        }
    }

    override suspend fun sendLetter(letter: Note): Boolean {
        // Simpan ke database lokal SQLDelight
        queries.insertNote(
            recipient = letter.recipient,
            sender = letter.sender,
            content = letter.content,
            song_title = letter.songTitle,
            song_artist = letter.songArtist,
            song_preview_url = letter.songPreviewUrl ?: "",
            song_album_art_url = letter.songAlbumArtUrl ?: "",
            category = letter.category.name,
            color = letter.color.name,
            is_pinned = if (letter.isPinned) 1L else 0L,
            created_at = letter.createdAt.toEpochMilliseconds(),
            updated_at = letter.updatedAt.toEpochMilliseconds()
        )
        return true
    }

    override suspend fun getLetterById(id: Long): Note? {
        // Cek di dummy data dulu (ID negatif)
        if (id < 0) return dummyGlobalLetters.find { it.id == id }
        
        // Baru cek di database lokal
        return queries.getNoteById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun deleteLetter(id: Long) {
        queries.deleteNoteById(id)
    }

    override suspend fun clearHistory() {
        // Hanya menghapus NoteEntity di database lokal, data dummy global tetap aman
        queries.deleteAllNotes()
    }
}
