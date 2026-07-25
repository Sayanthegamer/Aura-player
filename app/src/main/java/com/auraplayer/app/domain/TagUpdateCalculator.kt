package com.auraplayer.app.domain

import com.auraplayer.app.data.TrackEntity

object TagUpdateCalculator {

    fun applyBulkTags(
        tracks: List<TrackEntity>,
        targetTrackIds: Set<Long>,
        newGenre: String?,
        newMoodTags: String?,
        newBpm: Int?
    ): List<TrackEntity> {
        return tracks.map { track ->
            if (targetTrackIds.contains(track.id)) {
                track.copy(
                    genre = newGenre?.trim() ?: track.genre,
                    moodTags = newMoodTags?.trim() ?: track.moodTags,
                    bpm = newBpm ?: track.bpm
                )
            } else {
                track
            }
        }
    }
}
