package com.auraplayer.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auraplayer.app.lyrics.LyricLine
import com.auraplayer.app.lyrics.ParsedLyrics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LyricCanvas(
    lyrics: ParsedLyrics,
    currentPositionProvider: () -> Long,
    manualOffsetMs: Long,
    onOffsetChange: (Long) -> Unit,
    onClose: () -> Unit,
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

    LaunchedEffect(activeIndex) {
        if (lyrics.lines.isNotEmpty()) {
            listState.animateScrollToItem(
                index = activeIndex.coerceAtLeast(0),
                scrollOffset = -250
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SYNCED LYRICS (${lyrics.source})",
                            color = colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.5.sp
                        )
                        Text(
                            text = if (trackTitle.isNotBlank()) trackTitle else "Live Canvas",
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
                        color = colorScheme.surfaceContainerHigh,
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

                // Content: Loading / Empty / Syllable & Romanized Lyrics
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
                            text = "No synced lyrics found for this track",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        itemsIndexed(lyrics.lines) { index, line ->
                            val isActive = index == activeIndex
                            val alpha by animateFloatAsState(
                                targetValue = if (isActive) 1.0f else 0.35f,
                                animationSpec = tween(durationMillis = 300),
                                label = "lyricAlpha"
                            )
                            val scale by animateFloatAsState(
                                targetValue = if (isActive) 1.05f else 0.98f,
                                animationSpec = tween(durationMillis = 300),
                                label = "lyricScale"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer {
                                        this.alpha = alpha
                                        this.scaleX = scale
                                        this.scaleY = scale
                                    }
                            ) {
                                WordSyncedLine(
                                    line = line,
                                    currentPositionMs = currentPositionMs,
                                    isActive = isActive,
                                    colorScheme = colorScheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordSyncedLine(
    line: LyricLine,
    currentPositionMs: Long,
    isActive: Boolean,
    colorScheme: ColorScheme
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isActive || line.wordTokens.isEmpty()) {
            Text(
                text = line.content,
                color = if (isActive) colorScheme.onBackground else colorScheme.onSurfaceVariant,
                fontSize = if (isActive) 24.sp else 18.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
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

                    val progress = if (isCurrentWord) {
                        ((currentPositionMs - token.startMs).toFloat() / (token.endMs - token.startMs).coerceAtLeast(50L).toFloat()).coerceIn(0f, 1f)
                    } else if (isPastWord) 1f else 0f

                    val wordColor = lerp(
                        colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        colorScheme.primary,
                        progress
                    )

                    Text(
                        text = "${token.word} ",
                        color = wordColor,
                        fontSize = 24.sp,
                        fontWeight = if (isCurrentWord || isPastWord) FontWeight.ExtraBold else FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 2.dp)
                    )
                }
            }
        }

        // Render Romanization or Translation sub-line if available
        val subText = line.romanization ?: line.translation
        if (!subText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subText,
                color = if (isActive) colorScheme.secondary else colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontSize = if (isActive) 16.sp else 13.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
