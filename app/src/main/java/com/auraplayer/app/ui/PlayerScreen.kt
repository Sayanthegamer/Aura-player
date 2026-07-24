package com.auraplayer.app.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.auraplayer.app.playback.PlayerUiState

data class DynamicThemeColors(
    val dominantBg: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val surfaceContainer: Color,
    val secondaryBg: Color
)

@Composable
fun PlayerScreen(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onAlbumArtTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = uiState.currentTrack

    val dynamicColors = remember(track?.id) {
        DynamicThemeColors(
            dominantBg = Color(0xFF0F0D15),
            primaryAccent = Color(0xFFFF4081),
            secondaryAccent = Color(0xFF00E5FF),
            surfaceContainer = Color(0xFF1E1A29),
            secondaryBg = Color(0xFF14101F)
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = dynamicColors.dominantBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            dynamicColors.secondaryBg.copy(alpha = 0.8f),
                            dynamicColors.dominantBg,
                            dynamicColors.dominantBg
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Collapse Player",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NOW PLAYING",
                            color = dynamicColors.primaryAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = track?.album ?: "Aura Soundscape",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Audio Engine FX",
                            tint = dynamicColors.primaryAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Album Art
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = dynamicColors.surfaceContainer),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .clickable { onAlbumArtTap() }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            dynamicColors.secondaryBg,
                                            dynamicColors.surfaceContainer
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(track?.artworkUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Album Artwork Canvas",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                error = {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = "Album Artwork Canvas",
                                        tint = dynamicColors.primaryAccent,
                                        modifier = Modifier.size(108.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Header
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
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Badges
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = dynamicColors.surfaceContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${track?.codec ?: "FLAC"} • ${track?.bitDepth ?: 24}-bit/${(track?.sampleRate ?: 96000) / 1000}kHz",
                                color = dynamicColors.primaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = dynamicColors.surfaceContainer.copy(alpha = 0.8f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "RG -2.1dB",
                                color = dynamicColors.secondaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vibrating String Waveform Seekbar & Timestamps
                Column(modifier = Modifier.fillMaxWidth()) {
                    val currentPosMs = uiState.currentPositionMs
                    val totalDurationMs = if (uiState.durationMs > 0) uiState.durationMs else 180000L
                    val progressFraction = (currentPosMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)

                    VibratingWaveformSeekBar(
                        progress = progressFraction,
                        isPlaying = uiState.isPlaying,
                        activeColor = dynamicColors.primaryAccent,
                        inactiveColor = dynamicColors.surfaceContainer,
                        onSeek = { fraction ->
                            onSeek((fraction * totalDurationMs).toLong())
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTimeMs(currentPosMs),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatTimeMs(totalDurationMs),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Floating Control Bar Capsule
                Surface(
                    color = dynamicColors.surfaceContainer.copy(alpha = 0.95f),
                    shape = CircleShape,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BouncyIconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        BouncyIconButton(onClick = onPrevTrack) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        val playInteractionSource = remember { MutableInteractionSource() }
                        val isPlayPressed by playInteractionSource.collectIsPressedAsState()
                        val fabScale by animateFloatAsState(
                            targetValue = if (isPlayPressed) 0.88f else 1.0f,
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            ),
                            label = "fabScale"
                        )

                        Surface(
                            onClick = onPlayPauseToggle,
                            interactionSource = playInteractionSource,
                            shape = RoundedCornerShape(26.dp),
                            color = dynamicColors.primaryAccent,
                            shadowElevation = 8.dp,
                            modifier = Modifier.scale(fabScale)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                    tint = dynamicColors.dominantBg,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        BouncyIconButton(onClick = onNextTrack) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        BouncyIconButton(onClick = onAlbumArtTap) {
                            Icon(
                                imageVector = Icons.Default.Lyrics,
                                contentDescription = "Synced Lyrics",
                                tint = dynamicColors.primaryAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VibratingWaveformSeekBar(
    progress: Float,
    isPlaying: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveformVibration")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val baseAmplitudes = remember {
        floatArrayOf(
            0.3f, 0.5f, 0.2f, 0.8f, 0.4f, 0.9f, 0.6f, 0.3f,
            0.7f, 1.0f, 0.5f, 0.8f, 0.4f, 0.6f, 0.9f, 0.3f,
            0.5f, 0.8f, 0.2f, 0.7f, 0.4f, 0.9f, 0.6f, 0.3f,
            0.8f, 1.0f, 0.5f, 0.7f, 0.3f, 0.6f, 0.9f, 0.4f,
            0.6f, 0.9f, 0.3f, 0.8f, 0.5f, 0.7f, 0.4f, 0.2f,
            0.7f, 0.9f, 0.4f, 0.6f, 0.3f, 0.5f, 0.8f, 0.4f
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val fraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barCount = baseAmplitudes.size
            val availableWidth = size.width
            val availableHeight = size.height
            val barWidth = 4.dp.toPx()
            val gapWidth = (availableWidth - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)
            val activeX = progress * availableWidth

            for (i in 0 until barCount) {
                val x = i * (barWidth + gapWidth) + (barWidth / 2f)
                val baseAmp = baseAmplitudes[i]

                val vibrationFactor = if (isPlaying) {
                    0.20f * kotlin.math.sin(phase + i * 0.4f).toFloat()
                } else 0f

                val amp = (baseAmp + vibrationFactor).coerceIn(0.15f, 1.0f)
                val barHeight = (amp * availableHeight * 0.75f).coerceAtLeast(6.dp.toPx())

                val top = (availableHeight - barHeight) / 2f
                val bottom = top + barHeight

                val isPast = x <= activeX
                val color = if (isPast) activeColor else inactiveColor

                drawLine(
                    color = color,
                    start = Offset(x, top),
                    end = Offset(x, bottom),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }

            // Interactive thumb glow indicator
            drawCircle(
                color = activeColor,
                radius = 7.dp.toPx(),
                center = Offset(activeX, availableHeight / 2f)
            )
            drawCircle(
                color = Color.White,
                radius = 3.5.dp.toPx(),
                center = Offset(activeX, availableHeight / 2f)
            )
        }
    }
}

private fun formatTimeMs(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun BouncyIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.84f else 1.0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "btnScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
