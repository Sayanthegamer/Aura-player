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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
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
import com.auraplayer.app.ui.LibraryManagerScreen
import com.auraplayer.app.ui.LyricCanvas
import com.auraplayer.app.ui.MiniPlayer
import com.auraplayer.app.ui.PlayerScreen
import com.auraplayer.app.ui.SettingsScreen
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object LibraryManager : Screen()
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
        playerManager = PlayerManager(this, db)
        val audioDspManager = com.auraplayer.app.audio.AudioDspManager(this)

        setContent {
            val settings by settingsPreferences.settingsFlow.collectAsState(initial = AppSettings())
            val darkTheme = when (settings.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            AuraTheme(darkTheme = darkTheme, dynamicColor = settings.dynamicColor) {
                val uiState by playerManager.uiState.collectAsState()
                val dspState by audioDspManager.dspState.collectAsState()
                val tracks by musicRepository.getAllTracks().collectAsState(initial = emptyList())
                val albums by musicRepository.getAllAlbums().collectAsState(initial = emptyList())
                val artists by musicRepository.getAllArtists().collectAsState(initial = emptyList())
                val scanState by musicRepository.scanState.collectAsState()

                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                var showDspBottomSheet by remember { mutableStateOf(false) }
                var sampleLyrics by remember { mutableStateOf(ParsedLyrics()) }
                var isFetchingLyrics by remember { mutableStateOf(false) }
                var manualOffsetMs by remember { mutableStateOf(0L) }
                val coroutineScope = rememberCoroutineScope()

                // Palette extraction
                val context = androidx.compose.ui.platform.LocalContext.current
                var rawDominant by remember { mutableStateOf(Color(0xFF1E1B2E)) }
                var rawAccent   by remember { mutableStateOf(Color(0xFFD0BCFF)) }
                var rawSecondary by remember { mutableStateOf(Color(0xFF381E72)) }

                LaunchedEffect(uiState.currentTrack?.artworkUri) {
                    val artworkUri = uiState.currentTrack?.artworkUri
                    if (!artworkUri.isNullOrEmpty()) {
                        val request = coil.request.ImageRequest.Builder(context)
                            .data(artworkUri)
                            .allowHardware(false)
                            .build()
                        val result = (coil.ImageLoader(context).execute(request) as? coil.request.SuccessResult)?.drawable
                        if (result is android.graphics.drawable.BitmapDrawable) {
                            val bitmap = result.bitmap
                            androidx.palette.graphics.Palette.from(bitmap).generate { palette ->
                                palette?.let { p ->
                                    rawDominant = Color(p.getDominantColor(android.graphics.Color.parseColor("#1E1B2E")))
                                    rawAccent = Color(p.getVibrantColor(p.getLightVibrantColor(android.graphics.Color.parseColor("#D0BCFF"))))
                                    rawSecondary = Color(p.getMutedColor(p.getDarkMutedColor(android.graphics.Color.parseColor("#381E72"))))
                                }
                            }
                        }
                    }
                }

                val animatedDominant by animateColorAsState(
                    targetValue = rawDominant,
                    animationSpec = tween(1000, easing = LinearEasing),
                    label = "dominant"
                )
                val animatedAccent by animateColorAsState(
                    targetValue = rawAccent,
                    animationSpec = tween(1000, easing = LinearEasing),
                    label = "accent"
                )
                val animatedSecondary by animateColorAsState(
                    targetValue = rawSecondary,
                    animationSpec = tween(1000, easing = LinearEasing),
                    label = "secondary"
                )

                LaunchedEffect(playerManager.player.audioSessionId) {
                    val sessionId = playerManager.player.audioSessionId
                    if (sessionId != 0) {
                        audioDspManager.attachToAudioSession(sessionId)
                    }
                }

                LaunchedEffect(uiState.currentTrack?.id, uiState.currentTrack?.embeddedLyrics) {
                    uiState.currentTrack?.let { track ->
                        isFetchingLyrics = true
                        sampleLyrics = lrclibRepository.getLyrics(
                            trackId = track.id,
                            title = track.title,
                            artist = track.artist,
                            durationSeconds = (track.durationMs / 1000.0),
                            embeddedLyrics = track.embeddedLyrics
                        )
                        isFetchingLyrics = false
                    }
                }

                // Permission Launcher for Media Access & System Notifications
                val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val storageGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] == true ||
                            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                    if (storageGranted) {
                        lifecycleScope.launch {
                            if (tracks.isEmpty()) {
                                musicRepository.rescanLibrary(settings.blacklistedFolders)
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    val primaryPermission = permissionsToRequest.first()
                    if (ContextCompat.checkSelfPermission(this@MainActivity, primaryPermission) == PackageManager.PERMISSION_GRANTED) {
                        if (tracks.isEmpty()) {
                            musicRepository.rescanLibrary(settings.blacklistedFolders)
                        }
                    } else {
                        permissionLauncher.launch(permissionsToRequest)
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
                                        artists = artists,
                                        artistScrobbleCounts = emptyMap(),
                                        settings = settings,
                                        onTrackSelect = { track, trackList, index ->
                                            playerManager.playTrackList(trackList, index)
                                        },
                                        onOpenSettings = { currentScreen = Screen.Settings },
                                        onOpenLibraryManager = { currentScreen = Screen.LibraryManager },
                                        onRescanClick = {
                                            lifecycleScope.launch { musicRepository.rescanLibrary(settings.blacklistedFolders) }
                                        },
                                        onUpdateRailSettings = { newOrder, newHidden ->
                                            coroutineScope.launch {
                                                settingsPreferences.setHomeRailOrder(newOrder)
                                                settingsPreferences.setHiddenRails(newHidden)
                                            }
                                        },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                            }
                            is Screen.LibraryManager -> {
                                Scaffold(
                                    bottomBar = {
                                        MiniPlayer(
                                            uiState = uiState,
                                            onPlayPauseToggle = { playerManager.togglePlayPause() },
                                            onExpandPlayer = { currentScreen = Screen.FullPlayer }
                                        )
                                    }
                                ) { innerPadding ->
                                    LibraryManagerScreen(
                                        tracks = tracks,
                                        blacklistedFolders = settings.blacklistedFolders,
                                        onBack = { currentScreen = Screen.Home },
                                        onAddBlacklistedFolder = { folder ->
                                            coroutineScope.launch {
                                                settingsPreferences.addBlacklistedFolder(folder)
                                                musicRepository.rescanLibrary(settings.blacklistedFolders + folder)
                                            }
                                        },
                                        onRemoveBlacklistedFolder = { folder ->
                                            coroutineScope.launch {
                                                settingsPreferences.removeBlacklistedFolder(folder)
                                                musicRepository.rescanLibrary(settings.blacklistedFolders - folder)
                                            }
                                        },
                                        onUpdateTags = { ids, genre, moodTags, bpm ->
                                            coroutineScope.launch {
                                                db.trackDao().updateTrackTags(ids, genre, moodTags, bpm)
                                            }
                                        },
                                        onDeleteTracks = { ids ->
                                            coroutineScope.launch {
                                                db.trackDao().deleteTracksByIds(ids)
                                            }
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
                                    onBack = { currentScreen = Screen.Home },
                                    onPlayPauseToggle = { playerManager.togglePlayPause() },
                                    onSeek = { position -> playerManager.seekTo(position) },
                                    onNextTrack = { playerManager.skipToNext() },
                                    onPrevTrack = { playerManager.skipToPrevious() },
                                    onAlbumArtTap = { currentScreen = Screen.Lyrics },
                                    onOpenAudioDsp = { showDspBottomSheet = true },
                                    onShuffleToggle = { playerManager.toggleShuffle() },
                                    paletteAccent = animatedAccent,
                                    paletteDominant = animatedDominant,
                                    paletteSecondary = animatedSecondary
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
                                    onSeek = { targetMs -> playerManager.seekTo(targetMs) },
                                    isLoading = isFetchingLyrics,
                                    trackTitle = uiState.currentTrack?.title ?: "",
                                    artworkUri = uiState.currentTrack?.artworkUri,
                                    paletteAccent = animatedAccent,
                                    paletteDominant = animatedDominant,
                                    paletteSecondary = animatedSecondary
                                )
                            }
                        }
                    }

                    if (showDspBottomSheet) {
                        com.auraplayer.app.ui.AudioDspBottomSheet(
                            dspState = dspState,
                            onToggleEnabled = { enabled -> audioDspManager.setEnabled(enabled) },
                            onSelectPreset = { preset -> audioDspManager.setPreset(preset) },
                            onBandGainChanged = { bandIdx, gain -> audioDspManager.setBandGain(bandIdx, gain) },
                            onBassBoostChanged = { gain -> audioDspManager.setBassBoost(gain) },
                            onTrebleChanged = { gain -> audioDspManager.setTreble(gain) },
                            onDismissRequest = { showDspBottomSheet = false }
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
