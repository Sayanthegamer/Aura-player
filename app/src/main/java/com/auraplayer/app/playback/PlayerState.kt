package com.auraplayer.app.playback

data class TrackMetadata(
    val id: String = "",
    val title: String = "Unknown Title",
    val artist: String = "Unknown Artist",
    val album: String = "Unknown Album",
    val durationMs: Long = 0L,
    val artworkUri: String? = null,
    val filePath: String = "",
    val embeddedLyrics: String? = null,
    val codec: String = "FLAC",
    val sampleRate: Int = 44100,
    val bitDepth: Int = 24,
    val bitrateKbps: Int = 960,
    val replayGainDb: Float? = null,
    val replayGainPeak: Float? = null
)

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentTrack: TrackMetadata? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val volume: Float = 1.0f,
    val isBuffering: Boolean = false
)
