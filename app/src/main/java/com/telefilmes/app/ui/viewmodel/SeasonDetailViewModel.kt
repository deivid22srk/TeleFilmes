package com.telefilmes.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.telefilmes.app.data.model.Episode
import com.telefilmes.app.data.model.SeasonWithEpisodes
import com.telefilmes.app.data.repository.MediaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SeasonDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaRepository
) : ViewModel() {
    
    private val seasonId: Long = savedStateHandle.get<String>("seasonId")?.toLongOrNull() ?: 0L
    
    val seasonWithEpisodes: StateFlow<SeasonWithEpisodes?> = repository.getSeasonWithEpisodes(seasonId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    fun addEpisode(
        episodeNumber: Int,
        title: String,
        telegramFileId: String,
        telegramMessageId: Long,
        telegramChatId: Long,
        duration: Int = 0,
        fileSize: Long = 0
    ) {
        viewModelScope.launch {
            repository.insertEpisode(
                Episode(
                    seasonId = seasonId,
                    episodeNumber = episodeNumber,
                    title = title,
                    telegramFileId = telegramFileId,
                    telegramMessageId = telegramMessageId,
                    telegramChatId = telegramChatId,
                    duration = duration,
                    fileSize = fileSize
                )
            )
        }
    }
    
    fun deleteEpisode(episode: Episode) {
        viewModelScope.launch {
            repository.deleteEpisode(episode)
        }
    }
}

class SeasonDetailViewModelFactory(
    private val repository: MediaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
        if (modelClass.isAssignableFrom(SeasonDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return SeasonDetailViewModel(savedStateHandle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
