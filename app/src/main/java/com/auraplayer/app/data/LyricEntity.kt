package com.auraplayer.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lyrics")
data class LyricEntity(
    @PrimaryKey val trackId: String,
    val trackTitle: String,
    val artistName: String,
    val syncedLyrics: String?,
    val plainLyrics: String?,
    val userOffsetMs: Long = 0L,
    val lastUpdated: Long = System.currentTimeMillis()
)
