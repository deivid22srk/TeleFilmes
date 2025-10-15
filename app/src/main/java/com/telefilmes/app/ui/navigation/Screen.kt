package com.telefilmes.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SeriesDetail : Screen("series/{seriesId}") {
        fun createRoute(seriesId: Long) = "series/$seriesId"
    }
    object SeasonDetail : Screen("season/{seasonId}") {
        fun createRoute(seasonId: Long) = "season/$seasonId"
    }
    object TelegramChats : Screen("telegram_chats")
    object ChatMessages : Screen("chat/{chatId}") {
        fun createRoute(chatId: Long) = "chat/$chatId"
    }
    object TelegramLogin : Screen("telegram_login")
}
