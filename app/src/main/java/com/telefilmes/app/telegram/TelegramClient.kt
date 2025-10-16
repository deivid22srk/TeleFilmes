package com.telefilmes.app.telegram

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TelegramClient(private val context: Context) {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _authState = MutableStateFlow<TelegramAuthState>(TelegramAuthState.Idle)
    val authState: StateFlow<TelegramAuthState> = _authState.asStateFlow()
    
    private val _chats = MutableStateFlow<List<TelegramChat>>(emptyList())
    val chats: StateFlow<List<TelegramChat>> = _chats.asStateFlow()
    
    private val chatCache = mutableMapOf<Long, TelegramChat>()
    private val messageCache = mutableMapOf<Long, MutableList<TelegramMessage>>()
    
    companion object {
        private const val TAG = "TelegramClient"
        private const val API_ID = 94575
        private const val API_HASH = "a3406de8d171bb422bb6ddf3bbd800e2"
    }
    
    init {
        Log.d(TAG, "TelegramClient initialized - Using mock data for now")
        _authState.value = TelegramAuthState.WaitingForPhoneNumber
    }
    
    fun setPhoneNumber(phoneNumber: String) {
        scope.launch {
            try {
                Log.d(TAG, "Setting phone number: $phoneNumber")
                _authState.value = TelegramAuthState.WaitingForCode(phoneNumber)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting phone number", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Erro ao enviar número")
            }
        }
    }
    
    fun checkCode(code: String) {
        scope.launch {
            try {
                Log.d(TAG, "Checking code: $code")
                if (code.length >= 5) {
                    _authState.value = TelegramAuthState.Authenticated
                    loadMockChats()
                } else {
                    _authState.value = TelegramAuthState.Error("Código deve ter pelo menos 5 dígitos")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking code", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Código inválido")
            }
        }
    }
    
    fun checkPassword(password: String) {
        scope.launch {
            try {
                Log.d(TAG, "Checking password")
                if (password.isNotBlank()) {
                    _authState.value = TelegramAuthState.Authenticated
                    loadMockChats()
                } else {
                    _authState.value = TelegramAuthState.Error("Senha não pode estar vazia")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking password", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Senha inválida")
            }
        }
    }
    
    fun loadChats() {
        if (_authState.value is TelegramAuthState.Authenticated) {
            loadMockChats()
        }
    }
    
    private fun loadMockChats() {
        scope.launch {
            try {
                Log.d(TAG, "Loading mock chats")
                chatCache.clear()
                
                val mockChats = listOf(
                    TelegramChat(
                        id = 1,
                        title = "Filmes e Séries HD",
                        type = "group",
                        lastMessage = "Novo episódio disponível"
                    ),
                    TelegramChat(
                        id = 2,
                        title = "Canal de Séries",
                        type = "channel",
                        lastMessage = "Breaking Bad S05E16"
                    ),
                    TelegramChat(
                        id = 3,
                        title = "Grupo Filmes 4K",
                        type = "supergroup",
                        lastMessage = "[Vídeo] Avatar 2"
                    ),
                    TelegramChat(
                        id = 4,
                        title = "Anime Brasil",
                        type = "group",
                        lastMessage = "One Piece - Episódio 1100"
                    )
                )
                
                mockChats.forEach { chat ->
                    chatCache[chat.id] = chat
                }
                
                _chats.value = chatCache.values.toList()
                
                generateMockMessages()
                
                Log.d(TAG, "Loaded ${mockChats.size} mock chats")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading chats", e)
            }
        }
    }
    
    private fun generateMockMessages() {
        val currentTime = (System.currentTimeMillis() / 1000).toInt()
        
        chatCache.keys.forEach { chatId ->
            val messages = mutableListOf<TelegramMessage>()
            
            for (i in 1..10) {
                messages.add(
                    TelegramMessage(
                        id = (chatId * 100 + i),
                        chatId = chatId,
                        text = "Vídeo ${i} - Episódio ${i}",
                        date = currentTime - (i * 3600),
                        hasVideo = true,
                        videoFileId = "mock_file_${chatId}_$i",
                        videoDuration = 1800 + (i * 120),
                        videoSize = 50000000L + (i * 5000000L)
                    )
                )
            }
            
            messageCache[chatId] = messages
        }
    }
    
    fun getChatMessages(chatId: Long, limit: Int = 50): List<TelegramMessage> {
        Log.d(TAG, "Getting messages for chat: $chatId")
        return messageCache[chatId]?.take(limit) ?: emptyList()
    }
    
    fun downloadFile(fileId: Int) {
        scope.launch {
            try {
                Log.d(TAG, "Downloading file: $fileId")
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading file", e)
            }
        }
    }
    
    fun logout() {
        scope.launch {
            try {
                Log.d(TAG, "Logging out")
                _authState.value = TelegramAuthState.Idle
                chatCache.clear()
                messageCache.clear()
                _chats.value = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "Error logging out", e)
            }
        }
    }
    
    fun close() {
        scope.launch {
            try {
                Log.d(TAG, "Closing client")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing client", e)
            }
        }
    }
}
