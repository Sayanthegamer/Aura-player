package com.auraplayer.app

import com.auraplayer.app.data.TrackEntity
import com.auraplayer.app.playback.AutoplayScorer
import org.junit.Assert.*
import org.junit.Test

class AutoplayScorerTest {

    private val seedTrack = TrackEntity(
        id = 1L, mediaStoreId = 101L, title = "Aura Vibe", artistName = "Chill Artist",
        albumName = "Chill Album", artistId = 10L, albumId = 20L, durationMs = 200000L,
        filePath = "/sdcard/aura.mp3", uriString = "content://media/1", albumArtUri = null,
        mimeType = "audio/mp3", bpm = 120, genre = "Ambient", moodTags = "relaxed chill night"
    )

    @Test
    fun testExactGenreAndArtistMatchScoring() {
        val perfectMatch = TrackEntity(
            id = 2L, mediaStoreId = 102L, title = "Night Sky", artistName = "Chill Artist",
            albumName = "Chill Album", artistId = 10L, albumId = 20L, durationMs = 180000L,
            filePath = "/sdcard/night.mp3", uriString = "content://media/2", albumArtUri = null,
            mimeType = "audio/mp3", bpm = 122, genre = "Ambient", moodTags = "chill calm"
        )

        val score = AutoplayScorer.calculateScore(seedTrack, perfectMatch)
        assertTrue("Score for matching genre, artist, and BPM should be high", score > 0.70f)
    }

    @Test
    fun testRecencyPenaltyApplied() {
        val now = System.currentTimeMillis()
        val recentlyPlayed = TrackEntity(
            id = 3L, mediaStoreId = 103L, title = "Recent Track", artistName = "Chill Artist",
            albumName = "Chill Album", artistId = 10L, albumId = 20L, durationMs = 180000L,
            filePath = "/sdcard/recent.mp3", uriString = "content://media/3", albumArtUri = null,
            mimeType = "audio/mp3", bpm = 120, genre = "Ambient", moodTags = "relaxed",
            lastPlayedTimestamp = now - (30 * 60 * 1000) // played 30 mins ago
        )

        val unplayedTrack = recentlyPlayed.copy(id = 4L, lastPlayedTimestamp = 0L)

        val scoreRecent = AutoplayScorer.calculateScore(seedTrack, recentlyPlayed, now)
        val scoreUnplayed = AutoplayScorer.calculateScore(seedTrack, unplayedTrack, now)

        assertTrue("Recently played track score ($scoreRecent) should be lower than unplayed ($scoreUnplayed)", scoreRecent < scoreUnplayed)
    }

    @Test
    fun testFallbackSelectionWhenScoresLow() {
        val candidate = TrackEntity(
            id = 5L, mediaStoreId = 105L, title = "Rock Track", artistName = "Heavy Metal",
            albumName = "Metal Album", artistId = 99L, albumId = 88L, durationMs = 210000L,
            filePath = "/sdcard/rock.mp3", uriString = "content://media/5", albumArtUri = null,
            mimeType = "audio/mp3", bpm = 180, genre = "Rock", moodTags = "energetic fast"
        )

        val selected = AutoplayScorer.selectNextTrack(seedTrack, listOf(candidate))
        assertNotNull("Should return fallback candidate even if score is low", selected)
        assertEquals(5L, selected?.id)
    }
}
