package com.telefilmes.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.telefilmes.app.data.model.Season
import com.telefilmes.app.telegram.TelegramMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMessagesScreen(
    chatId: Long,
    chatTitle: String,
    messages: List<TelegramMessage>,
    availableSeasons: List<Season>,
    onBackClick: () -> Unit,
    onSaveVideo: (TelegramMessage, Long) -> Unit,
    onDownloadVideo: (String) -> Unit
) {
    var showSeasonPicker by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<TelegramMessage?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = chatTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (messages.isEmpty()) {
            EmptyMessagesContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages.filter { it.hasVideo }, key = { it.id }) { message ->
                    VideoMessageCard(
                        message = message,
                        onSaveClick = {
                            selectedMessage = message
                            showSeasonPicker = true
                        },
                        onDownloadClick = {
                            message.videoFileId?.let { onDownloadVideo(it) }
                        }
                    )
                }
            }
        }
    }
    
    if (showSeasonPicker && selectedMessage != null) {
        SeasonPickerDialog(
            seasons = availableSeasons,
            onDismiss = { 
                showSeasonPicker = false
                selectedMessage = null
            },
            onSeasonSelected = { season ->
                onSaveVideo(selectedMessage!!, season.id)
                showSeasonPicker = false
                selectedMessage = null
            }
        )
    }
}

@Composable
private fun EmptyMessagesContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhum vídeo encontrado",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Este chat não possui vídeos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun VideoMessageCard(
    message: TelegramMessage,
    onSaveClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = message.text.ifEmpty { "Vídeo" },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val minutes = message.videoDuration / 60
                    val seconds = message.videoDuration % 60
                    val sizeMB = message.videoSize / (1024 * 1024)
                    Text(
                        text = String.format("%d:%02d • %d MB", minutes, seconds, sizeMB),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Baixar")
                }
                
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salvar")
                }
            }
        }
    }
}

@Composable
private fun SeasonPickerDialog(
    seasons: List<Season>,
    onDismiss: () -> Unit,
    onSeasonSelected: (Season) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecione a Temporada") },
        text = {
            if (seasons.isEmpty()) {
                Text("Nenhuma temporada disponível. Crie uma temporada primeiro.")
            } else {
                LazyColumn {
                    items(seasons, key = { it.id }) { season ->
                        Card(
                            onClick = { onSeasonSelected(season) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = season.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Temporada ${season.seasonNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
