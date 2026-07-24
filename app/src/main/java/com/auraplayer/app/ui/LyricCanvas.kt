package com.auraplayer.app.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    trackTitle: String = ""
) {
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()
    var showOffsetSlider by remember { mutableStateOf(false) }

    val currentPositionMs = currentPositionProvider() + manualOffsetMs

    val activeIndex by remember(currentPositionMs, lyrics.lines) {
        derivedStateOf {
            val idx = lyrics.lines.indexOfLast { it.startMs <= currentPositionMs }
            if (idx >= 0) idx else 0
        }
    }

    // Spring auto-scroll centering to 35% viewport height
    LaunchedEffect(activeIndex) {
        if (lyrics.lines.isNotEmpty()) {
            listState.animateScrollToItem(
                index = activeIndex.coerceAtLeast(0),
                scrollOffset = -240
            )
        }
    }

    val ambientGradient = remember(colorScheme.primary, colorScheme.tertiary) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.surface.copy(alpha = 0.95f),
                colorScheme.primaryContainer.copy(alpha = 0.40f),
                colorScheme.surface
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ambientGradient)
    ) {
        // Ethereal Glow Orbs
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer { alpha = 0.25f }
                .blur(85.dp)
                .background(colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .size(380.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer { alpha = 0.20f }
                .blur(95.dp)
                .background(colorScheme.tertiary)
        )

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
                Column {
                    Text(
                        text = "ETHEREAL LYRICS • ${lyrics.source.uppercase()}",
                        color = colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.5.sp
                    )
                    Text(
                        text = if (trackTitle.isNotBlank()) trackTitle else "Apple Music View",
                        color = colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Row {
                    IconButton(onClick = { showOffsetSlider = !showOffsetSlider }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Adjust Lyric Offset",
                            tint = if (showOffsetSlider) colorScheme.primary else colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Lyrics",
                            tint = colorScheme.onSurface
                        )
                    }
                }
            }

            // Manual Offset Slider
            if (showOffsetSlider) {
                Surface(
                    color = colorScheme.surfaceContainerHigh.copy(alpha = 0.90f),
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
                                color = colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${if (manualOffsetMs >= 0) "+" else ""}${manualOffsetMs} ms",
                                color = colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = manualOffsetMs.toFloat(),
                            onValueChange = { onOffsetChange(it.toLong()) },
                            valueRange = -5000f..5000f,
                            colors = SliderDefaults.colors(
                                thumbColor = colorScheme.primary,
                                activeTrackColor = colorScheme.primary,
                                inactiveTrackColor = colorScheme.surfaceContainerHighest
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
                            color = colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Fetching synced lyrics from LRCLIB & LyricsPlus...",
                            color = colorScheme.onSurfaceVariant,
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
                        color = colorScheme.onSurfaceVariant,
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
                                colorScheme = colorScheme
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
    colorScheme: ColorScheme
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isActive || line.wordTokens.isEmpty()) {
            Text(
                text = line.content,
                color = if (isActive) Color.White else colorScheme.onSurfaceVariant,
                fontSize = if (isActive) 30.sp else 22.sp,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.SemiBold,
                lineHeight = if (isActive) 38.sp else 30.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
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
                        colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        Color.White,
                        progress
                    )

                    val shadowColor = colorScheme.primary.copy(alpha = if (isCurrentWord) 0.8f else 0f)

                    Text(
                        text = token.word,
                        color = wordColor,
                        fontSize = 30.sp,
                        fontWeight = if (isCurrentWord || isPastWord) FontWeight.Black else FontWeight.Bold,
                        lineHeight = 38.sp,
                        modifier = Modifier
                            .graphicsLayer {
                                this.scaleX = wordScale
                                this.scaleY = wordScale
                                this.shadowElevation = if (isCurrentWord) (if (isExtendedNote) 18.dp.toPx() else 12.dp.toPx()) else 0f
                                this.spotShadowColor = shadowColor
                                this.ambientShadowColor = shadowColor
                            }
                            .padding(end = if (isCurrentWord) 5.dp else 2.dp)
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
                color = if (isActive) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = if (isActive) 18.sp else 14.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
