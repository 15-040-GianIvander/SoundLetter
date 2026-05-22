package com.soundletter.app.presentation.screens.home

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeHomeRepository : LetterRepository {
    private val flow = MutableSharedFlow<List<Note>>()
    var shouldFail = false

    override fun getLetters(): Flow<List<Note>> = flow {
        if (shouldFail) throw Exception("Network Error")
        emitAll(flow)
    }

    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun sendLetter(letter: Note) {}
    override suspend fun deleteLetter(id: Long) {}

    suspend fun emit(data: List<Note>) = flow.emit(data)
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeHomeRepository
    private lateinit var viewModel: HomeScreenViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeHomeRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLetters success should emit Success state`() = runTest {
        viewModel = HomeScreenViewModel(repository)
        val mockData = listOf(Note(id = 1, recipient = "Test", content = "Msg"))
        
        viewModel.uiState.test {
            val initialState = awaitItem()
            if (initialState is UiState.Loading) {
                repository.emit(mockData)
                assertIs<UiState.Success<List<Note>>>(awaitItem())
            } else {
                assertIs<UiState.Success<List<Note>>>(initialState)
            }
        }
    }

    @Test
    fun `loadLetters failure should emit Error state`() = runTest {
        repository.shouldFail = true
        viewModel = HomeScreenViewModel(repository)
        
        viewModel.uiState.test {
            val state = awaitItem()
            if (state is UiState.Loading) {
                val errorState = awaitItem()
                assertIs<UiState.Error>(errorState)
                assertEquals("Network Error", errorState.message)
            } else {
                assertIs<UiState.Error>(state)
                assertEquals("Network Error", (state as UiState.Error).message)
            }
        }
    }
}
