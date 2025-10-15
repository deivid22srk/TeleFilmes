package com.telefilmes.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SeriesWithSeasons(
    @Embedded val series: Series,
    @Relation(
        parentColumn = "id",
        entityColumn = "seriesId"
    )
    val seasons: List<Season>
)
