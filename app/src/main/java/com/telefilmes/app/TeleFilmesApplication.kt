package com.telefilmes.app

import android.app.Application
import com.telefilmes.app.data.database.TeleFilmesDatabase
import com.telefilmes.app.data.repository.MediaRepository
import com.telefilmes.app.telegram.TelegramClient

class TeleFilmesApplication : Application() {
    
    val database by lazy { TeleFilmesDatabase.getDatabase(this) }
    
    val mediaRepository by lazy {
        MediaRepository(
            database.seriesDao(),
            database.seasonDao(),
            database.episodeDao()
        )
    }
    
    val telegramClient by lazy { TelegramClient(this) }
    
    override fun onCreate() {
        super.onCreate()
        try {
            System.loadLibrary("tdjni")
            android.util.Log.d("TeleFilmesApp", "TDLib native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            android.util.Log.e("TeleFilmesApp", "TDLib native library not found", e)
        }
    }
}
