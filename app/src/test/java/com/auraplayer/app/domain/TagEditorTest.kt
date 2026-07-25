package com.auraplayer.app.domain

import com.auraplayer.app.data.TrackEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class TagEditorTest {

    @Test
    fun applyBulkTags_updatesSpecifiedTracksOnly() {
        val t1 = createTrack(id = 1, genre = "Pop", mood = "", bpm = 0)
        val t2 = createTrack(id = 2, genre = "Rock", mood = "", bpm = 0)
        val t3 = createTrack(id = 3, genre = "Jazz", mood = "", bpm = 0)

        val updated = TagUpdateCalculator.applyBulkTags(
            tracks = listOf(t1, t2, t3),
            targetTrackIds = setOf(1L, 3L),
            newGenre = "Electronic",
            newMoodTags = "Energetic, Workout",
            newBpm = 128
        )

        assertEquals("Electronic", updated[0].genre)
        assertEquals("Energetic, Workout", updated[0].moodTags)
        assertEquals(128, updated[0].bpm)

        // Track 2 remains untouched
        assertEquals("Rock", updated[1].genre)
        assertEquals(0, updated[1].bpm)

        assertEquals("Electronic", updated[2].genre)
    }

    private fun createTrack(id: Long, genre: String, mood: String, bpm: Int): TrackEntity {
        return TrackEntity(
            id = id,
            mediaStoreId = id,
            title = "Track $id",
            artistName = "Artist",
            albumName = "Album",
            artistId = 100L,
            albumId = 200L,
            durationMs = 180000L,
            filePath = "/path/$id.mp3",
            uriString = "content://media/$id",
            albumArtUri = null,
            mimeType = "audio/mp3",
            genre = genre,
            moodTags = mood,
            bpm = bpm
        )
    }
}
