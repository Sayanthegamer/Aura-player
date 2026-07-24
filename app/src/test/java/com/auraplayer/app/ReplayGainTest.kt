package com.auraplayer.app

import com.auraplayer.app.metadata.MetadataExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReplayGainTest {

    @Test
    fun replayGain_negativeGain_reducesScale() {
        val gainDb = -6.0f
        val peak = 0.95f
        val scale = MetadataExtractor.calculateReplayGainScale(gainDb, peak)

        // 10^(-6/20) approx 0.501
        assertTrue(scale < 1.0f)
        assertTrue(scale > 0.45f)
    }

    @Test
    fun replayGain_positiveGainWithHighPeak_clampsScaleToAntiClippingLimit() {
        val gainDb = +6.0f  // 10^(6/20) approx 1.995
        val peak = 0.90f   // max safe scale = 1 / 0.90 = 1.111

        val scale = MetadataExtractor.calculateReplayGainScale(gainDb, peak)

        // Scale should be clamped to 1.0 / peak = 1.111 to prevent digital clipping
        val expectedMaxScale = 1.0f / peak
        assertEquals(expectedMaxScale, scale, 0.001f)
        assertTrue(scale * peak <= 1.0001f)
    }

    @Test
    fun replayGain_nullGain_returnsUnityScale() {
        val scale = MetadataExtractor.calculateReplayGainScale(null, 0.95f)
        assertEquals(1.0f, scale, 0.001f)
    }
}
