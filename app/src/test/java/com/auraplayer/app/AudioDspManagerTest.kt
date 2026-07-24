package com.auraplayer.app

import com.auraplayer.app.audio.AudioDspState
import com.auraplayer.app.audio.EqBand
import com.auraplayer.app.audio.EqPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioDspManagerTest {

    @Test
    fun testDefault16BandsInitialization() {
        val bands = AudioDspState.DEFAULT_16_BANDS
        assertEquals(16, bands.size)
        assertEquals(31, bands[0].centerFreqHz)
        assertEquals(16000, bands.last().centerFreqHz)
        assertTrue(bands.all { it.gainDb == 0f })
    }

    @Test
    fun testPresetGainCalculations() {
        val rockBands = AudioDspState.getPresetBands(EqPreset.ROCK)
        assertEquals(16, rockBands.size)
        // Bass frequencies should be boosted
        val lowestBand = rockBands.first { it.centerFreqHz == 31 }
        assertEquals(4.5f, lowestBand.gainDb, 0.01f)

        val jazzBands = AudioDspState.getPresetBands(EqPreset.JAZZ)
        val jazzLowest = jazzBands.first { it.centerFreqHz == 31 }
        assertEquals(3.0f, jazzLowest.gainDb, 0.01f)
    }

    @Test
    fun testBandGainClamping() {
        val band = EqBand(index = 0, centerFreqHz = 100, gainDb = 0f, minGainDb = -15f, maxGainDb = 15f)
        val overboost = 25f.coerceIn(band.minGainDb, band.maxGainDb)
        assertEquals(15f, overboost, 0.01f)

        val underboost = (-20f).coerceIn(band.minGainDb, band.maxGainDb)
        assertEquals(-15f, underboost, 0.01f)
    }
}
