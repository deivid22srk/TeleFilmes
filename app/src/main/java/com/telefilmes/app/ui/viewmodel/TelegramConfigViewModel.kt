package com.telefilmes.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.telefilmes.app.data.TelegramConfigRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TelegramConfigViewModel(
    private val configRepository: TelegramConfigRepository
) : ViewModel() {
    
    val apiId: StateFlow<Int> = configRepository.apiId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TelegramConfigRepository.DEFAULT_API_ID
        )
    
    val apiHash: StateFlow<String> = configRepository.apiHash
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TelegramConfigRepository.DEFAULT_API_HASH
        )
    
    val hasCustomCredentials: StateFlow<Boolean> = configRepository.hasCustomCredentials
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    fun saveCredentials(apiId: Int, apiHash: String) {
        viewModelScope.launch {
            configRepository.saveCredentials(apiId, apiHash)
        }
    }
    
    fun clearCredentials() {
        viewModelScope.launch {
            configRepository.clearCredentials()
        }
    }
}

class TelegramConfigViewModelFactory(
    private val configRepository: TelegramConfigRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TelegramConfigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TelegramConfigViewModel(configRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
