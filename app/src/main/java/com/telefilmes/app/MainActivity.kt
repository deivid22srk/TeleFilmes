package com.telefilmes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
                        
                        composable(Screen.TelegramLogin.route) {
                            TelegramLoginScreen(
                                viewModel = telegramViewModel,
                                onLoginSuccess = {
                                    navController.popBackStack()
                                    navController.navigate(Screen.TelegramChats.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
