package com.telefilmes.app.telegram

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.util.concurrent.ConcurrentHashMap

class TelegramClient(private val context: Context) {
    
    private var client: Client? = null
    private val queryResults = ConcurrentHashMap<Long, Any?>()
    private var currentQueryId = 0L
    
    private val _authState = MutableStateFlow<TelegramAuthState>(TelegramAuthState.Idle)
    val authState: StateFlow<TelegramAuthState> = _authState.asStateFlow()
    
    private val _chats = MutableStateFlow<List<TelegramChat>>(emptyList())
    val chats: StateFlow<List<TelegramChat>> = _chats.asStateFlow()
    
    init {
        Client.setLogVerbosityLevel(1)
        createClient()
    }
    
    private fun createClient() {
        client = Client.create(
            { update -> handleUpdate(update) },
            { exception -> handleException(exception) },
            { exception -> handleException(exception) }
        )
    }
    
    private fun handleUpdate(update: Any) {
        when (update) {
            is TdApi.UpdateAuthorizationState -> {
                handleAuthorizationState(update.authorizationState)
            }
            is TdApi.UpdateNewChat -> {
                loadChats()
            }
        }
    }
    
    private fun handleAuthorizationState(state: TdApi.AuthorizationState) {
        when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> {
                val parameters = TdApi.SetTdlibParameters().apply {
                    useTestDc = false
                    databaseDirectory = context.filesDir.absolutePath + "/tdlib"
                    filesDirectory = context.filesDir.absolutePath + "/tdlib_files"
                    useFileDatabase = true
                    useChatInfoDatabase = true
                    useMessageDatabase = true
                    useSecretChats = false
                    apiId = 94575
                    apiHash = "a3406de8d171bb422bb6ddf3bbd800e2"
                    systemLanguageCode = "pt"
                    deviceModel = "Android"
                    systemVersion = "Unknown"
                    applicationVersion = "1.0"
                    enableStorageOptimizer = true
                }
                sendQuery(parameters)
            }
            is TdApi.AuthorizationStateWaitPhoneNumber -> {
                _authState.value = TelegramAuthState.WaitingForPhoneNumber
            }
            is TdApi.AuthorizationStateWaitCode -> {
                _authState.value = TelegramAuthState.WaitingForCode("")
            }
            is TdApi.AuthorizationStateWaitPassword -> {
                _authState.value = TelegramAuthState.WaitingForPassword("")
            }
            is TdApi.AuthorizationStateReady -> {
                _authState.value = TelegramAuthState.Authenticated
                loadChats()
            }
            is TdApi.AuthorizationStateClosed -> {
                _authState.value = TelegramAuthState.Idle
            }
        }
    }
    
    private fun handleException(exception: Throwable?) {
        exception?.let {
            _authState.value = TelegramAuthState.Error(it.message ?: "Unknown error")
        }
    }
    
    private fun sendQuery(query: TdApi.Function<*>): Any? {
        val queryId = currentQueryId++
        client?.send(query) { result ->
            queryResults[queryId] = result
        }
        return queryResults[queryId]
    }
    
    fun setPhoneNumber(phoneNumber: String) {
        val function = TdApi.SetAuthenticationPhoneNumber(phoneNumber, null)
        sendQuery(function)
    }
    
    fun checkCode(code: String) {
        val function = TdApi.CheckAuthenticationCode(code)
        sendQuery(function)
    }
    
    fun checkPassword(password: String) {
        val function = TdApi.CheckAuthenticationPassword(password)
        sendQuery(function)
    }
    
    fun loadChats() {
        val function = TdApi.GetChats(TdApi.ChatListMain(), 100)
        sendQuery(function)
    }
    
    fun getChatMessages(chatId: Long, limit: Int = 50): List<TelegramMessage> {
        return emptyList()
    }
    
    fun downloadFile(fileId: Int) {
        val function = TdApi.DownloadFile(fileId, 1, 0, 0, true)
        sendQuery(function)
    }
    
    fun logout() {
        val function = TdApi.LogOut()
        sendQuery(function)
    }
    
    fun close() {
        client?.close()
    }
}
