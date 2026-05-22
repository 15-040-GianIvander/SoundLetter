package com.soundletter.app.presentation.screens.compose

import app.cash.turbine.test
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import com.soundletter.app.domain.repository.MusicRepository
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
import kotlin.test.assertTrue

class FakeLetterRepo : LetterRepository {
    var shouldFail = false
    var wasSendCalled = false
    override fun getLetters(): Flow<List<Note>> = flowOf(emptyList())
    override suspend fun getLetterById(id: Long): Note? = null
    override suspend fun deleteLetter(id: Long) {}
    override suspend fun sendLetter(letter: Note) {
        wasSendCalled = true
        if (shouldFail) throw Exception("Network Error")
    }
}

class FakeMusicRepo : MusicRepository {
    override suspend fun searchSongs(query: String): List<String> = emptyList()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ComposeViewModelTest {
    private lateinit var viewModel: ComposeViewModel
    private lateinit var repository: FakeLetterRepo

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeLetterRepo()
        viewModel = ComposeViewModel(repository, FakeMusicRepo())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendSoundLetter should reach Success state when repository succeeds`() = runTest {
        viewModel.onToChange("Dzakky")
        viewModel.onMessageChange("Hello")
        
        viewModel.sendSoundLetter("Dzakky", "Anon", "Hello", null)
        
        assertIs<UiState.Success<Unit>>(viewModel.state.value.sendStatus)
        assertTrue(repository.wasSendCalled)
    }

    @Test
    fun `sendSoundLetter should return error state when repository fails`() = runTest {
        repository.shouldFail = true
        viewModel.sendSoundLetter("To", "From", "Msg", null)
        
        val status = viewModel.state.value.sendStatus
        assertIs<UiState.Error>(status)
        assertEquals("Network Error", status.message)
    }

    @Test
    fun `sendSoundLetter should return error when recipient is blank`() = runTest {
        viewModel.sendSoundLetter("", "From", "Msg", null)
        
        val status = viewModel.state.value.sendStatus
        assertIs<UiState.Error>(status)
        assertEquals("Recipient and message cannot be empty", status.message)
        assertTrue(!repository.wasSendCalled)
    }

    @Test
    fun `onMessageChange should update state correctly`() = runTest {
        val message = "Hello World"
        viewModel.onMessageChange(message)
        assertEquals(message, viewModel.state.value.message)
    }

    @Test
    fun `recommendSongs should update suggestions`() = runTest {
        viewModel.recommendSongs()
        assertTrue(viewModel.state.value.suggestions.isNotEmpty())
    }
}
