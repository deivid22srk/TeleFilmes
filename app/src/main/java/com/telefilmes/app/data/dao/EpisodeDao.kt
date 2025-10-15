package com.telefilmes.app.data.dao

import androidx.room.*
import com.telefilmes.app.data.model.Episode
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE seasonId = :seasonId ORDER BY episodeNumber ASC")
    fun getEpisodesBySeason(seasonId: Long): Flow<List<Episode>>
    
    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    suspend fun getEpisodeById(episodeId: Long): Episode?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: Episode): Long
    
    @Update
    suspend fun updateEpisode(episode: Episode)
    
    @Delete
    suspend fun deleteEpisode(episode: Episode)
    
    @Query("SELECT COUNT(*) FROM episodes WHERE seasonId = :seasonId")
    fun getEpisodeCount(seasonId: Long): Flow<Int>
}
