package com.telefilmes.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = Season::class,
            parentColumns = ["id"],
            childColumns = ["seasonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("seasonId")]
)
data class Episode(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val seasonId: Long,
    val episodeNumber: Int,
    val title: String,
    val telegramFileId: String,
    val telegramMessageId: Long,
    val telegramChatId: Long,
    val thumbnailUrl: String? = null,
    val duration: Int = 0,
    val fileSize: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
