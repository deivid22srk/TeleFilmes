package com.telefilmes.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SeasonWithEpisodes(
    @Embedded val season: Season,
    @Relation(
        parentColumn = "id",
        entityColumn = "seasonId"
    )
    val episodes: List<Episode>
)
