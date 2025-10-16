package com.telefilmes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.telefilmes.app.ui.navigation.Screen
import com.telefilmes.app.ui.screen.*
import com.telefilmes.app.ui.theme.TeleFilmesTheme
import com.telefilmes.app.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as TeleFilmesApplication
        
        setContent {
            TeleFilmesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModelFactory(app.mediaRepository)
                    )
                    
                    val telegramViewModel: TelegramViewModel = viewModel(
                        factory = TelegramViewModelFactory(app.telegramClient)
                    )
                    
                    val telegramConfigViewModel: TelegramConfigViewModel = viewModel(
                        factory = TelegramConfigViewModelFactory(app.telegramConfigRepository)
                    )
                    
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onSeriesClick = { seriesId ->
                                    navController.navigate(Screen.SeriesDetail.createRoute(seriesId))
                                },
                                onChatClick = {
                                    navController.navigate(Screen.TelegramChats.route)
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.SeriesDetail.route,
                            arguments = listOf(
                                navArgument("seriesId") { type = NavType.StringType }
                            )
                        ) {
                            val seriesDetailViewModel: SeriesDetailViewModel = viewModel(
                                factory = SeriesDetailViewModelFactory(app.mediaRepository)
                            )
                            
                            SeriesDetailScreen(
                                viewModel = seriesDetailViewModel,
                                onBackClick = { navController.navigateUp() },
                                onSeasonClick = { seasonId ->
                                    navController.navigate(Screen.SeasonDetail.createRoute(seasonId))
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.SeasonDetail.route,
                            arguments = listOf(
                                navArgument("seasonId") { type = NavType.StringType }
                            )
                        ) {
                            val seasonDetailViewModel: SeasonDetailViewModel = viewModel(
                                factory = SeasonDetailViewModelFactory(app.mediaRepository)
                            )
                            
                            SeasonDetailScreen(
                                viewModel = seasonDetailViewModel,
                                onBackClick = { navController.navigateUp() },
                                onAddFromTelegram = {
                                    navController.navigate(Screen.TelegramChats.route)
                                }
                            )
                        }
                        
                        composable(Screen.TelegramChats.route) {
                            TelegramChatsScreen(
                                viewModel = telegramViewModel,
                                onBackClick = { navController.navigateUp() },
                                onChatClick = { chatId ->
                                    navController.navigate(Screen.ChatMessages.createRoute(chatId))
                                },
                                onLoginRequired = {
                                    navController.navigate(Screen.TelegramLogin.route)
                                }
                            )
                        }
                        
                        composable(
                            route = Screen.ChatMessages.route,
                            arguments = listOf(
                                navArgument("chatId") { type = NavType.StringType }
                            )
                        ) {
                            val chatMessagesViewModel: ChatMessagesViewModel = viewModel(
                                factory = ChatMessagesViewModelFactory(
                                    app.telegramClient,
                                    app.mediaRepository
                                )
                            )
                            
                            val messages by chatMessagesViewModel.messages.collectAsState()
                            val seasons by chatMessagesViewModel.allSeasons.collectAsState()
                            val chatId = it.arguments?.getString("chatId")?.toLongOrNull() ?: 0L
                            val chatTitle = telegramViewModel.chats.collectAsState().value
                                .find { chat -> chat.id == chatId }
                                ?.title ?: "Chat"
                            
                            ChatMessagesScreen(
                                chatId = it.arguments?.getString("chatId")?.toLongOrNull() ?: 0L,
                                chatTitle = chatTitle,
                                messages = messages,
                                availableSeasons = seasons,
                                onBackClick = { navController.navigateUp() },
                                onSaveVideo = { message, seasonId ->
                                    chatMessagesViewModel.saveVideoToSeason(message, seasonId)
                                },
                                onDownloadVideo = { fileId ->
                                    chatMessagesViewModel.downloadVideo(fileId)
                                }
                            )
                        }
                        
                        composable(Screen.TelegramLogin.route) {
                            TelegramLoginScreen(
                                viewModel = telegramViewModel,
                                onLoginSuccess = {
                                    navController.popBackStack()
                                    navController.navigate(Screen.TelegramChats.route)
                                },
                                onConfigureApi = {
                                    navController.navigate(Screen.TelegramApiConfig.route)
                                }
                            )
                        }
                        
                        composable(Screen.TelegramApiConfig.route) {
                            val apiId by telegramConfigViewModel.apiId.collectAsState()
                            val apiHash by telegramConfigViewModel.apiHash.collectAsState()
                            val hasCustomCredentials by telegramConfigViewModel.hasCustomCredentials.collectAsState()
                            
                            TelegramApiConfigScreen(
                                currentApiId = apiId,
                                currentApiHash = apiHash,
                                hasCustomCredentials = hasCustomCredentials,
                                onSave = { newApiId, newApiHash ->
                                    telegramConfigViewModel.saveCredentials(newApiId, newApiHash)
                                    app.telegramClient.updateCredentials(newApiId, newApiHash)
                                    navController.popBackStack()
                                },
                                onBackClick = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}
