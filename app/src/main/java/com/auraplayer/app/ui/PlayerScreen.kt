package com.auraplayer.app.ui

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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val colorScheme = MaterialTheme.colorScheme

    val primaryAccent = colorScheme.primary
    val secondaryAccent = colorScheme.secondary
    val surfaceContainer = colorScheme.surfaceContainerHigh
    val dominantBg = colorScheme.background
    val onSurface = colorScheme.onBackground
    val onSurfaceVariant = colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier.fillMaxSize(),
        color = dominantBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.surfaceContainer.copy(alpha = 0.8f),
                            dominantBg,
                            dominantBg
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
                            tint = onSurface,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NOW PLAYING",
                            color = primaryAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = track?.album ?: "Aura Soundscape",
                            color = onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Audio Engine FX",
                            tint = primaryAccent
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
                        colors = CardDefaults.cardColors(containerColor = surfaceContainer),
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
                                            colorScheme.surfaceContainerLowest,
                                            surfaceContainer
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
                                        tint = primaryAccent,
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
                        color = onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = track?.artist ?: "Aura Audio Engine",
                        color = onSurfaceVariant,
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
                            color = surfaceContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${track?.codec ?: "FLAC"} • ${track?.bitDepth ?: 24}-bit/${(track?.sampleRate ?: 96000) / 1000}kHz",
                                color = primaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = surfaceContainer.copy(alpha = 0.8f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "RG ${if ((track?.replayGainDb ?: 0f) >= 0) "+" else ""}${String.format("%.1f", track?.replayGainDb ?: 0.0f)}dB",
                                color = secondaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Android 13/14 Squiggly Vibrating Wave Seekbar & Timestamps
                Column(modifier = Modifier.fillMaxWidth()) {
                    val currentPosMs = uiState.currentPositionMs
                    val totalDurationMs = if (uiState.durationMs > 0) uiState.durationMs else 180000L
                    val progressFraction = (currentPosMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)

                    MaterialSquigglySeekBar(
                        progress = progressFraction,
                        isPlaying = uiState.isPlaying,
                        activeColor = primaryAccent,
                        inactiveColor = onSurfaceVariant.copy(alpha = 0.35f),
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
                            color = onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${formatTimeMs(currentPosMs)} / ${formatTimeMs(totalDurationMs)}",
                            color = onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Floating Control Bar Capsule
                Surface(
                    color = surfaceContainer.copy(alpha = 0.95f),
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
                                tint = onSurface.copy(alpha = 0.7f)
                            )
                        }

                        BouncyIconButton(onClick = onPrevTrack) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Track",
                                tint = onSurface,
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
                            color = primaryAccent,
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
                                    tint = colorScheme.onPrimary,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }

                        BouncyIconButton(onClick = onNextTrack) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Track",
                                tint = onSurface,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        BouncyIconButton(onClick = onAlbumArtTap) {
                            Icon(
                                imageVector = Icons.Default.Lyrics,
                                contentDescription = "Synced Lyrics",
                                tint = primaryAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialSquigglySeekBar(
    progress: Float,
    isPlaying: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "squigglyVibration")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
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
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val activeX = (progress * width).coerceIn(0f, width)

            val strokeWidthPx = 4.dp.toPx()
            val amplitudePx = 5.dp.toPx()
            val wavelengthPx = 28.dp.toPx()
            val thumbRadiusPx = 7.5.dp.toPx()

            // 1. Draw Played Squiggly Wave (0 to activeX)
            if (activeX > 0f) {
                val wavePath = Path()
                wavePath.moveTo(0f, centerY)

                val activeEndX = (activeX - thumbRadiusPx).coerceAtLeast(0f)
                var x = 0f
                val step = 2.dp.toPx()

                while (x <= activeEndX) {
                    val currentPhase = if (isPlaying) phase else 0f
                    val y = centerY + amplitudePx * kotlin.math.sin((x / wavelengthPx) * (2 * Math.PI) + currentPhase).toFloat()
                    wavePath.lineTo(x, y)
                    x += step
                }

                drawPath(
                    path = wavePath,
                    color = activeColor,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // 2. Draw Active Thumb Dot
            drawCircle(
                color = activeColor,
                radius = thumbRadiusPx,
                center = Offset(activeX, centerY)
            )

            // 3. Draw Unplayed Straight Line (activeX to width)
            val unplayedStartX = (activeX + thumbRadiusPx).coerceAtMost(width)
            if (unplayedStartX < width) {
                drawLine(
                    color = inactiveColor,
                    start = Offset(unplayedStartX, centerY),
                    end = Offset(width, centerY),
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private fun formatTimeMs(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
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
