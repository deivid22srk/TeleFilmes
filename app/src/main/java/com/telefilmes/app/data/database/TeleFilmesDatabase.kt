package com.telefilmes.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.telefilmes.app.data.dao.EpisodeDao
import com.telefilmes.app.data.dao.SeasonDao
import com.telefilmes.app.data.dao.SeriesDao
import com.telefilmes.app.data.model.Episode
import com.telefilmes.app.data.model.Season
import com.telefilmes.app.data.model.Series

@Database(
    entities = [Series::class, Season::class, Episode::class],
    version = 1,
    exportSchema = false
)
abstract class TeleFilmesDatabase : RoomDatabase() {
    abstract fun seriesDao(): SeriesDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    
    companion object {
        @Volatile
        private var INSTANCE: TeleFilmesDatabase? = null
        
        fun getDatabase(context: Context): TeleFilmesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TeleFilmesDatabase::class.java,
                    "telefilmes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
