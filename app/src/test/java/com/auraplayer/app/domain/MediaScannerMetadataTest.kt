package com.auraplayer.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class MediaScannerMetadataTest {

    @Test
    fun parseGenre_normalGenre_returnsTrimmed() {
        val result = MetadataTagParser.parseGenre("  Rock  ")
        assertEquals("Rock", result)
    }

    @Test
    fun parseGenre_unknownOrOther_returnsEmpty() {
        assertEquals("", MetadataTagParser.parseGenre("Unknown"))
        assertEquals("", MetadataTagParser.parseGenre("OTHER"))
        assertEquals("", MetadataTagParser.parseGenre(null))
        assertEquals("", MetadataTagParser.parseGenre("   "))
    }

    @Test
    fun parseGenre_id3NumericCode_convertsToName() {
        assertEquals("Pop", MetadataTagParser.parseGenre("(13)"))
        assertEquals("Jazz", MetadataTagParser.parseGenre("(8)"))
    }

    @Test
    fun parseBpm_validBpmString_returnsInteger() {
        assertEquals(128, MetadataTagParser.parseBpm("128"))
        assertEquals(120, MetadataTagParser.parseBpm("120.5 BPM"))
        assertEquals(0, MetadataTagParser.parseBpm("invalid"))
        assertEquals(0, MetadataTagParser.parseBpm(null))
    }
}
