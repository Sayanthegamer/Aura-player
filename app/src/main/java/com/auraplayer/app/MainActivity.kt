package com.auraplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.auraplayer.app.data.AuraDatabase
import com.auraplayer.app.lyrics.ParsedLyrics
import com.auraplayer.app.playback.PlayerManager
import com.auraplayer.app.repository.LrclibRepository
import com.auraplayer.app.ui.AuraTheme
import com.auraplayer.app.ui.LyricCanvas
import com.auraplayer.app.ui.PlayerScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var playerManager: PlayerManager
    private lateinit var lrclibRepository: LrclibRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AuraDatabase.getInstance(this)
        lrclibRepository = LrclibRepository(db.lyricDao())
        playerManager = PlayerManager(this)

        setContent {
            AuraTheme {
                val uiState by playerManager.uiState.collectAsState()
                var showLyrics by remember { mutableStateOf(false) }
                var sampleLyrics by remember { mutableStateOf(ParsedLyrics()) }
                var manualOffsetMs by remember { mutableStateOf(0L) }

                Crossfade(targetState = showLyrics, label = "screenCrossfade") { isLyricsVisible ->
                    if (isLyricsVisible) {
                        LyricCanvas(
                            lyrics = sampleLyrics,
                            currentPositionProvider = { uiState.currentPositionMs },
                            manualOffsetMs = manualOffsetMs,
                            onOffsetChange = { newOffset ->
                                manualOffsetMs = newOffset
                                uiState.currentTrack?.let { track ->
                                    lifecycleScope.launch {
                                        lrclibRepository.updateLyricOffset(track.id, newOffset)
                                    }
                                }
                            },
                            onClose = { showLyrics = false }
                        )
                    } else {
                        PlayerScreen(
                            uiState = uiState,
                            onPlayPauseToggle = { playerManager.togglePlayPause() },
                            onSeek = { position -> playerManager.seekTo(position) },
                            onAlbumArtTap = {
                                showLyrics = true
                                uiState.currentTrack?.let { track ->
                                    lifecycleScope.launch {
                                        sampleLyrics = lrclibRepository.getLyrics(
                                            trackId = track.id,
                                            title = track.title,
                                            artist = track.artist
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}
