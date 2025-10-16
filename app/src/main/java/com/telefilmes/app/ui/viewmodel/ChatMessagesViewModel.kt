package com.telefilmes.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.telefilmes.app.data.model.Episode
import com.telefilmes.app.data.model.Season
import com.telefilmes.app.data.repository.MediaRepository
import com.telefilmes.app.telegram.TelegramClient
import com.telefilmes.app.telegram.TelegramMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatMessagesViewModel(
    savedStateHandle: SavedStateHandle,
    private val telegramClient: TelegramClient,
    private val mediaRepository: MediaRepository
) : ViewModel() {
    
    private val chatId: Long = savedStateHandle.get<String>("chatId")?.toLongOrNull() ?: 0L
    
    private val _messages = MutableStateFlow<List<TelegramMessage>>(emptyList())
    val messages: StateFlow<List<TelegramMessage>> = _messages.asStateFlow()
    
    private val _allSeasons = MutableStateFlow<List<Season>>(emptyList())
    val allSeasons: StateFlow<List<Season>> = _allSeasons.asStateFlow()
    
    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()
    
    init {
        loadMessages()
        loadAllSeasons()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            try {
                val msgs = telegramClient.getChatMessages(chatId, 100)
                _messages.value = msgs
            } catch (e: Exception) {
                _messages.value = emptyList()
            }
        }
    }
    
    private fun loadAllSeasons() {
        viewModelScope.launch {
            mediaRepository.getAllSeries().collect { seriesList ->
                val seasons = mutableListOf<Season>()
                seriesList.forEach { series ->
                    mediaRepository.getSeasonsBySeries(series.id).collect { seasonList ->
                        seasons.addAll(seasonList)
                    }
                }
                _allSeasons.value = seasons
            }
        }
    }
    
    fun saveVideoToSeason(message: TelegramMessage, seasonId: Long) {
        viewModelScope.launch {
            try {
                _saveStatus.value = SaveStatus.Saving
                
                val existingEpisodes = mediaRepository.getEpisodesBySeason(seasonId)
                var nextEpisodeNumber = 1
                existingEpisodes.collect { episodes ->
                    nextEpisodeNumber = (episodes.maxOfOrNull { it.episodeNumber } ?: 0) + 1
                }
                
                val episode = Episode(
                    seasonId = seasonId,
                    episodeNumber = nextEpisodeNumber,
                    title = message.text.ifEmpty { "Episódio $nextEpisodeNumber" },
                    telegramFileId = message.videoFileId ?: "",
                    telegramMessageId = message.id,
                    telegramChatId = message.chatId,
                    duration = message.videoDuration,
                    fileSize = message.videoSize
                )
                
                mediaRepository.insertEpisode(episode)
                _saveStatus.value = SaveStatus.Success("Vídeo salvo com sucesso!")
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Erro ao salvar vídeo")
            }
        }
    }
    
    fun downloadVideo(fileId: String) {
        viewModelScope.launch {
            try {
                telegramClient.downloadFile(fileId.toInt())
            } catch (e: Exception) {
            }
        }
    }
    
    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Saving : SaveStatus()
    data class Success(val message: String) : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}

class ChatMessagesViewModelFactory(
    private val telegramClient: TelegramClient,
    private val mediaRepository: MediaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
        if (modelClass.isAssignableFrom(ChatMessagesViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return ChatMessagesViewModel(savedStateHandle, telegramClient, mediaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
