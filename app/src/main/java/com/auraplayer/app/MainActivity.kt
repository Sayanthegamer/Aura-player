package com.auraplayer.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.auraplayer.app.data.AppSettings
import com.auraplayer.app.data.AuraDatabase
import com.auraplayer.app.data.MediaScanner
import com.auraplayer.app.data.SettingsPreferences
import com.auraplayer.app.data.ThemeMode
import com.auraplayer.app.lyrics.ParsedLyrics
import com.auraplayer.app.playback.PlayerManager
import com.auraplayer.app.repository.LrclibRepository
import com.auraplayer.app.repository.MusicRepository
import com.auraplayer.app.ui.AuraTheme
import com.auraplayer.app.ui.HomeScreen
import com.auraplayer.app.ui.LyricCanvas
import com.auraplayer.app.ui.MiniPlayer
import com.auraplayer.app.ui.PlayerScreen
import com.auraplayer.app.ui.SettingsScreen
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Settings : Screen()
    object FullPlayer : Screen()
    object Lyrics : Screen()
}

class MainActivity : ComponentActivity() {

    private lateinit var playerManager: PlayerManager
    private lateinit var lrclibRepository: LrclibRepository
    private lateinit var musicRepository: MusicRepository
    private lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AuraDatabase.getInstance(this)
        val mediaScanner = MediaScanner(this, db)
        musicRepository = MusicRepository(db, mediaScanner)
        settingsPreferences = SettingsPreferences(this)
        lrclibRepository = LrclibRepository(db.lyricDao())
        playerManager = PlayerManager(this)

        setContent {
            val settings by settingsPreferences.settingsFlow.collectAsState(initial = AppSettings())
            val darkTheme = when (settings.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            AuraTheme(darkTheme = darkTheme, dynamicColor = settings.dynamicColor) {
                val uiState by playerManager.uiState.collectAsState()
                val tracks by musicRepository.getAllTracks().collectAsState(initial = emptyList())
                val albums by musicRepository.getAllAlbums().collectAsState(initial = emptyList())
                val artists by musicRepository.getAllArtists().collectAsState(initial = emptyList())
                val scanState by musicRepository.scanState.collectAsState()

                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                var sampleLyrics by remember { mutableStateOf(ParsedLyrics()) }
                var isFetchingLyrics by remember { mutableStateOf(false) }
                var manualOffsetMs by remember { mutableStateOf(0L) }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(uiState.currentTrack?.id) {
                    uiState.currentTrack?.let { track ->
                        isFetchingLyrics = true
                        sampleLyrics = lrclibRepository.getLyrics(
                            trackId = track.id,
                            title = track.title,
                            artist = track.artist
                        )
                        isFetchingLyrics = false
                    }
                }

                // Permission Launcher
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        lifecycleScope.launch {
                            musicRepository.rescanLibrary(settings.blacklistedFolders)
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED) {
                        if (tracks.isEmpty()) {
                            musicRepository.rescanLibrary(settings.blacklistedFolders)
                        }
                    } else {
                        permissionLauncher.launch(permission)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                        when (screen) {
                            is Screen.Home -> {
                                Scaffold(
                                    bottomBar = {
                                        MiniPlayer(
                                            uiState = uiState,
                                            onPlayPauseToggle = { playerManager.togglePlayPause() },
                                            onExpandPlayer = { currentScreen = Screen.FullPlayer }
                                        )
                                    }
                                ) { innerPadding ->
                                    HomeScreen(
                                        tracks = tracks,
                                        albums = albums,
                                        artists = artists,
                                        onTrackSelect = { track, trackList, index ->
                                            playerManager.playTrackList(trackList, index)
                                        },
                                        onOpenSettings = { currentScreen = Screen.Settings },
                                        onRescanClick = {
                                            lifecycleScope.launch { musicRepository.rescanLibrary(settings.blacklistedFolders) }
                                        },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                            }
                            is Screen.Settings -> {
                                SettingsScreen(
                                    settings = settings,
                                    scanState = scanState,
                                    onBack = { currentScreen = Screen.Home },
                                    onThemeModeChange = { mode ->
                                        coroutineScope.launch { settingsPreferences.setThemeMode(mode) }
                                    },
                                    onDynamicColorToggle = { enabled ->
                                        coroutineScope.launch { settingsPreferences.setDynamicColor(enabled) }
                                    },
                                    onReplayGainLufsChange = { lufs ->
                                        coroutineScope.launch { settingsPreferences.setReplayGainTargetLufs(lufs) }
                                    },
                                    onAntiClippingToggle = { enabled ->
                                        coroutineScope.launch { settingsPreferences.setAntiClippingEnabled(enabled) }
                                    },
                                    onAddBlacklistFolder = { folder ->
                                        coroutineScope.launch {
                                            settingsPreferences.addBlacklistedFolder(folder)
                                            musicRepository.rescanLibrary(settings.blacklistedFolders + folder)
                                        }
                                    },
                                    onRemoveBlacklistFolder = { folder ->
                                        coroutineScope.launch {
                                            settingsPreferences.removeBlacklistedFolder(folder)
                                            musicRepository.rescanLibrary(settings.blacklistedFolders - folder)
                                        }
                                    },
                                    onRescanClick = {
                                        lifecycleScope.launch { musicRepository.rescanLibrary(settings.blacklistedFolders) }
                                    }
                                )
                            }
                            is Screen.FullPlayer -> {
                                PlayerScreen(
                                    uiState = uiState,
                                    onPlayPauseToggle = { playerManager.togglePlayPause() },
                                    onSeek = { position -> playerManager.seekTo(position) },
                                    onAlbumArtTap = {
                                        currentScreen = Screen.Lyrics
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
                            is Screen.Lyrics -> {
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
                                    onClose = { currentScreen = Screen.FullPlayer },
                                    isLoading = isFetchingLyrics,
                                    trackTitle = uiState.currentTrack?.title ?: ""
                                )
                            }
                        }
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
