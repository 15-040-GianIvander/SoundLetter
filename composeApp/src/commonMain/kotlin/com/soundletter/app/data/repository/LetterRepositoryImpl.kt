package com.soundletter.app.data.repository

import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LetterRepositoryImpl(
    private val database: SoundLetterDatabase
) : LetterRepository {
    override fun getLetters(): Flow<List<Note>> {
        return flowOf(emptyList())
    }

    override suspend fun getLetterById(id: Long): Note? {
        return null
    }

    override suspend fun sendLetter(letter: Note) {
        // Implementation for sending letter
    }

    override suspend fun deleteLetter(id: Long) {
        // Implementation for deleting letter
    }
}
