package com.auraplayer.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Long,
    val mediaStoreId: Long,
    val title: String,
    val artistName: String,
    val albumName: String,
    val artistId: Long,
    val albumId: Long,
    val durationMs: Long,
    val filePath: String,
    val uriString: String,
    val albumArtUri: String?,
    val mimeType: String,
    val codec: String = "MP3",
    val bitrate: Int = 0,
    val sampleRate: Int = 0,
    val bitDepth: Int = 16,
    val replayGainTrackGain: Float? = null,
    val replayGainTrackPeak: Float? = null,
    val dateAdded: Long = 0L,
    val bpm: Int = 0,
    val genre: String = "Unknown",
    val moodTags: String = "",
    val lastPlayedTimestamp: Long = 0L
)

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artistName: String,
    val trackCount: Int,
    val albumArtUri: String?
)

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)
