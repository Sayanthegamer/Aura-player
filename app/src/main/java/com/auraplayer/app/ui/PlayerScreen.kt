package com.auraplayer.app.ui

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
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
    onOpenAudioDsp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val track = uiState.currentTrack

    // Extract dynamic colors from album artwork using Palette
    var dominantColor by remember { mutableStateOf(Color(0xFF1E1B2E)) }
    var accentColor by remember { mutableStateOf(Color(0xFFD0BCFF)) }
    var secondaryColor by remember { mutableStateOf(Color(0xFF381E72)) }

    LaunchedEffect(track?.artworkUri) {
        if (!track?.artworkUri.isNullOrEmpty()) {
            val request = ImageRequest.Builder(context)
                .data(track?.artworkUri)
                .allowHardware(false)
                .build()
            val result = (coil.ImageLoader(context).execute(request) as? coil.request.SuccessResult)?.drawable
            if (result is android.graphics.drawable.BitmapDrawable) {
                val bitmap = result.bitmap
                androidx.palette.graphics.Palette.from(bitmap).generate { palette ->
                    palette?.let { p ->
                        p.getDominantColor(android.graphics.Color.parseColor("#1E1B2E")).let {
                            dominantColor = Color(it)
                        }
                        p.getVibrantColor(p.getLightVibrantColor(android.graphics.Color.parseColor("#D0BCFF"))).let {
                            accentColor = Color(it)
                        }
                        p.getMutedColor(p.getDarkMutedColor(android.graphics.Color.parseColor("#381E72"))).let {
                            secondaryColor = Color(it)
                        }
                    }
                }
            }
        }
    }

    // Animate color transitions smoothly when track changes
    val animatedDominant by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "animatedDominant"
    )
    val animatedAccent by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "animatedAccent"
    )
    val animatedSecondary by animateColorAsState(
        targetValue = secondaryColor,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "animatedSecondary"
    )

    // Continuous Brownian Motion Animation for super-blurred background orbs
    val transition = rememberInfiniteTransition(label = "brownianMotion")
    val rawAnim1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brownian1"
    )
    val rawAnim2 by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brownian2"
    )

    val primaryAccent = animatedAccent
    val secondaryAccent = animatedSecondary
    val surfaceContainer = Color.White.copy(alpha = 0.15f)
    val dominantBg = animatedDominant
    val onSurface = Color.White
    val onSurfaceVariant = Color.White.copy(alpha = 0.85f)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = dominantBg
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Super Blurred Brownian Motion Gradient Blobs
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        renderEffect = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            android.graphics.RenderEffect.createBlurEffect(
                                140f, 140f,
                                android.graphics.Shader.TileMode.MIRROR
                            ).asComposeRenderEffect()
                        } else null
                    }
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Orb 1: Top-Left to Center Brownian Float
                val orb1X = canvasW * (0.2f + 0.5f * rawAnim1)
                val orb1Y = canvasH * (0.15f + 0.4f * rawAnim2)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animatedAccent.copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(orb1X, orb1Y),
                        radius = canvasW * 0.85f
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = canvasW * 0.85f
                )

                // Orb 2: Bottom-Right to Center Brownian Float
                val orb2X = canvasW * (0.8f - 0.5f * rawAnim2)
                val orb2Y = canvasH * (0.75f - 0.4f * rawAnim1)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(animatedSecondary.copy(alpha = 0.85f), Color.Transparent),
                        center = Offset(orb2X, orb2Y),
                        radius = canvasW * 0.95f
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = canvasW * 0.95f
                )

                // Dark contrast gradient for maximum text legibility
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.70f)
                        )
                    )
                )
            }

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
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = track?.album ?: "Aura Soundscape",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = onOpenAudioDsp) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Audio Engine FX & EQ",
                            tint = Color.White
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
                        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .clickable { onAlbumArtTap() }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
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
                                        tint = Color.White,
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
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // High Contrast Frosted Glass Badges
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.20f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "${track?.codec ?: "FLAC"} • ${track?.bitDepth ?: 24}-bit/${(track?.sampleRate ?: 96000) / 1000}kHz",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = Color.White.copy(alpha = 0.20f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "RG ${if ((track?.replayGainDb ?: 0f) >= 0) "+" else ""}${String.format("%.1f", track?.replayGainDb ?: 0.0f)}dB",
                                color = Color.White,
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
                        activeColor = Color.White,
                        inactiveColor = Color.White.copy(alpha = 0.35f),
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
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${formatTimeMs(currentPosMs)} / ${formatTimeMs(totalDurationMs)}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Modern Frosted Glass Control Bar Capsule
                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    shadowElevation = 24.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BouncyIconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = "Shuffle",
                                tint = Color.White.copy(alpha = 0.75f)
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
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 12.dp,
                            modifier = Modifier.scale(fabScale)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
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
                                tint = Color.White
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

    val animatedAmplitudeFactor by animateFloatAsState(
        targetValue = if (isPlaying) 1.0f else 0.0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "amplitudeCollapse"
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
            val maxAmplitudePx = 5.dp.toPx()
            val currentAmplitudePx = maxAmplitudePx * animatedAmplitudeFactor
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
                    val y = centerY + currentAmplitudePx * kotlin.math.sin((x / wavelengthPx) * (2 * Math.PI) + phase).toFloat()
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
