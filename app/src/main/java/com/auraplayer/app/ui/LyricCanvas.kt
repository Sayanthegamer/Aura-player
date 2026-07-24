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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auraplayer.app.lyrics.LyricLine
import com.auraplayer.app.lyrics.ParsedLyrics
import kotlin.math.abs
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LyricCanvas(
    lyrics: ParsedLyrics,
    currentPositionProvider: () -> Long,
    manualOffsetMs: Long,
    onOffsetChange: (Long) -> Unit,
    onClose: () -> Unit,
    onSeek: (Long) -> Unit = {},
    isLoading: Boolean = false,
    trackTitle: String = "",
    artworkUri: String? = null,
    // Pre-resolved palette colors passed from parent so there is no default-flash on navigation
    paletteAccent: Color = Color(0xFFD0BCFF),
    paletteDominant: Color = Color(0xFF1E1B2E),
    paletteSecondary: Color = Color(0xFF381E72)
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val listState = rememberLazyListState()
    var showOffsetSlider by remember { mutableStateOf(false) }

    // Use parent-supplied palette colors directly (already animated in parent)
    val dominantColor = paletteDominant
    val accentColor = paletteAccent
    val secondaryColor = paletteSecondary

    val themeColorScheme = MaterialTheme.colorScheme
    val primaryAccent = if (accentColor != Color(0xFFD0BCFF)) accentColor else themeColorScheme.primary
    val secondaryAccent = if (secondaryColor != Color(0xFF381E72)) secondaryColor else themeColorScheme.secondary
    val dominantBg = if (dominantColor != Color(0xFF1E1B2E)) dominantColor else themeColorScheme.background

    // Continuous Brownian Motion Animation for super-blurred background orbs
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "lyricBrownian")
    val rawAnim1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(16000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "brownian1"
    )
    val rawAnim2 by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(20000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "brownian2"
    )

    val activeIndex by remember(lyrics.lines, manualOffsetMs) {
        derivedStateOf {
            val pos = currentPositionProvider() + manualOffsetMs
            val idx = lyrics.lines.indexOfLast { it.startMs <= pos }
            if (idx >= 0) idx else 0
        }
    }

    val currentPositionMs = currentPositionProvider() + manualOffsetMs

    // Spring auto-scroll centering to 35% viewport height
    LaunchedEffect(activeIndex) {
        if (lyrics.lines.isNotEmpty()) {
            listState.animateScrollToItem(
                index = activeIndex.coerceAtLeast(0),
                scrollOffset = -240
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantBg)
    ) {
        // Super Blurred Brownian Motion Gradient Blobs
        androidx.compose.foundation.Canvas(
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

            // Orb 1: Top Brownian Float
            val orb1X = canvasW * (0.25f + 0.5f * rawAnim1)
            val orb1Y = canvasH * (0.2f + 0.35f * rawAnim2)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryAccent.copy(alpha = 0.85f), Color.Transparent),
                    center = Offset(orb1X, orb1Y),
                    radius = canvasW * 0.9f
                ),
                center = Offset(orb1X, orb1Y),
                radius = canvasW * 0.9f
            )

            // Orb 2: Bottom Brownian Float
            val orb2X = canvasW * (0.75f - 0.5f * rawAnim2)
            val orb2Y = canvasH * (0.8f - 0.35f * rawAnim1)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(secondaryAccent.copy(alpha = 0.85f), Color.Transparent),
                    center = Offset(orb2X, orb2Y),
                    radius = canvasW * 1.0f
                ),
                center = Offset(orb2X, orb2Y),
                radius = canvasW * 1.0f
            )

            // Dark vignette overlay for 100% lyric readability
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.60f),
                        Color.Black.copy(alpha = 0.35f),
                        Color.Black.copy(alpha = 0.70f)
                    )
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "ETHEREAL LYRICS • ${lyrics.source.uppercase()}",
                        color = primaryAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.5.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (trackTitle.isNotBlank()) trackTitle else "Lyrics",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = { showOffsetSlider = !showOffsetSlider }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Adjust Lyric Offset",
                            tint = if (showOffsetSlider) primaryAccent else Color.White
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Lyrics",
                            tint = Color.White
                        )
                    }
                }
            }

            // Manual Offset Slider
            if (showOffsetSlider) {
                Surface(
                    color = themeColorScheme.surfaceContainerHigh.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Manual Sync Offset",
                                color = themeColorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${if (manualOffsetMs >= 0) "+" else ""}${manualOffsetMs} ms",
                                color = primaryAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = manualOffsetMs.toFloat(),
                            onValueChange = { onOffsetChange(it.toLong()) },
                            valueRange = -5000f..5000f,
                            colors = SliderDefaults.colors(
                                thumbColor = primaryAccent,
                                activeTrackColor = primaryAccent,
                                inactiveTrackColor = themeColorScheme.surfaceContainerHighest
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content: Loading / Empty / Spatial Physics Lyrics List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = primaryAccent,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Fetching synced lyrics from LRCLIB & LyricsPlus...",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (lyrics.lines.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No synced lyrics available for this track",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    itemsIndexed(lyrics.lines) { index, line ->
                        val distanceFromActive = abs(index - activeIndex)
                        val targetScale = max(0.85f, 1.0f - (distanceFromActive * 0.05f))
                        val targetAlpha = if (index == activeIndex) 1.0f else max(0.20f, 0.35f - (distanceFromActive * 0.04f))
                        val targetTranslationY = if (index == activeIndex) 0f else (if (index < activeIndex) -4f else 4f)

                        val scaleState by animateFloatAsState(
                            targetValue = targetScale,
                            animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
                            label = "lineScale"
                        )
                        val alphaState by animateFloatAsState(
                            targetValue = targetAlpha,
                            animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
                            label = "lineAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    this.alpha = alphaState
                                    this.scaleX = scaleState
                                    this.scaleY = scaleState
                                    this.translationY = targetTranslationY.dp.toPx()
                                }
                                .clickable {
                                    onSeek(line.startMs)
                                }
                        ) {
                            AppleMusicWordLine(
                                line = line,
                                currentPositionMs = currentPositionMs,
                                isActive = index == activeIndex,
                                primaryAccent = primaryAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppleMusicWordLine(
    line: LyricLine,
    currentPositionMs: Long,
    isActive: Boolean,
    primaryAccent: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (line.wordTokens.isEmpty()) {
            // Line-Synced Standard LRC Lyric
            val lineScale by animateFloatAsState(
                targetValue = if (isActive) 1.05f else 1.0f,
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy),
                label = "lineScale"
            )
            val shadowColor = primaryAccent.copy(alpha = if (isActive) 0.85f else 0f)

            Text(
                text = line.content,
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.45f),
                fontSize = 28.sp,
                fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold,
                lineHeight = 36.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        this.scaleX = lineScale
                        this.scaleY = lineScale
                    }
            )
        } else {
            // Syllable / Word-Synced Lyric
            FlowRow(
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                line.wordTokens.forEach { token ->
                    val isPastWord = currentPositionMs >= token.endMs
                    val isCurrentWord = currentPositionMs >= token.startMs && currentPositionMs < token.endMs

                    val wordDurationMs = (token.endMs - token.startMs).coerceAtLeast(100L)
                    val isExtendedNote = wordDurationMs > 1200L

                    val targetWordScale = if (isCurrentWord) (if (isExtendedNote) 1.15f else 1.08f) else 1.0f

                    val wordScale by animateFloatAsState(
                        targetValue = targetWordScale,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        ),
                        label = "wordScale"
                    )

                    val progress = if (isCurrentWord) {
                        ((currentPositionMs - token.startMs).toFloat() / wordDurationMs.toFloat()).coerceIn(0f, 1f)
                    } else if (isPastWord) 1f else 0f
                    val wordColor = lerp(
                        Color.White.copy(alpha = 0.45f),
                        Color.White,
                        progress
                    )

                    Text(
                        text = token.word,
                        color = wordColor,
                        fontSize = 28.sp,
                        fontWeight = if (isCurrentWord || isPastWord) FontWeight.Black else FontWeight.Bold,
                        lineHeight = 36.sp,
                        modifier = Modifier
                            .graphicsLayer {
                                this.scaleX = wordScale
                                this.scaleY = wordScale
                            }
                            .padding(end = if (isCurrentWord) 6.dp else 3.dp)
                    )
                }
            }
        }

        // Render Romanization or Translation sub-line if available
        val subText = line.romanization ?: line.translation
        if (!subText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subText,
                color = if (isActive) primaryAccent else Color.White.copy(alpha = 0.45f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
