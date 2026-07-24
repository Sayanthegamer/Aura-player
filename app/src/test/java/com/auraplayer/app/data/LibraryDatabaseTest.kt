package com.auraplayer.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LibraryDatabaseTest {

    @Test
    fun testTrackEntityCreation() {
        val track = TrackEntity(
            id = 1L,
            mediaStoreId = 100L,
            title = "Midnight City",
            artistName = "M83",
            albumName = "Hurry Up, We're Dreaming",
            artistId = 10L,
            albumId = 20L,
            durationMs = 243000L,
            filePath = "/storage/emulated/0/Music/midnight.flac",
            uriString = "content://media/external/audio/media/100",
            albumArtUri = "content://media/external/audio/albumart/20",
            mimeType = "audio/flac",
            bitrate = 1411,
            sampleRate = 44100,
            replayGainTrackGain = -2.5f,
            replayGainTrackPeak = 0.98f,
            dateAdded = 1600000000L
        )

        assertEquals("Midnight City", track.title)
        assertEquals("M83", track.artistName)
        assertEquals("Hurry Up, We're Dreaming", track.albumName)
        assertEquals(-2.5f, track.replayGainTrackGain!!, 0.01f)
    }

    @Test
    fun testAlbumEntityCreation() {
        val album = AlbumEntity(
            id = 20L,
            title = "Hurry Up, We're Dreaming",
            artistName = "M83",
            trackCount = 22,
            albumArtUri = "content://media/external/audio/albumart/20"
        )

        assertEquals("Hurry Up, We're Dreaming", album.title)
        assertEquals(22, album.trackCount)
    }

    @Test
    fun testArtistEntityCreation() {
        val artist = ArtistEntity(
            id = 10L,
            name = "M83",
            trackCount = 45,
            albumCount = 4
        )

        assertEquals("M83", artist.name)
        assertEquals(45, artist.trackCount)
        assertEquals(4, artist.albumCount)
    }
}
