package com.auraplayer.app.scrobble

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scrobble_queue")
data class ScrobbleQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val trackTitle: String,
    val artistName: String,
    val albumName: String,
    val timestampMs: Long,
    val durationMs: Long,
    val serviceTarget: String = "ALL", // "LASTFM", "LISTENBRAINZ", "OPEN_SCROBBLER", "ALL"
    val status: String = "PENDING" // "PENDING", "SYNCED", "FAILED"
)

enum class ScrobbleService(val displayName: String) {
    LAST_FM("Last.fm"),
    LISTENBRAINZ("ListenBrainz"),
    OPEN_SCROBBLER("Open Scrobbler")
}
