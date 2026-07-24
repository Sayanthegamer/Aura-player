package com.auraplayer.app.audio

import android.content.Context
import android.media.audiofx.DynamicsProcessing
import android.media.audiofx.Equalizer
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EqBand(
    val index: Int,
    val centerFreqHz: Int,
    val gainDb: Float,
    val minGainDb: Float = -15f,
    val maxGainDb: Float = 15f
)

enum class EqPreset(val displayName: String) {
    FLAT("Flat"),
    ROCK("Rock"),
    JAZZ("Jazz"),
    BASS_BOOST("Bass Boost"),
    CUSTOM("Custom")
}

data class AudioDspState(
    val isEnabled: Boolean = false,
    val isDynamicsProcessingSupported: Boolean = true,
    val preset: EqPreset = EqPreset.FLAT,
    val bands: List<EqBand> = DEFAULT_16_BANDS,
    val bassBoostGainDb: Float = 0f,
    val trebleGainDb: Float = 0f,
    val limiterThresholdDb: Float = 0f,
    val isLimiterEnabled: Boolean = true
) {
    companion object {
        val FREQUENCIES = listOf(
            31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000,
            45, 90, 180, 350, 750, 1500
        ).sorted()

        val DEFAULT_16_BANDS = FREQUENCIES.mapIndexed { idx, freq ->
            EqBand(index = idx, centerFreqHz = freq, gainDb = 0f)
        }

        fun getPresetBands(preset: EqPreset): List<EqBand> {
            return when (preset) {
                EqPreset.FLAT -> DEFAULT_16_BANDS
                EqPreset.ROCK -> FREQUENCIES.mapIndexed { idx, freq ->
                    val gain = when {
                        freq <= 125 -> 4.5f
                        freq <= 500 -> 1.5f
                        freq <= 2000 -> -1.0f
                        freq <= 8000 -> 3.0f
                        else -> 5.0f
                    }
                    EqBand(index = idx, centerFreqHz = freq, gainDb = gain)
                }
                EqPreset.JAZZ -> FREQUENCIES.mapIndexed { idx, freq ->
                    val gain = when {
                        freq <= 125 -> 3.0f
                        freq <= 500 -> 1.5f
                        freq <= 2000 -> 0.0f
                        freq <= 8000 -> 2.5f
                        else -> 3.5f
                    }
                    EqBand(index = idx, centerFreqHz = freq, gainDb = gain)
                }
                EqPreset.BASS_BOOST -> FREQUENCIES.mapIndexed { idx, freq ->
                    val gain = when {
                        freq <= 125 -> 7.0f
                        freq <= 250 -> 4.0f
                        freq <= 500 -> 2.0f
                        else -> 0.0f
                    }
                    EqBand(index = idx, centerFreqHz = freq, gainDb = gain)
                }
                EqPreset.CUSTOM -> DEFAULT_16_BANDS
            }
        }
    }
}

