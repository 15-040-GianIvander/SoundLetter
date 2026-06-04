package com.soundletter.app.presentation.screens.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeScreen(
    onNavigateBack: () -> Unit,
    onSuccess: (Boolean) -> Unit = {},
    viewModel: ComposeViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val contentColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
    
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = contentColor,
        unfocusedBorderColor = contentColor.copy(alpha = 0.5f),
        focusedLabelColor = contentColor,
        unfocusedLabelColor = contentColor.copy(alpha = 0.7f),
        focusedTextColor = if (isDarkMode) Color.White else Color.Black,
        unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
    )

    // Handle UI Events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ComposeUiEvent.ShowOfflineSnackbar -> {
                    snackbarHostState.showSnackbar("Koneksi terputus. Pesan disimpan secara lokal.")
                }
                is ComposeUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    LaunchedEffect(state.sendStatus) {
        when (val status = state.sendStatus) {
            is UiState.Success -> {
                val isSynced = status.data
                viewModel.resetStatus()
                onSuccess(isSynced)
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar("Gagal: ${status.message}")
                viewModel.resetStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Compose Letter", color = contentColor) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = contentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Bagian scrollable berisi input field
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.recipient,
                    onValueChange = { viewModel.onRecipientChange(it) },
                    label = { Text("Untuk") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = state.sender,
                    onValueChange = { viewModel.onSenderChange(it) },
                    label = { Text("Dari (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = state.message,
                    onValueChange = { viewModel.onMessageChange(it) },
                    label = { Text("Pesan") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    colors = textFieldColors
                )

                Button(
                    onClick = { viewModel.recommendSongs() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = !state.isAiLoading
                ) {
                    if (state.isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rekomendasikan Lagu")
                    }
                }

                // FIX: Gunakan tinggi tetap untuk LazyRow agar tidak hilang dalam Scroll
                if (state.suggestions.isNotEmpty()) {
                    Text("Pilih Lagu:", style = MaterialTheme.typography.labelMedium, color = contentColor)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.suggestions) { song ->
                            SongSuggestionCard(
                                song = song, 
                                isSelected = state.selectedSong == song,
                                isDarkMode = isDarkMode,
                                onClick = { viewModel.onSongSelect(song) }
                            )
                        }
                    }
                }
            }

            // Tombol Kirim permanen di bawah
            Button(
                onClick = { viewModel.sendSoundLetter() },
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                enabled = state.sendStatus !is UiState.Loading
            ) {
                Text("Kirim Surat Musik", fontWeight = FontWeight.Bold)
            }
        }
        
        if (state.sendStatus is UiState.Loading) {
            LoadingView()
        }
    }
}

@Composable
fun SongSuggestionCard(song: SongSuggestion, isSelected: Boolean, isDarkMode: Boolean, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.width(160.dp).height(70.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                             else if (isDarkMode) Color.White.copy(alpha = 0.1f)
                             else Color.White.copy(alpha = 0.8f)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = song.title, 
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold, 
                maxLines = 1,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                        else if (isDarkMode) Color.White else Color.Black
            )
            Text(
                text = song.artist, 
                style = MaterialTheme.typography.labelSmall, 
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                        else if (isDarkMode) Color.White.copy(alpha = 0.6f) 
                        else Color.DarkGray
            )
        }
    }
}
