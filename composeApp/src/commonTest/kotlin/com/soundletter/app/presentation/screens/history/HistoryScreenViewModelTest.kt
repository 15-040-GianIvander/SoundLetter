package com.soundletter.app.presentation.screens.history

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeHistoryRepository : LetterRepository {
    private val flow = MutableSharedFlow<List<Note>>()
    var shouldFail = false

    override fun getLetters(): Flow<List<Note>> {
        if (shouldFail) throw Exception("Database Connection Error")
        return flow
    }
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {}

    suspend fun emit(data: List<Note>) = flow.emit(data)
}

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeHistoryRepository
    private lateinit var viewModel: HistoryScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHistoryRepository()
        viewModel = HistoryScreenViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadHistory success should emit Success state`() = runTest {
        val mockData = listOf(Note(id = 1, recipient = "Test", content = "Msg"))
        viewModel.historyState.test {
            assertIs<UiState.Loading>(awaitItem())
            repository.emit(mockData)
            val state = awaitItem()
            assertIs<UiState.Success<List<Note>>>(state)
            assertEquals(1, state.data.size)
        }
    }

    @Test
    fun `loadHistory failure should emit Error state`() = runTest {
        repository.shouldFail = true
        // Re-init to trigger failure on init
        viewModel = HistoryScreenViewModel(repository)
        
        viewModel.historyState.test {
            assertIs<UiState.Loading>(awaitItem())
            val state = awaitItem()
            assertIs<UiState.Error>(state)
            assertEquals("Database Connection Error", state.message)
        }
    }
}
