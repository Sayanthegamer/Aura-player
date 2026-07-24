package com.auraplayer.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import com.auraplayer.app.audio.AudioDspState
import com.auraplayer.app.audio.EqBand
import com.auraplayer.app.audio.EqPreset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDspBottomSheet(
    dspState: AudioDspState,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectPreset: (EqPreset) -> Unit,
    onBandGainChanged: (Int, Float) -> Unit,
    onBassBoostChanged: (Float) -> Unit,
    onTrebleChanged: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Title & Bypass Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Audio DSP & Equalizer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (dspState.isDynamicsProcessingSupported) "16-Band Parametric Dynamics DSP" else "5-Band Hardware Equalizer",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (dspState.isEnabled) "ON" else "OFF",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (dspState.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Switch(
                        checked = dspState.isEnabled,
                        onCheckedChange = onToggleEnabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = dspState.isEnabled) {
                Column {
                    // Preset Selection Chips
                    Text(
                        text = "Presets",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(EqPreset.values()) { preset ->
                            FilterChip(
                                selected = dspState.preset == preset,
                                onClick = { onSelectPreset(preset) },
                                label = { Text(preset.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bass & Treble Quick Controls
                    Text(
                        text = "Sound Enhancements",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Bass Boost Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Bass Boost", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("+${dspState.bassBoostGainDb.roundToInt()} dB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Slider(
                                    value = dspState.bassBoostGainDb,
                                    onValueChange = onBassBoostChanged,
                                    valueRange = 0f..10f,
                                    steps = 9
                                )
                            }
                        }

                        // Treble Boost Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Treble", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("+${dspState.trebleGainDb.roundToInt()} dB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Slider(
                                    value = dspState.trebleGainDb,
                                    onValueChange = onTrebleChanged,
                                    valueRange = 0f..10f,
                                    steps = 9
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 16-Band Parametric Equalizer Sliders
                    Text(
                        text = "Parametric Frequencies",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(dspState.bands) { band ->
                                BandSliderColumn(
                                    band = band,
                                    onGainChanged = { gain -> onBandGainChanged(band.index, gain) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BandSliderColumn(
    band: EqBand,
    onGainChanged: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(48.dp)
    ) {
        // Gain value text
        val gainText = if (band.gainDb > 0) "+${String.format("%.1f", band.gainDb)}" else String.format("%.1f", band.gainDb)
        Text(
            text = gainText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (band.gainDb != 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // True Vertical Slider via graphics rotation & layout constraint swapping
        Box(
            modifier = Modifier
                .height(160.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = band.gainDb,
                onValueChange = onGainChanged,
                valueRange = band.minGainDb..band.maxGainDb,
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = -90f
                    }
                    .layout { measurable, constraints ->
                        // Swap width and height for vertical layout calculation
                        val placeable = measurable.measure(
                            constraints.copy(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxWidth
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(-placeable.width / 2 + placeable.height / 2, -placeable.height / 2 + placeable.width / 2)
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Frequency label
        val freqLabel = if (band.centerFreqHz >= 1000) "${band.centerFreqHz / 1000}k" else "${band.centerFreqHz}"
        Text(
            text = freqLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
