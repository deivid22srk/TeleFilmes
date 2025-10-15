package com.telefilmes.app.data.repository

import com.telefilmes.app.data.dao.EpisodeDao
import com.telefilmes.app.data.dao.SeasonDao
import com.telefilmes.app.data.dao.SeriesDao
import com.telefilmes.app.data.model.Episode
import com.telefilmes.app.data.model.Season
import com.telefilmes.app.data.model.Series
import kotlinx.coroutines.flow.Flow

class MediaRepository(
    private val seriesDao: SeriesDao,
    private val seasonDao: SeasonDao,
    private val episodeDao: EpisodeDao
) {
    fun getAllSeries(): Flow<List<Series>> = seriesDao.getAllSeries()
    
    fun getSeriesWithSeasons(seriesId: Long) = seriesDao.getSeriesWithSeasons(seriesId)
    
    suspend fun insertSeries(series: Series): Long = seriesDao.insertSeries(series)
    
    suspend fun updateSeries(series: Series) = seriesDao.updateSeries(series)
    
    suspend fun deleteSeries(series: Series) = seriesDao.deleteSeries(series)
    
    fun getSeasonsBySeries(seriesId: Long): Flow<List<Season>> = 
        seasonDao.getSeasonsBySeries(seriesId)
    
    fun getSeasonWithEpisodes(seasonId: Long) = seasonDao.getSeasonWithEpisodes(seasonId)
    
    suspend fun insertSeason(season: Season): Long = seasonDao.insertSeason(season)
    
    suspend fun updateSeason(season: Season) = seasonDao.updateSeason(season)
    
    suspend fun deleteSeason(season: Season) = seasonDao.deleteSeason(season)
    
    fun getEpisodesBySeason(seasonId: Long): Flow<List<Episode>> = 
        episodeDao.getEpisodesBySeason(seasonId)
    
    suspend fun insertEpisode(episode: Episode): Long = episodeDao.insertEpisode(episode)
    
    suspend fun updateEpisode(episode: Episode) = episodeDao.updateEpisode(episode)
    
    suspend fun deleteEpisode(episode: Episode) = episodeDao.deleteEpisode(episode)
    
    fun getEpisodeCount(seasonId: Long): Flow<Int> = episodeDao.getEpisodeCount(seasonId)
}
