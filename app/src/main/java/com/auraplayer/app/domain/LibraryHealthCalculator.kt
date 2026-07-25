package com.auraplayer.app.domain

import com.auraplayer.app.data.TrackEntity

object LibraryHealthCalculator {

    data class HealthSummary(
        val totalTracks: Int = 0,
        val missingGenreCount: Int = 0,
        val missingArtworkCount: Int = 0,
        val zeroBpmCount: Int = 0
    )

    fun calculateHealth(tracks: List<TrackEntity>): HealthSummary {
        val missingGenre = tracks.count { it.genre.isBlank() || it.genre.equals("Unknown", ignoreCase = true) }
        val missingArtwork = tracks.count { !it.hasArtwork }
        val zeroBpm = tracks.count { it.bpm == 0 }

        return HealthSummary(
            totalTracks = tracks.size,
            missingGenreCount = missingGenre,
            missingArtworkCount = missingArtwork,
            zeroBpmCount = zeroBpm
        )
    }

    fun filterMissingGenre(tracks: List<TrackEntity>): List<TrackEntity> {
        return tracks.filter { it.genre.isBlank() || it.genre.equals("Unknown", ignoreCase = true) }
    }

    fun filterMissingArtwork(tracks: List<TrackEntity>): List<TrackEntity> {
        return tracks.filter { !it.hasArtwork }
    }

    fun filterZeroBpm(tracks: List<TrackEntity>): List<TrackEntity> {
        return tracks.filter { it.bpm == 0 }
    }
}
