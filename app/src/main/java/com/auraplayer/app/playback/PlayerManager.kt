package com.auraplayer.app.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerManager(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            /* handleAudioFocus = */ true
        )
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_LOCAL)
        .build()

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var positionUpdateJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startPositionUpdates()
                } else {
                    stopPositionUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val duration = if (player.duration != C.TIME_UNSET) player.duration else 0L
                _uiState.update {
                    it.copy(
                        isBuffering = isBuffering,
                        durationMs = duration
                    )
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val title = mediaItem?.mediaMetadata?.title?.toString() ?: "Sample Audio"
                val artist = mediaItem?.mediaMetadata?.artist?.toString() ?: "Aura Artist"
                val album = mediaItem?.mediaMetadata?.albumTitle?.toString() ?: "Aura Album"

                _uiState.update {
                    it.copy(
                        currentTrack = TrackMetadata(
                            id = mediaItem?.mediaId ?: "default",
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = if (player.duration != C.TIME_UNSET) player.duration else 0L,
                            codec = "FLAC",
                            sampleRate = 96000,
                            bitDepth = 24,
                            bitrateKbps = 1411
                        )
                    )
                }
            }
        })
    }

    fun playTrack(mediaUri: String, title: String = "Aura Soundscape", artist: String = "Aura Audio Engine") {
        val mediaItem = MediaItem.Builder()
            .setUri(mediaUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle("Hi-Res Audio Collection")
                    .build()
            )
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0)
            }
            player.play()
        }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _uiState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun setVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0f, 1f)
        player.volume = clampedVolume
        _uiState.update { it.copy(volume = clampedVolume) }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = scope.launch {
            while (true) {
                if (player.isPlaying) {
                    val currentPos = player.currentPosition
                    val duration = if (player.duration != C.TIME_UNSET) player.duration else 0L
                    _uiState.update {
                        it.copy(
                            currentPositionMs = currentPos,
                            durationMs = duration
                        )
                    }
                }
                delay(200)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun release() {
        stopPositionUpdates()
        player.release()
    }
}
