package com.telefilmes.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.telefilmes.app.data.model.Series
import com.telefilmes.app.data.repository.MediaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MediaRepository) : ViewModel() {
    
    val seriesList: StateFlow<List<Series>> = repository.getAllSeries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun createSeries(name: String, description: String? = null) {
        viewModelScope.launch {
            repository.insertSeries(
                Series(
                    name = name,
                    description = description
                )
            )
        }
    }
    
    fun deleteSeries(series: Series) {
        viewModelScope.launch {
            repository.deleteSeries(series)
        }
    }
}

class HomeViewModelFactory(
    private val repository: MediaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
