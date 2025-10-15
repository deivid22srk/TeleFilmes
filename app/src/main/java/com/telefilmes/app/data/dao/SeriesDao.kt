package com.telefilmes.app.data.dao

import androidx.room.*
import com.telefilmes.app.data.model.Series
import com.telefilmes.app.data.model.SeriesWithSeasons
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series ORDER BY updatedAt DESC")
    fun getAllSeries(): Flow<List<Series>>
    
    @Transaction
    @Query("SELECT * FROM series WHERE id = :seriesId")
    fun getSeriesWithSeasons(seriesId: Long): Flow<SeriesWithSeasons?>
    
    @Query("SELECT * FROM series WHERE id = :seriesId")
    suspend fun getSeriesById(seriesId: Long): Series?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: Series): Long
    
    @Update
    suspend fun updateSeries(series: Series)
    
    @Delete
    suspend fun deleteSeries(series: Series)
}
