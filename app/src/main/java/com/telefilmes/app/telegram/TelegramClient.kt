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
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class TelegramClient(private val context: Context) {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var client: Client? = null
    private val currentQueryId = AtomicLong()
    
    private val _authState = MutableStateFlow<TelegramAuthState>(TelegramAuthState.Idle)
    val authState: StateFlow<TelegramAuthState> = _authState.asStateFlow()
    
    private val _chats = MutableStateFlow<List<TelegramChat>>(emptyList())
    val chats: StateFlow<List<TelegramChat>> = _chats.asStateFlow()
    
    private val chatCache = ConcurrentHashMap<Long, TelegramChat>()
    private val messageCache = ConcurrentHashMap<Long, MutableList<TelegramMessage>>()
    
    companion object {
        private const val TAG = "TelegramClient"
        
        // ⚠️ IMPORTANTE: Estas credenciais estão DESATUALIZADAS e causam erro UPDATE_APP_TO_LOGIN
        // Para corrigir:
        // 1. Acesse: https://my.telegram.org/apps
        // 2. Crie uma aplicação
        // 3. Substitua os valores abaixo pelos seus
        // 
        // NUNCA compartilhe suas credenciais publicamente!
        private const val API_ID = 94575  // ❌ Substitua pelo seu api_id
        private const val API_HASH = "a3406de8d171bb422bb6ddf3bbd800e2"  // ❌ Substitua pelo seu api_hash
        
        // Opção para teste: use servidor de teste do Telegram
        // Mude para 'true' apenas para testes (não conecta ao Telegram real)
        private const val USE_TEST_DC = false
    }
    
    init {
        initializeClient()
    }
    
    private fun initializeClient() {
        scope.launch {
            try {
                Client.setLogVerbosityLevel(1)
                
                client = Client.create(
                    { update -> handleUpdate(update) },
                    { exception -> handleException(exception) },
                    { exception -> handleException(exception) }
                )
                
                Log.d(TAG, "TelegramClient initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing client", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Erro ao inicializar")
            }
        }
    }
    
    private fun handleUpdate(update: Any?) {
        if (update !is TdApi.Object) return
        
        when (update) {
            is TdApi.UpdateAuthorizationState -> {
                handleAuthorizationState(update.authorizationState)
            }
            is TdApi.UpdateNewChat -> {
                handleNewChat(update.chat)
            }
            is TdApi.UpdateNewMessage -> {
                handleNewMessage(update.message)
            }
            else -> {
                Log.d(TAG, "Update: ${update.javaClass.simpleName}")
            }
        }
    }
    
    private fun handleAuthorizationState(state: TdApi.AuthorizationState) {
        Log.d(TAG, "Authorization state changed: ${state.javaClass.simpleName}")
        when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> {
                val parameters = TdApi.TdlibParameters().apply {
                    useTestDc = USE_TEST_DC  // Mude para true para usar servidor de teste
                    databaseDirectory = context.filesDir.absolutePath + "/tdlib"
                    filesDirectory = context.filesDir.absolutePath + "/tdlib_files"
                    useFileDatabase = true
                    useChatInfoDatabase = true
                    useMessageDatabase = true
                    useSecretChats = false
                    apiId = API_ID
                    apiHash = API_HASH
                    systemLanguageCode = "pt-BR"
                    deviceModel = android.os.Build.MODEL
                    systemVersion = android.os.Build.VERSION.RELEASE
                    applicationVersion = "1.0"
                    enableStorageOptimizer = true
                }
                send(TdApi.SetTdlibParameters(parameters))
            }
            is TdApi.AuthorizationStateWaitEncryptionKey -> {
                send(TdApi.CheckDatabaseEncryptionKey())
            }
            is TdApi.AuthorizationStateWaitPhoneNumber -> {
                Log.d(TAG, "Waiting for phone number")
                _authState.value = TelegramAuthState.WaitingForPhoneNumber
            }
            is TdApi.AuthorizationStateWaitCode -> {
                Log.d(TAG, "Waiting for verification code")
                _authState.value = TelegramAuthState.WaitingForCode("")
            }
            is TdApi.AuthorizationStateWaitPassword -> {
                Log.d(TAG, "Waiting for password")
                _authState.value = TelegramAuthState.WaitingForPassword("")
            }
            is TdApi.AuthorizationStateReady -> {
                Log.d(TAG, "Authentication successful")
                _authState.value = TelegramAuthState.Authenticated
                loadChats()
            }
            is TdApi.AuthorizationStateClosed -> {
                Log.d(TAG, "Client closed")
                _authState.value = TelegramAuthState.Idle
            }
            else -> {
                Log.d(TAG, "Auth state: ${state.javaClass.simpleName}")
            }
        }
    }
    
    private fun handleNewChat(chat: TdApi.Chat) {
        val telegramChat = TelegramChat(
            id = chat.id,
            title = chat.title,
            type = when (chat.type) {
                is TdApi.ChatTypePrivate -> "private"
                is TdApi.ChatTypeBasicGroup -> "group"
                is TdApi.ChatTypeSupergroup -> "supergroup"
                is TdApi.ChatTypeSecret -> "secret"
                else -> "unknown"
            },
            photoPath = null,
            lastMessage = chat.lastMessage?.content?.let {
                when (it) {
                    is TdApi.MessageText -> it.text.text
                    is TdApi.MessageVideo -> "[Vídeo]"
                    is TdApi.MessagePhoto -> "[Foto]"
                    else -> ""
                }
            }
        )
        
        chatCache[chat.id] = telegramChat
        _chats.value = chatCache.values.toList()
    }
    
    private fun handleNewMessage(message: TdApi.Message) {
        val telegramMessage = convertMessage(message)
        
        messageCache.getOrPut(message.chatId) { mutableListOf() }.add(telegramMessage)
    }
    
    private fun convertMessage(message: TdApi.Message): TelegramMessage {
        val content = message.content
        val hasVideo = content is TdApi.MessageVideo
        
        return TelegramMessage(
            id = message.id,
            chatId = message.chatId,
            text = when (content) {
                is TdApi.MessageText -> content.text.text
                is TdApi.MessageVideo -> content.caption.text
                else -> ""
            },
            date = message.date,
            hasVideo = hasVideo,
            videoFileId = if (hasVideo) (content as TdApi.MessageVideo).video.video.id.toString() else null,
            videoDuration = if (hasVideo) (content as TdApi.MessageVideo).video.duration else 0,
            videoSize = if (hasVideo) (content as TdApi.MessageVideo).video.video.size.toLong() else 0L
        )
    }
    
    private fun handleException(exception: Throwable?) {
        exception?.let {
            Log.e(TAG, "TDLib error", it)
            _authState.value = TelegramAuthState.Error(it.message ?: "Unknown error")
        }
    }
    
    private fun send(function: TdApi.Function) {
        client?.send(function) { result ->
            if (result is TdApi.Error) {
                Log.e(TAG, "TDLib error: ${result.code} - ${result.message}")
                _authState.value = TelegramAuthState.Error(result.message)
            }
        }
    }
    
    fun setPhoneNumber(phoneNumber: String) {
        scope.launch {
            try {
                val settings = TdApi.PhoneNumberAuthenticationSettings().apply {
                    allowFlashCall = false
                    isCurrentPhoneNumber = false
                    allowSmsRetrieverApi = false
                }
                send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, settings))
                Log.d(TAG, "Phone number sent: $phoneNumber")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting phone number", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Erro ao enviar número")
            }
        }
    }
    
    fun checkCode(code: String) {
        scope.launch {
            try {
                send(TdApi.CheckAuthenticationCode(code))
                Log.d(TAG, "Code sent: $code")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking code", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Código inválido")
            }
        }
    }
    
    fun checkPassword(password: String) {
        scope.launch {
            try {
                send(TdApi.CheckAuthenticationPassword(password))
                Log.d(TAG, "Password sent")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking password", e)
                _authState.value = TelegramAuthState.Error(e.message ?: "Senha inválida")
            }
        }
    }
    
    fun loadChats() {
        scope.launch {
            try {
                send(TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 100))
                Log.d(TAG, "Loading chats...")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading chats", e)
            }
        }
    }
    
    fun getChatMessages(chatId: Long, limit: Int = 50): List<TelegramMessage> {
        scope.launch {
            try {
                client?.send(TdApi.GetChatHistory(chatId, 0, 0, limit, false)) { result ->
                    if (result is TdApi.Messages) {
                        val messages = result.messages.map { message ->
                            convertMessage(message as TdApi.Message)
                        }
                        messageCache[chatId] = messages.toMutableList()
                    }
                }
                Log.d(TAG, "Loading messages for chat: $chatId")
            } catch (e: Exception) {
                Log.e(TAG, "Error getting chat messages", e)
            }
        }
        
        return messageCache[chatId]?.reversed() ?: emptyList()
    }
    
    fun downloadFile(fileId: Int) {
        scope.launch {
            try {
                send(TdApi.DownloadFile(fileId, 32, 0, 0, true))
                Log.d(TAG, "Downloading file: $fileId")
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading file", e)
            }
        }
    }
    
    fun logout() {
        scope.launch {
            try {
                send(TdApi.LogOut())
                _authState.value = TelegramAuthState.Idle
                chatCache.clear()
                messageCache.clear()
                _chats.value = emptyList()
                Log.d(TAG, "Logged out")
            } catch (e: Exception) {
                Log.e(TAG, "Error logging out", e)
            }
        }
    }
    
    fun close() {
        scope.launch {
            try {
                client?.close()
                Log.d(TAG, "Client closed")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing client", e)
            }
        }
    }
}
