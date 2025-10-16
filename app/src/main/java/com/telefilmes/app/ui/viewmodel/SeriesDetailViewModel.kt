package com.telefilmes.app.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.telefilmes.app.data.model.Season
import com.telefilmes.app.data.model.SeriesWithSeasons
import com.telefilmes.app.data.repository.MediaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SeriesDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MediaRepository
) : ViewModel() {
    
    private val seriesId: Long = savedStateHandle.get<String>("seriesId")?.toLongOrNull() ?: 0L
    
    val seriesWithSeasons: StateFlow<SeriesWithSeasons?> = repository.getSeriesWithSeasons(seriesId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    fun createSeason(name: String, seasonNumber: Int) {
        viewModelScope.launch {
            repository.insertSeason(
                Season(
                    seriesId = seriesId,
                    name = name,
                    seasonNumber = seasonNumber
                )
            )
        }
    }
    
    fun deleteSeason(season: Season) {
        viewModelScope.launch {
            repository.deleteSeason(season)
        }
    }
}

class SeriesDetailViewModelFactory(
    private val repository: MediaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
        if (modelClass.isAssignableFrom(SeriesDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return SeriesDetailViewModel(savedStateHandle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
