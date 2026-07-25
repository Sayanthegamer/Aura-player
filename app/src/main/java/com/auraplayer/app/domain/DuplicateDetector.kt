package com.auraplayer.app.domain

import com.auraplayer.app.data.TrackEntity

object DuplicateDetector {

    data class DuplicateGroup(
        val title: String,
        val artistName: String,
        val tracks: List<TrackEntity>
    )

    fun findDuplicates(tracks: List<TrackEntity>): List<DuplicateGroup> {
        return tracks
            .groupBy { normalize(it.title) to normalize(it.artistName) }
            .filter { (key, groupTracks) ->
                key.first.isNotBlank() && groupTracks.size > 1 &&
                        groupTracks.map { it.filePath }.distinct().size > 1
            }
            .map { (_, groupTracks) ->
                DuplicateGroup(
                    title = groupTracks.first().title,
                    artistName = groupTracks.first().artistName,
                    tracks = groupTracks.sortedByDescending { it.dateAdded }
                )
            }
    }

    private fun normalize(str: String): String {
        return str.trim().lowercase()
            .replace(Regex("[^a-z0-9]"), "")
    }
}
