package com.soundletter.app.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.presentation.components.GlassCard
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import com.soundletter.app.presentation.theme.SoundLetterColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToCompose: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeScreenViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCompose,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Compose", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(SoundLetterColors.getBackgroundGradient(isDarkMode)))
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "SoundLetter",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                )

                Text(
                    text = "Global Feed",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isDarkMode) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline
                    )
                )

                when (val state = uiState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.data) { letter ->
                                MessageCard(
                                    message = letter, 
                                    isDarkMode = isDarkMode,
                                    onClick = { onNavigateToDetail(letter.id.toString()) }
                                )
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MessageCard(message: Note, isDarkMode: Boolean, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "To: ${message.recipient}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "From: ${message.sender}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isDarkMode) Color.White.copy(alpha = 0.9f) else Color.Black
                ),
                maxLines = 3
            )
            message.songTitle?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDarkMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        }
    }
}
