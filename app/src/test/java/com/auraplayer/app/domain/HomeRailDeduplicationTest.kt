package com.auraplayer.app.domain

import com.auraplayer.app.data.TrackEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HomeRailDeduplicationTest {

    @Test
    fun buildContinueListeningHero_prefersRecentlyPlayedOverAdded() {
        val now = System.currentTimeMillis()
        val playedRecently = createTrack(id = 1, title = "Played Recently", lastPlayed = now - 100000)
        val neverPlayed = createTrack(id = 2, title = "Never Played", lastPlayed = 0)

        val hero = HomeRailBuilder.buildContinueListeningHero(listOf(playedRecently, neverPlayed), now)

        assertNotNull(hero)
        assertEquals("Played Recently", hero!!.track.title)
        assertEquals(true, hero.isRecentPlayed)
    }

    @Test
    fun buildMadeForYouCarousel_deduplicatesByArtist() {
        val seed = createTrack(id = 1, title = "Seed", artist = "Artist A", genre = "Rock")
        val track2 = createTrack(id = 2, title = "Candidate 1", artist = "Artist B", genre = "Rock")
        val track3 = createTrack(id = 3, title = "Candidate 2", artist = "Artist B", genre = "Rock")
        val track4 = createTrack(id = 4, title = "Candidate 3", artist = "Artist C", genre = "Rock")

        val result = HomeRailBuilder.buildMadeForYouCarousel(
            tracks = listOf(seed, track2, track3, track4),
            seedTrack = seed
        )

        // Candidate 1 and Candidate 2 share Artist B; only one should be included
        val artistNames = result.map { it.artistName }
        assertEquals(artistNames.distinct().size, artistNames.size)
    }

    @Test
    fun buildRecentlyAdded_fallsBackToTop20WhenClustered() {
        val now = System.currentTimeMillis()
        // Create 10 tracks with dateAdded older than 14 days
        val oldTracks = (1..10).map { i ->
            createTrack(id = i.toLong(), title = "Track $i", dateAdded = 1000L)
        }

        val result = HomeRailBuilder.buildRecentlyAdded(oldTracks, now)

        // Fallback returns top 20 (or all 10 available)
        assertEquals(10, result.size)
    }

    private fun createTrack(
        id: Long,
        title: String,
        artist: String = "Artist",
        genre: String = "Pop",
        lastPlayed: Long = 0L,
        dateAdded: Long = System.currentTimeMillis() / 1000
    ): TrackEntity {
        return TrackEntity(
            id = id,
            mediaStoreId = id,
            title = title,
            artistName = artist,
            albumName = "Album",
            artistId = 100L,
            albumId = 200L,
            durationMs = 180000L,
            filePath = "/path/$id.mp3",
            uriString = "content://media/$id",
            albumArtUri = null,
            mimeType = "audio/mp3",
            genre = genre,
            lastPlayedTimestamp = lastPlayed,
            dateAdded = dateAdded
        )
    }
}