class AudioDspManager(
    private val context: Context
) {
    private val TAG = "AudioDspManager"

    private val _dspState = MutableStateFlow(AudioDspState())
    val dspState: StateFlow<AudioDspState> = _dspState.asStateFlow()

    private var dynamicsProcessing: DynamicsProcessing? = null
    private var legacyEqualizer: Equalizer? = null
    private var currentAudioSessionId: Int = 0

    fun attachToAudioSession(sessionId: Int) {
        if (sessionId == 0 || sessionId == currentAudioSessionId) return
        currentAudioSessionId = sessionId
        releaseEffects()

        if (_dspState.value.isEnabled) {
            initAudioEffects(sessionId)
        }
    }

    fun setEnabled(enabled: Boolean) {
        _dspState.update { it.copy(isEnabled = enabled) }
        if (enabled) {
            if (currentAudioSessionId != 0) {
                initAudioEffects(currentAudioSessionId)
            }
        } else {
            releaseEffects()
        }
    }

    private fun initAudioEffects(sessionId: Int) {
        releaseEffects()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val bands = _dspState.value.bands
                val dpBuilder = DynamicsProcessing.Config.Builder(
                    DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION,
                    2, // Channel count
                    true, bands.size, // PreEQ
                    false, 0, // MBC
                    true, bands.size, // PostEQ
                    _dspState.value.isLimiterEnabled
                )

                val dp = DynamicsProcessing(0, sessionId, dpBuilder.build())
                dp.enabled = true
                dynamicsProcessing = dp
                _dspState.update { it.copy(isDynamicsProcessingSupported = true) }
                applyDynamicsProcessingGains()
                Log.d(TAG, "DynamicsProcessing initialized successfully for session $sessionId")
                return
            } catch (e: Exception) {
                Log.w(TAG, "DynamicsProcessing initialization failed, falling back to legacy Equalizer", e)
                _dspState.update { it.copy(isDynamicsProcessingSupported = false) }
            }
        } else {
            _dspState.update { it.copy(isDynamicsProcessingSupported = false) }
        }

        // Fallback to 5-band legacy Equalizer
        try {
            val eq = Equalizer(0, sessionId)
            eq.enabled = true
            legacyEqualizer = eq
            applyLegacyEqualizerGains()
            Log.d(TAG, "Legacy Equalizer initialized successfully for session $sessionId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize legacy Equalizer", e)
        }
    }

    fun setPreset(preset: EqPreset) {
        val newBands = if (preset == EqPreset.CUSTOM) {
            _dspState.value.bands
        } else {
            AudioDspState.getPresetBands(preset)
        }
        _dspState.update {
            it.copy(
                preset = preset,
                bands = newBands
            )
        }
        applyAllGains()
    }

    fun setBandGain(bandIndex: Int, gainDb: Float) {
        val updatedBands = _dspState.value.bands.map { band ->
            if (band.index == bandIndex) band.copy(gainDb = gainDb.coerceIn(band.minGainDb, band.maxGainDb))
            else band
        }
        _dspState.update {
            it.copy(
                preset = EqPreset.CUSTOM,
                bands = updatedBands
            )
        }
        applyAllGains()
    }

    fun setBassBoost(gainDb: Float) {
        val clamped = gainDb.coerceIn(0f, 10f)
        _dspState.update { it.copy(bassBoostGainDb = clamped) }
        applyAllGains()
    }

    fun setTreble(gainDb: Float) {
        val clamped = gainDb.coerceIn(0f, 10f)
        _dspState.update { it.copy(trebleGainDb = clamped) }
        applyAllGains()
    }

    private fun applyAllGains() {
        if (!_dspState.value.isEnabled) return
        if (dynamicsProcessing != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            applyDynamicsProcessingGains()
        } else if (legacyEqualizer != null) {
            applyLegacyEqualizerGains()
        }
    }

    private fun applyDynamicsProcessingGains() {
        val dp = dynamicsProcessing ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return

        val state = _dspState.value
        val bassExtra = state.bassBoostGainDb
        val trebleExtra = state.trebleGainDb

        state.bands.forEachIndexed { idx, band ->
            var finalGain = band.gainDb
            if (band.centerFreqHz <= 250) {
                finalGain += bassExtra
            } else if (band.centerFreqHz >= 4000) {
                finalGain += trebleExtra
            }
            finalGain = finalGain.coerceIn(-15f, 15f)

            try {
                // Apply gain across both stereo channels (0 and 1)
                dp.setPreEqBandAllChannelsTo(idx, DynamicsProcessing.EqBand(true, band.centerFreqHz.toFloat(), finalGain))
            } catch (e: Exception) {
                Log.e(TAG, "Error setting DynamicsProcessing band $idx", e)
            }
        }
    }

    private fun applyLegacyEqualizerGains() {
        val eq = legacyEqualizer ?: return
        val numBands = eq.numberOfBands.toInt()
        val state = _dspState.value

        for (b in 0 until numBands) {
            val centerFreq = eq.getCenterFreq(b.toShort()) / 1000 // Convert mHz to Hz
            // Find closest band in state
            val closestBand = state.bands.minByOrNull { kotlin.math.abs(it.centerFreqHz - centerFreq) }
            var gain = closestBand?.gainDb ?: 0f
            if (centerFreq <= 250) gain += state.bassBoostGainDb
            if (centerFreq >= 4000) gain += state.trebleGainDb
            
            val millibels = (gain.coerceIn(-15f, 15f) * 100).toInt().toShort()
            try {
                eq.setBandLevel(b.toShort(), millibels)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting legacy EQ band $b", e)
            }
        }
    }

    fun releaseEffects() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dynamicsProcessing?.enabled = false
            dynamicsProcessing?.release()
            dynamicsProcessing = null
        }
        legacyEqualizer?.enabled = false
        legacyEqualizer?.release()
        legacyEqualizer = null
    }
}
