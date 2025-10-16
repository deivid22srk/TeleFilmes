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
        // TDLib native library not included (using mock implementation)
        // To use real TDLib, follow guide in TDLIB_SETUP.md
        // try {
        //     System.loadLibrary("tdjni")
        // } catch (e: UnsatisfiedLinkError) {
        //     android.util.Log.e("TeleFilmesApp", "TDLib native library not found", e)
        // }
    }
}
