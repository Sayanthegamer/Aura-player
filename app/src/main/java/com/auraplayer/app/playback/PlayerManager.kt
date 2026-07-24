package com.auraplayer.app.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.auraplayer.app.data.TrackEntity
import com.auraplayer.app.metadata.MetadataExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

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

    private val activeTrackMap = ConcurrentHashMap<String, TrackEntity>()
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
                val mediaId = mediaItem?.mediaId
                val trackEntity = mediaId?.let { activeTrackMap[it] }

                val title = mediaItem?.mediaMetadata?.title?.toString() ?: trackEntity?.title ?: "Unknown Track"
                val artist = mediaItem?.mediaMetadata?.artist?.toString() ?: trackEntity?.artistName ?: "Unknown Artist"
                val album = mediaItem?.mediaMetadata?.albumTitle?.toString() ?: trackEntity?.albumName ?: "Unknown Album"

                val codec = trackEntity?.codec ?: "MP3"
                val sampleRate = if ((trackEntity?.sampleRate ?: 0) > 0) trackEntity!!.sampleRate else 44100
                val bitDepth = if ((trackEntity?.bitDepth ?: 0) > 0) trackEntity!!.bitDepth else if (codec == "FLAC" || codec == "WAV") 24 else 16
                val bitrateKbps = if ((trackEntity?.bitrate ?: 0) > 0) trackEntity!!.bitrate else 320
                val gainDb = trackEntity?.replayGainTrackGain
                val peak = trackEntity?.replayGainTrackPeak

                val artworkUri = mediaItem?.mediaMetadata?.artworkUri?.toString() ?: trackEntity?.albumArtUri

                applyReplayGain(gainDb, peak)

                _uiState.update {
                    it.copy(
                        currentTrack = TrackMetadata(
                            id = mediaId ?: "default",
                            title = title,
                            artist = artist,
                            album = album,
                            durationMs = if (player.duration != C.TIME_UNSET) player.duration else (trackEntity?.durationMs ?: 0L),
                            artworkUri = artworkUri,
                            codec = codec,
                            sampleRate = sampleRate,
                            bitDepth = bitDepth,
                            bitrateKbps = bitrateKbps,
                            replayGainDb = gainDb,
                            replayGainPeak = peak
                        )
                    )
                }
            }
        })
    }

    fun applyReplayGain(gainDb: Float?, peak: Float?) {
        val scale = MetadataExtractor.calculateReplayGainScale(gainDb, peak)
        player.volume = scale
        _uiState.update { it.copy(volume = scale) }
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

    fun playTrackList(tracks: List<TrackEntity>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        activeTrackMap.clear()
        val mediaItems = tracks.map { track ->
            activeTrackMap[track.id.toString()] = track
            MediaItem.Builder()
                .setMediaId(track.id.toString())
                .setUri(track.uriString)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artistName)
                        .setAlbumTitle(track.albumName)
                        .setArtworkUri(track.albumArtUri?.let { android.net.Uri.parse(it) })
                        .build()
                )
                .build()
        }
        player.setMediaItems(mediaItems, startIndex, 0L)
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

    fun skipToNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }

    fun skipToPrevious() {
        if (player.currentPosition > 3000L) {
            player.seekTo(0)
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        } else {
            player.seekTo(0)
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
