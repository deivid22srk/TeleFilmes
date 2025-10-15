package com.telefilmes.app.telegram

data class TelegramChat(
    val id: Long,
    val title: String,
    val type: String,
    val photoPath: String? = null,
    val lastMessage: String? = null
)

data class TelegramMessage(
    val id: Long,
    val chatId: Long,
    val text: String,
    val date: Int,
    val hasVideo: Boolean = false,
    val videoFileId: String? = null,
    val videoThumbnail: String? = null,
    val videoDuration: Int = 0,
    val videoSize: Long = 0
)

sealed class TelegramAuthState {
    object Idle : TelegramAuthState()
    object WaitingForPhoneNumber : TelegramAuthState()
    data class WaitingForCode(val phoneNumber: String) : TelegramAuthState()
    data class WaitingForPassword(val phoneNumber: String) : TelegramAuthState()
    object Authenticated : TelegramAuthState()
    data class Error(val message: String) : TelegramAuthState()
}
