package com.telefilmes.app.telegram

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TelegramClient(private val context: Context) {
    
    private val _authState = MutableStateFlow<TelegramAuthState>(TelegramAuthState.Idle)
    val authState: StateFlow<TelegramAuthState> = _authState.asStateFlow()
    
    private val _chats = MutableStateFlow<List<TelegramChat>>(emptyList())
    val chats: StateFlow<List<TelegramChat>> = _chats.asStateFlow()
    
    fun setPhoneNumber(phoneNumber: String) {
        _authState.value = TelegramAuthState.WaitingForCode(phoneNumber)
    }
    
    fun checkCode(code: String) {
        if (code.length >= 5) {
            _authState.value = TelegramAuthState.Authenticated
            loadMockChats()
        } else {
            _authState.value = TelegramAuthState.Error("Código inválido")
        }
    }
    
    fun checkPassword(password: String) {
        if (password.isNotBlank()) {
            _authState.value = TelegramAuthState.Authenticated
            loadMockChats()
        } else {
            _authState.value = TelegramAuthState.Error("Senha inválida")
        }
    }
    
    fun loadChats() {
        if (_authState.value is TelegramAuthState.Authenticated) {
            loadMockChats()
        }
    }
    
    private fun loadMockChats() {
        _chats.value = listOf(
            TelegramChat(
                id = 1,
                title = "Chat de Exemplo 1",
                type = "private",
                lastMessage = "Última mensagem do chat"
            ),
            TelegramChat(
                id = 2,
                title = "Grupo de Filmes",
                type = "group",
                lastMessage = "Compartilhando novos vídeos..."
            ),
            TelegramChat(
                id = 3,
                title = "Canal de Séries",
                type = "channel",
                lastMessage = "Nova temporada disponível!"
            )
        )
    }
    
    fun getChatMessages(chatId: Long, limit: Int = 50): List<TelegramMessage> {
        return listOf(
            TelegramMessage(
                id = 1,
                chatId = chatId,
                text = "Vídeo de exemplo",
                date = (System.currentTimeMillis() / 1000).toInt(),
                hasVideo = true,
                videoFileId = "mock_file_id_1",
                videoDuration = 1800,
                videoSize = 50000000
            ),
            TelegramMessage(
                id = 2,
                chatId = chatId,
                text = "Outro vídeo",
                date = (System.currentTimeMillis() / 1000).toInt(),
                hasVideo = true,
                videoFileId = "mock_file_id_2",
                videoDuration = 2400,
                videoSize = 75000000
            )
        )
    }
    
    fun downloadFile(fileId: Int) {
    }
    
    fun logout() {
        _authState.value = TelegramAuthState.Idle
        _chats.value = emptyList()
    }
    
    fun close() {
    }
}
