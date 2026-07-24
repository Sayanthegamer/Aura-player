package com.auraplayer.app.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaScannerTest {

    @Test
    fun testBlacklistPathMatching() {
        val blacklistedFolders = setOf("WhatsApp/Media", "Telegram", "/storage/emulated/0/Recordings")

        val path1 = "/storage/emulated/0/WhatsApp/Media/WhatsApp Audio/AUD-20260724.mp3"
        val path2 = "/storage/emulated/0/Telegram/Telegram Audio/song.mp3"
        val path3 = "/storage/emulated/0/Recordings/voice_note.m4a"
        val path4 = "/storage/emulated/0/Music/Flac/track.flac"

        assertTrue(isPathBlacklisted(path1, blacklistedFolders))
        assertTrue(isPathBlacklisted(path2, blacklistedFolders))
        assertTrue(isPathBlacklisted(path3, blacklistedFolders))
        assertFalse(isPathBlacklisted(path4, blacklistedFolders))
    }

    private fun isPathBlacklisted(filePath: String, blacklistedFolders: Set<String>): Boolean {
        return blacklistedFolders.any { folder ->
            folder.isNotBlank() && filePath.lowercase().contains(folder.lowercase())
        }
    }
}
