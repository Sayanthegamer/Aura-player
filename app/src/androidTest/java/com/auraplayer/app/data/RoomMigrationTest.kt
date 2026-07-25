package com.auraplayer.app.data

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AuraDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate4To5() {
        // Create database in version 4
        var db = helper.createDatabase(TEST_DB, 4)

        // Insert a sample v4 track record
        db.execSQL(
            """
            INSERT INTO tracks (id, mediaStoreId, title, artistName, albumName, artistId, albumId, durationMs, filePath, uriString, mimeType, codec, bitrate, sampleRate, bitDepth, dateAdded, bpm, genre, moodTags, lastPlayedTimestamp)
            VALUES (1, 100, 'Test Track', 'Test Artist', 'Test Album', 10, 20, 180000, '/path/1.mp3', 'content://media/1', 'audio/mp3', 'MP3', 320, 44100, 16, 1700000000, 0, 'Pop', '', 0)
            """.trimIndent()
        )
        db.close()

        // Run MIGRATION_4_5 and validate schema version 5
        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5)
        db.close()
    }
}
