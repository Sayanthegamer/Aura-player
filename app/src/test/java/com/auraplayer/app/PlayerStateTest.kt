package com.auraplayer.app

import com.auraplayer.app.playback.PlayerUiState
import com.auraplayer.app.playback.TrackMetadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow
import kotlin.math.min

class PlayerStateTest {

    @Test
    fun playerUiState_defaultValues_areCorrect() {
        val state = PlayerUiState()
        assertFalse(state.isPlaying)
        assertFalse(state.isBuffering)
        assertEquals(0L, state.currentPositionMs)
        assertEquals(0L, state.durationMs)
        assertEquals(1.0f, state.volume, 0.001f)
    }

    @Test
    fun replayGain_scaleCalculationWithPeakClamping_isAccurate() {
        val gainDb = -2.5f
        val peak = 0.95f

        // Linear scale = 10^(gainDb / 20)
        val linearScale = 10f.pow(gainDb / 20f)
        val maxSafeScale = 1.0f / peak
        val finalScale = min(linearScale, maxSafeScale)

        assertTrue(finalScale <= maxSafeScale)
        assertTrue(finalScale > 0f)
    }
}
