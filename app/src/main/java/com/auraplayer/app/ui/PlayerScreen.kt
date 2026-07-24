package com.auraplayer.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auraplayer.app.playback.PlayerUiState

val DarkBg = Color(0xFF0F0E17)
val CardBg = Color(0xFF1E1C2A)
val AccentOrange = Color(0xFFFF8906)
val AccentPink = Color(0xFFE53170)
val TextMuted = Color(0xFFA7A9BE)
val ChipBg = Color(0xFF2A283E)

@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onNextTrack: () -> Unit = {},
    onPrevTrack: () -> Unit = {},
    onAlbumArtTap: () -> Unit = {}
) {
    val track = uiState.currentTrack

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1829),
                            DarkBg,
                            Color(0xFF09080E)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AURA PLAYER",
                            color = AccentOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Now Playing",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Surface(
                        color = ChipBg,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Audio Output",
                                tint = AccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hi-Res Audio",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Album Art Canvas Card
                val albumScale by animateFloatAsState(
                    targetValue = if (uiState.isPlaying) 1.0f else 0.94f,
                    animationSpec = tween(durationMillis = 400),
                    label = "albumScale"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                        .scale(albumScale)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { onAlbumArtTap() },
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2E2B44),
                                        Color(0xFF151421)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Album Artwork Placeholder",
                            tint = AccentPink,
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Track Metadata Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = track?.title ?: "Aura Soundscape",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track?.artist ?: "Aura Audio Engine",
                        color = TextMuted,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Audio Quality Chip & ReplayGain Badge
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ChipBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${track?.codec ?: "FLAC"} • ${track?.bitDepth ?: 24}-bit/${(track?.sampleRate ?: 96000) / 1000}kHz",
                                color = AccentOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = Color(0xFF251A2E),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "RG -2.1dB",
                                color = AccentPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar & Timestamps
                Column(modifier = Modifier.fillMaxWidth()) {
                    val currentPosMs = uiState.currentPositionMs
                    val totalDurationMs = if (uiState.durationMs > 0) uiState.durationMs else 180000L
                    val sliderValue = (currentPosMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)

                    Slider(
                        value = sliderValue,
                        onValueChange = { fraction ->
                            onSeek((fraction * totalDurationMs).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = AccentOrange,
                            activeTrackColor = AccentOrange,
                            inactiveTrackColor = ChipBg
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTimeMs(currentPosMs),
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatTimeMs(totalDurationMs),
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Media Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = TextMuted
                        )
                    }

                    IconButton(onClick = onPrevTrack) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Main Play/Pause FAB
                    Surface(
                        onClick = onPlayPauseToggle,
                        shape = CircleShape,
                        color = Color.Unspecified,
                        shadowElevation = 12.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(AccentOrange, AccentPink)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    IconButton(onClick = onNextTrack) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Repeat",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimeMs(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
