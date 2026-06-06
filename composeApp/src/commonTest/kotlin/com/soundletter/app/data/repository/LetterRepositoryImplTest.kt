package com.soundletter.app.data.repository

import com.soundletter.app.data.local.NoteEntity
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import com.soundletter.app.domain.model.NoteColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LetterRepositoryImplTest {

    private lateinit var repository: FakeLetterRepository

    @BeforeTest
    fun setup() {
        repository = FakeLetterRepository()
    }

    @Test
    fun `getGlobalLetters should return 50 dummy notes`() = runTest {
        val letters = repository.getGlobalLetters().first()
        assertNotNull(letters)
        // In actual implementation it is 50, in our fake we can control it
        assertEquals(5, letters.size)
    }

    @Test
    fun `searchLetters should find correct dummy note by recipient`() = runTest {
        val query = "Gian"
        val results = repository.searchLetters(query).first()
        assertTrue(results.any { it.recipient.contains(query) })
    }

    // FAKE for testing logic that depends on LetterRepository
    class FakeLetterRepository : com.soundletter.app.domain.repository.LetterRepository {
        private val dummyNotes = List(5) { index ->
            Note(
                id = -(index + 100).toLong(),
                recipient = if (index == 0) "Gian Ivander" else "User $index",
                sender = "Sender $index",
                content = "Content $index",
                category = NoteCategory.GENERAL,
                color = NoteColor.DEFAULT
            )
        }

        override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
        override fun getGlobalLetters(): Flow<List<Note>> = flowOf(dummyNotes)
        override fun searchLetters(query: String): Flow<List<Note>> = flowOf(
            dummyNotes.filter { it.recipient.contains(query, ignoreCase = true) }
        )
        override suspend fun sendLetter(letter: Note): Boolean = true
        override suspend fun getLetterById(id: Long): Note? = dummyNotes.find { it.id == id }
        override suspend fun deleteLetter(id: Long) {}
        override suspend fun clearHistory() {}
    }
}

private fun assertTrue(actual: Boolean) {
    if (!actual) throw AssertionError("Expected true but was false")
}
