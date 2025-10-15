package com.telefilmes.app.data.dao

import androidx.room.*
import com.telefilmes.app.data.model.Season
import com.telefilmes.app.data.model.SeasonWithEpisodes
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons WHERE seriesId = :seriesId ORDER BY seasonNumber ASC")
    fun getSeasonsBySeries(seriesId: Long): Flow<List<Season>>
    
    @Transaction
    @Query("SELECT * FROM seasons WHERE id = :seasonId")
    fun getSeasonWithEpisodes(seasonId: Long): Flow<SeasonWithEpisodes?>
    
    @Query("SELECT * FROM seasons WHERE id = :seasonId")
    suspend fun getSeasonById(seasonId: Long): Season?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: Season): Long
    
    @Update
    suspend fun updateSeason(season: Season)
    
    @Delete
    suspend fun deleteSeason(season: Season)
}
