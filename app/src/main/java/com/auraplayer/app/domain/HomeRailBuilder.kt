package com.auraplayer.app.domain

import com.auraplayer.app.data.ArtistEntity
import com.auraplayer.app.data.TrackEntity
import com.auraplayer.app.playback.AutoplayScorer

object HomeRailBuilder {

    sealed class HomeRail {
        data class ContinueListening(val track: TrackEntity, val isRecentPlayed: Boolean) : HomeRail()
        data class MadeForYou(val title: String, val tracks: List<TrackEntity>) : HomeRail()
        data class MostPlayedArtists(val title: String, val artists: List<Pair<ArtistEntity, Int>>) : HomeRail()
        data class RecentlyAdded(val title: String, val tracks: List<TrackEntity>) : HomeRail()
        data class OnRepeat(val title: String, val tracks: List<TrackEntity>) : HomeRail()
    }

    fun buildContinueListeningHero(
        tracks: List<TrackEntity>,
        currentTimeMs: Long = System.currentTimeMillis()
    ): HomeRail.ContinueListening? {
        if (tracks.isEmpty()) return null
        val sevenDaysMs = 7 * 24 * 60 * 60 * 1000L
        val recentlyPlayed = tracks
            .filter { it.lastPlayedTimestamp > 0 && (currentTimeMs - it.lastPlayedTimestamp) <= sevenDaysMs }
            .maxByOrNull { it.lastPlayedTimestamp }

        return if (recentlyPlayed != null) {
            HomeRail.ContinueListening(recentlyPlayed, isRecentPlayed = true)
        } else {
            val recentlyAdded = tracks.maxByOrNull { it.dateAdded } ?: tracks.first()
            HomeRail.ContinueListening(recentlyAdded, isRecentPlayed = false)
        }
    }

    fun buildMadeForYouCarousel(
        tracks: List<TrackEntity>,
        seedTrack: TrackEntity?,
        artistPlayCounts: Map<String, Int> = emptyMap()
    ): List<TrackEntity> {
        if (tracks.isEmpty() || seedTrack == null) return emptyList()

        val maxScrobble = artistPlayCounts.values.maxOrNull() ?: 1
        val scoredCandidates = tracks
            .filter { it.id != seedTrack.id }
            .map { candidate ->
                candidate to AutoplayScorer.calculateScore(
                    seedTrack = seedTrack,
                    candidateTrack = candidate,
                    scrobblePlayCounts = artistPlayCounts,
                    maxScrobbleCount = maxScrobble
                )
            }
            .sortedByDescending { it.second }

        // Deduplicate by artist so one artist doesn't dominate the row
        val seenArtists = mutableSetOf<String>()
        val result = mutableListOf<TrackEntity>()

        for ((candidate, score) in scoredCandidates) {
            val normalizedArtist = candidate.artistName.lowercase().trim()
            if (!seenArtists.contains(normalizedArtist)) {
                seenArtists.add(normalizedArtist)
                result.add(candidate)
            }
            if (result.size >= 10) break
        }

        return result
    }

    fun buildRecentlyAdded(
        tracks: List<TrackEntity>,
        currentTimeMs: Long = System.currentTimeMillis()
    ): List<TrackEntity> {
        if (tracks.isEmpty()) return emptyList()

        val fourteenDaysSec = 14 * 24 * 60 * 60L
        val fourteenDaysMs = fourteenDaysSec * 1000L
        // Date added can be in seconds (MediaStore default) or ms
        val inFourteenDays = tracks.filter { track ->
            val timestampMs = if (track.dateAdded > 10_000_000_000L) track.dateAdded else track.dateAdded * 1000L
            (currentTimeMs - timestampMs) <= fourteenDaysMs
        }

        // Fallback: If 14-day window yields fewer than 5 tracks (e.g. big initial import or old files),
        // return top 20 most recently added tracks
        return if (inFourteenDays.size >= 5) {
            inFourteenDays.sortedByDescending { it.dateAdded }.take(20)
        } else {
            tracks.sortedByDescending { it.dateAdded }.take(20)
        }
    }
}
