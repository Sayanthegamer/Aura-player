package com.auraplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.auraplayer.app.playback.PlayerManager
import com.auraplayer.app.ui.PlayerScreen
import com.auraplayer.app.ui.AuraTheme

class MainActivity : ComponentActivity() {

    private lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        playerManager = PlayerManager(this)

        setContent {
            AuraTheme {
                val uiState by playerManager.uiState.collectAsState()

                PlayerScreen(
                    uiState = uiState,
                    onPlayPauseToggle = { playerManager.togglePlayPause() },
                    onSeek = { position -> playerManager.seekTo(position) }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}
