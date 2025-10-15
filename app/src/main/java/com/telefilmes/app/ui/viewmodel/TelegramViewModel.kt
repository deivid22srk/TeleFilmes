package com.telefilmes.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.telefilmes.app.telegram.TelegramAuthState
import com.telefilmes.app.telegram.TelegramChat
import com.telefilmes.app.telegram.TelegramClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TelegramViewModel(private val telegramClient: TelegramClient) : ViewModel() {
    
    val authState: StateFlow<TelegramAuthState> = telegramClient.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TelegramAuthState.Idle
        )
    
    val chats: StateFlow<List<TelegramChat>> = telegramClient.chats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun setPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            telegramClient.setPhoneNumber(phoneNumber)
        }
    }
    
    fun checkCode(code: String) {
        viewModelScope.launch {
            telegramClient.checkCode(code)
        }
    }
    
    fun checkPassword(password: String) {
        viewModelScope.launch {
            telegramClient.checkPassword(password)
        }
    }
    
    fun loadChats() {
        viewModelScope.launch {
            telegramClient.loadChats()
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            telegramClient.logout()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        telegramClient.close()
    }
}

class TelegramViewModelFactory(
    private val telegramClient: TelegramClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TelegramViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TelegramViewModel(telegramClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
