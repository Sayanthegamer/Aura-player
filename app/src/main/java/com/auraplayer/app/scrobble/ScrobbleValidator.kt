package com.auraplayer.app.scrobble

import com.auraplayer.app.data.TrackEntity

object ScrobbleValidator {

    const val MIN_TRACK_DURATION_MS = 30_000L // 30 seconds
    const val MAX_THRESHOLD_MS = 240_000L // 4 minutes (240s)

    fun isEligibleForScrobble(
        durationMs: Long,
        playbackPositionMs: Long
    ): Boolean {
        if (durationMs < MIN_TRACK_DURATION_MS) return false

        val halfDuration = durationMs / 2L
        val requiredThreshold = kotlin.math.min(halfDuration, MAX_THRESHOLD_MS)

        return playbackPositionMs >= requiredThreshold
    }

    fun createScrobblePayload(
        track: TrackEntity,
        timestampMs: Long = System.currentTimeMillis()
    ): ScrobbleQueueEntity {
        return ScrobbleQueueEntity(
            trackTitle = track.title,
            artistName = track.artistName,
            albumName = track.albumName,
            timestampMs = timestampMs,
            durationMs = track.durationMs,
            status = "PENDING"
        )
    }
}
