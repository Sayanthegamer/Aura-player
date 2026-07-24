package com.auraplayer.app

import com.auraplayer.app.data.TrackEntity
import com.auraplayer.app.scrobble.ScrobbleValidator
import org.junit.Assert.*
import org.junit.Test

class ScrobbleThresholdTest {

    @Test
    fun testShortTrackUnder30SecondsIgnored() {
        val shortTrackDuration = 25_000L // 25s
        val isEligible = ScrobbleValidator.isEligibleForScrobble(shortTrackDuration, 25_000L)
        assertFalse("Tracks under 30s should never be eligible for scrobbling", isEligible)
    }

    @Test
    fun test50PercentThresholdRuleForStandardTrack() {
        val trackDuration = 180_000L // 3 minutes (180s) -> threshold = 90s (50%)
        
        assertFalse("Playback at 60s (< 50%) should not be eligible", 
            ScrobbleValidator.isEligibleForScrobble(trackDuration, 60_000L))
        
        assertTrue("Playback at 90s (== 50%) should be eligible", 
            ScrobbleValidator.isEligibleForScrobble(trackDuration, 90_000L))
        
        assertTrue("Playback at 120s (> 50%) should be eligible", 
            ScrobbleValidator.isEligibleForScrobble(trackDuration, 120_000L))
    }

    @Test
    fun test240SecondMaxThresholdRuleForLongTrack() {
        val longTrackDuration = 600_000L // 10 minutes (600s) -> 50% = 300s, max cap = 240s
        
        assertFalse("Playback at 200s (< 240s) should not be eligible", 
            ScrobbleValidator.isEligibleForScrobble(longTrackDuration, 200_000L))
        
        assertTrue("Playback at 240s (== 240s max threshold) should be eligible even though < 50%", 
            ScrobbleValidator.isEligibleForScrobble(longTrackDuration, 240_000L))
    }

    @Test
    fun testScrobblePayloadCreation() {
        val track = TrackEntity(
            id = 10L, mediaStoreId = 101L, title = "Test Song", artistName = "Test Artist",
            albumName = "Test Album", artistId = 1L, albumId = 2L, durationMs = 210000L,
            filePath = "/test.mp3", uriString = "content://1", albumArtUri = null, mimeType = "audio/mp3"
        )

        val payload = ScrobbleValidator.createScrobblePayload(track)
        assertEquals("Test Song", payload.trackTitle)
        assertEquals("Test Artist", payload.artistName)
        assertEquals("Test Album", payload.albumName)
        assertEquals("PENDING", payload.status)
    }
}
