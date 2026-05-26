package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LetterRepository {
    /**
     * Riwayat Lokal: Data dari database lokal (SQLDelight)
     */
    fun getLetters(): Flow<List<Note>>
    
    /**
     * Feed Global: Data dari Supabase (Cloud)
     */
    fun getGlobalLetters(): Flow<List<Note>>

    suspend fun getLetterById(id: Long): Note?
    
    /**
     * Mengirim pesan ke Supabase (jika online) dan menyimpannya di lokal
     */
    suspend fun sendLetter(letter: Note)

    suspend fun deleteLetter(id: Long)
}
