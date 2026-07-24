package com.auraplayer.app

import com.auraplayer.app.lyrics.LrcParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LrcParserTest {

    @Test
    fun parse_standardLrc_parsesTimestampsCorrectly() {
        val sampleLrc = """
            [00:12.50]Hello world
            [01:05.12]Second lyric line
        """.trimIndent()

        val parsed = LrcParser.parse(sampleLrc)
        assertEquals(2, parsed.lines.size)
        assertEquals(12500L, parsed.lines[0].startMs)
        assertEquals("Hello world", parsed.lines[0].content)

        assertEquals(65120L, parsed.lines[1].startMs)
        assertEquals("Second lyric line", parsed.lines[1].content)
        assertFalse(parsed.isEnhancedWordSynced)
    }

    @Test
    fun parse_enhancedWordSyncedLrc_parsesWordTokensCorrectly() {
        val sampleEnhancedLrc = """
            [00:10.00]<00:10.00>Aura <00:10.50>Soundscape <00:11.20>Engine
        """.trimIndent()

        val parsed = LrcParser.parse(sampleEnhancedLrc)
        assertEquals(1, parsed.lines.size)
        assertTrue(parsed.isEnhancedWordSynced)

        val line = parsed.lines[0]
        assertEquals(3, line.wordTokens.size)
        assertEquals("Aura ", line.wordTokens[0].word)
        assertEquals(10000L, line.wordTokens[0].startMs)

        assertEquals("Soundscape ", line.wordTokens[1].word)
        assertEquals(10500L, line.wordTokens[1].startMs)
    }
}
