package com.auraplayer.app.domain

import com.auraplayer.app.data.TrackEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class DuplicateDetectionTest {

    @Test
    fun findDuplicates_detectsMatchingTitleAndArtistWithDifferentFilePaths() {
        val track1 = createTestTrack(id = 1, title = "Song A", artist = "Artist X", path = "/sdcard/Music/SongA.mp3")
        val track2 = createTestTrack(id = 2, title = "Song A", artist = "Artist X", path = "/sdcard/Downloads/SongA_copy.mp3")
        val track3 = createTestTrack(id = 3, title = "Song B", artist = "Artist X", path = "/sdcard/Music/SongB.mp3")

        val duplicates = DuplicateDetector.findDuplicates(listOf(track1, track2, track3))

        assertEquals(1, duplicates.size)
        assertEquals("Song A", duplicates[0].title)
        assertEquals(2, duplicates[0].tracks.size)
    }

    @Test
    fun findDuplicates_returnsEmptyIfPathsAreIdenticalOrUniqueTracks() {
        val track1 = createTestTrack(id = 1, title = "Song A", artist = "Artist X", path = "/sdcard/Music/SongA.mp3")
        val track2 = createTestTrack(id = 2, title = "Song B", artist = "Artist Y", path = "/sdcard/Music/SongB.mp3")

        val duplicates = DuplicateDetector.findDuplicates(listOf(track1, track2))
        assertEquals(0, duplicates.size)
    }

    private fun createTestTrack(id: Long, title: String, artist: String, path: String): TrackEntity {
        return TrackEntity(
            id = id,
            mediaStoreId = id,
            title = title,
            artistName = artist,
            albumName = "Album",
            artistId = 100L,
            albumId = 200L,
            durationMs = 180000L,
            filePath = path,
            uriString = "content://media/$id",
            albumArtUri = null,
            mimeType = "audio/mp3"
        )
    }
}
