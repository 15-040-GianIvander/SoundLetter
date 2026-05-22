package com.soundletter.app.presentation.screens.detail

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeDetailRepository : LetterRepository {
    var shouldFail = false
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    override suspend fun getLetterById(id: Long): Note? {
        if (shouldFail) throw Exception("Network Error")
        return if (id == 1L) Note(id = 1L, recipient = "Test", content = "Content") else null
    }
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class DetailMessageScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeDetailRepository
    private lateinit var viewModel: DetailMessageScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeDetailRepository()
        viewModel = DetailMessageScreenViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test // Test 16: Success Load
    fun `loadMessage with valid ID should emit Success`() = runTest {
        viewModel.state.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.loadMessage("1")
            assertIs<UiState.Loading>(awaitItem())
            val success = awaitItem()
            assertIs<UiState.Success<Note>>(success)
            assertEquals(1L, success.data.id)
        }
    }

    @Test // Test 17: Not Found Load
    fun `loadMessage with invalid ID should emit Error`() = runTest {
        viewModel.state.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.loadMessage("99")
            assertIs<UiState.Loading>(awaitItem())
            val error = awaitItem()
            assertIs<UiState.Error>(error)
            assertEquals("Letter not found", error.message)
        }
    }

    @Test // Test 18: Exception Load
    fun `loadMessage with exception should emit Error with message`() = runTest {
        repository.shouldFail = true
        viewModel.state.test {
            assertIs<UiState.Idle>(awaitItem())
            viewModel.loadMessage("1")
            assertIs<UiState.Loading>(awaitItem())
            val error = awaitItem()
            assertIs<UiState.Error>(error)
            assertEquals("Network Error", error.message)
        }
    }
}
