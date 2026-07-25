## 🔘 1. Comprehensive Buttons & FAB Suite

Based on official [Material 3 All Buttons Overview](https://m3.material.io/components/all-buttons).

### A. The 5 Common Button Variants

| Button Variant | Container Token | Content Token | Default Elevation | Hover Elevation | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **FilledButton** | `primary` | `onPrimary` | 0 dp | 1 dp | High-emphasis primary action (Play, Save) |
| **FilledTonalButton** | `secondaryContainer` | `onSecondaryContainer` | 0 dp | 1 dp | Medium-emphasis action (Shuffle All, Add Queue) |
| **ElevatedButton** | `surfaceContainerLow` | `primary` | 1 dp | 2 dp | Standout action on flat surfaces |
| **OutlinedButton** | Transparent + `outline` stroke | `primary` | 0 dp | 0 dp | Medium-low emphasis secondary actions |
| **TextButton** | Transparent | `primary` | 0 dp | 0 dp | Dialog actions, inline text links |

#### Common Button Layout & Padding Rules
* **Container Height**: Standard **40 dp** height (touch target box enforced at **$48\text{dp}$**).
* **Horizontal Padding**: **24 dp** (without icon) or **16 dp** (when containing a leading icon).
* **Icon-to-Label Gap**: **8 dp** (`M3Spacing.Small`) between leading icon and button label text.
* **Shape**: Full Pill (`RoundedCornerShape(9999.dp)`).


### B. Floating Action Buttons (FAB Family & Extended FAB)

Based on official [Material 3 FAB Specs](https://m3.material.io/components/floating-action-button/specs), [Extended FAB Specs](https://m3.material.io/components/extended-fab/specs), and [Guidelines](https://m3.material.io/components/floating-action-button/guidelines).

| FAB Variant | Container Size | Icon Size | Shape Token | Default Elevation | Hover Elevation | Usage |
|:---|:---|:---|:---|:---|:---|:---|
| **Small FAB** | **40 dp × 40 dp** | 24 dp | `medium` (12dp) | 6 dp | 8 dp | Secondary floating action (Touch target box expanded to 48dp) |
| **Standard FAB** | **56 dp × 56 dp** | 24 dp | `large` (16dp) | 6 dp | 8 dp | Primary screen floating action |
| **Large FAB** | **96 dp × 96 dp** | 36 / 48 dp | `extraLarge` (28dp) | 6 dp | 8 dp | **Hero Now-Playing central Play/Pause FAB** |
| **Extended FAB** | **56 dp** Height | 24 dp | 16dp / Pill | 6 dp | 8 dp | Icon + Label expanding action (e.g., "Shuffle All") |


#### Extended FAB Expansion & Scroll-Collapse Pattern
When user scrolls down a media list, the Extended FAB collapses smoothly to a standard FAB showing only the icon:

```kotlin
val lazyListState = rememberLazyListState()
val isExpanded by remember {
    derivedStateOf { lazyListState.firstVisibleItemIndex == 0 }
}

ExtendedFloatingActionButton(
    onClick = onShuffleAll,
    expanded = isExpanded,
    icon = { Icon(Icons.Default.Shuffle, contentDescription = null) },
    text = { Text("Shuffle All", style = MaterialTheme.typography.labelLarge) },
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
)
```

#### FAB Menu (Speed Dial Reveal Pattern)
Based on official [Material 3 FAB Menu Specs](https://m3.material.io/components/fab-menu/specs) and [Guidelines](https://m3.material.io/components/fab-menu/guidelines).

Tapping the main FAB reveals a vertical stack of **Small FABs** ($40\text{dp} \times 40\text{dp}$) with accompanying label pills:
* **Trigger FAB Morph**: Main FAB icon rotates $45^\circ \to 90^\circ$ (e.g. `+` morphs to `×`).
* **Staggered Entry**: Items reveal sequentially with a $50\text{ms}$ stagger delay using `BouncySpring`.
* **Scrim Background**: Darkens background with Level 4 scrim (`Color.Black.copy(alpha = 0.4f)`).

```kotlin
// FAB Menu Container Implementation
Box(modifier = Modifier.fillMaxSize()) {
    // Scrim overlay when expanded
    if (isFabMenuExpanded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { isFabMenuExpanded = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
    ) {
        if (isFabMenuExpanded) {
            // Speed Dial Item 1: Scan Folder
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    Text("Scan Folder", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.width(8.dp))
                SmallFloatingActionButton(onClick = onScanFolder) {
                    Icon(Icons.Default.FolderOpen, contentDescription = "Scan Folder")
                }
            }
            // Speed Dial Item 2: Add Playlist
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    Text("New Playlist", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Spacer(Modifier.width(8.dp))
                SmallFloatingActionButton(onClick = onNewPlaylist) {
                    Icon(Icons.Default.PlaylistAdd, contentDescription = "New Playlist")
                }
            }
        }

        // Main Trigger FAB
        FloatingActionButton(
            onClick = { isFabMenuExpanded = !isFabMenuExpanded },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            val rotation by animateFloatAsState(if (isFabMenuExpanded) 45f else 0f)
            Icon(
                Icons.Default.Add,
                contentDescription = "Quick Actions",
                modifier = Modifier.graphicsLayer { rotationZ = rotation }
            )
        }
    }
}
```



### C. Icon Buttons (Standard, Filled, Tonal, Outlined)

Based on official [Material 3 Icon Buttons Specs](https://m3.material.io/components/icon-buttons/specs) and [Guidelines](https://m3.material.io/components/icon-buttons/guidelines).

| Icon Button Variant | Container Token | Content Token | Corner Shape | Primary Usage |
|:---|:---|:---|:---|:---|
| **Standard IconButton** | Transparent | `onSurfaceVariant` / `primary` | Circle (`9999.dp`) | App bar actions, inline list item actions |
| **FilledIconButton** | `primary` | `onPrimary` | Circle (`9999.dp`) | High-emphasis action / toggle |
| **FilledTonalIconButton** | `secondaryContainer` | `onSecondaryContainer` | Circle (`9999.dp`) | Medium-emphasis media control toggles (Shuffle / Repeat) |
| **OutlinedIconButton** | Transparent + `outline` stroke | `onSurface` | Circle (`9999.dp`) | Bordered secondary toggles |

#### Icon Button Dimension & Touch Target Specs
* **Container Dimensions**: **40 dp × 40 dp**
* **Icon Size**: Standard **24 dp × 24 dp**
* **Touch Target Area**: Minimum **$48\text{dp} \times 48\text{dp}$** touch target boundary.

```kotlin
// Filled Tonal Icon Button for Secondary Media Control Toggles (Shuffle / Repeat)
FilledTonalIconButton(
    onClick = onToggleShuffle,
    colors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = if (isShuffleActive) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = if (isShuffleActive) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ),
    modifier = Modifier.size(48.dp) // Enforces 48dp M3 touch target boundary
) {
    Icon(
        imageVector = Icons.Default.Shuffle,
        contentDescription = if (isShuffleActive) "Shuffle On" else "Shuffle Off",
        modifier = Modifier.size(24.dp)
    )
}
```


### D. Segmented Buttons (Audio EQ & Library View Selectors)

Based on official [Material 3 Segmented Buttons Specs](https://m3.material.io/components/segmented-buttons/specs) and [Guidelines](https://m3.material.io/components/segmented-buttons/guidelines).

Used for selecting mutually exclusive options (Single-Choice) or toggling multiple active filters (Multi-Choice):

#### Segmented Button State & Color Tokens

| Segment State | Container Token | Content Token | Leading Icon | Border Stroke |
|:---|:---|:---|:---|:---|
| **Selected** | `secondaryContainer` | `onSecondaryContainer` | `Icons.Default.Check` | $1\text{dp}$ `outline` |
| **Unselected** | Transparent | `onSurfaceVariant` | None | $1\text{dp}$ `outline` |

#### Dimension & Corner Rules
* **Container Height**: **40 dp**
* **Outer Corners**: Full Pill (`9999.dp`) or $12\text{dp}$ (`shape.medium`).
* **Inner Edges**: Shared flat edges computed via `SegmentedButtonDefaults.itemShape(index = index, count = totalCount)`.

```kotlin
// Single-Choice Segmented Button Row for Audio Equalizer Presets
SingleChoiceSegmentedButtonRow(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
) {
    EqPreset.entries.forEachIndexed { index, preset ->
        SegmentedButton(
            selected = currentPreset == preset,
            onClick = { onSelectPreset(preset) },
            shape = SegmentedButtonDefaults.itemShape(index = index, count = EqPreset.entries.size),
            colors = SegmentedButtonDefaults.colors(
                activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                inactiveContainerColor = Color.Transparent,
                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            icon = {
                if (currentPreset == preset) {
                    SegmentedButtonDefaults.Icon(active = true)
                }
            }
        ) {
            Text(preset.displayName, style = MaterialTheme.typography.labelMedium)
        }
    }
}
```

### E. Button Groups (Connected vs. Separated Group Specifications)

Based on official [Material 3 Button Groups Overview](https://m3.material.io/components/button-groups/overview), [Specs](https://m3.material.io/components/button-groups/specs), [Guidelines](https://m3.material.io/components/button-groups/guidelines), and [Accessibility](https://m3.material.io/components/button-groups/accessibility).

| Group Type | Inner Edge Styling | Gap | Primary Usage |
|:---|:---|:---|:---|
| **Connected Group** | Shared inner edges (`SegmentedButtonDefaults.itemShape`) | 0 dp | Mutually exclusive presets (EQ Flat / Rock / Jazz / Bass) |
| **Separated Group** | Full individual corner radii ($9999\text{dp}$ / $12\text{dp}$) | 8 dp (`Small`) | Media Player Control Bar (Prev · Play · Next · Shuffle) |

```kotlin
// Separated Media Control Button Group
Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
) {
    IconButton(onClick = onShuffle, modifier = Modifier.size(48.dp)) {
        Icon(Icons.Default.Shuffle, contentDescription = "Shuffle")
    }
    IconButton(onClick = onPrevious, modifier = Modifier.size(48.dp)) {
        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track")
    }
    // Hero Central Large Play/Pause FAB (96dp x 96dp)
    LargeFloatingActionButton(
        onClick = onPlayPause,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.size(96.dp)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier.size(48.dp)
        )
    }
    IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
        Icon(Icons.Default.SkipNext, contentDescription = "Next Track")
    }
    IconButton(onClick = onRepeat, modifier = Modifier.size(48.dp)) {
        Icon(Icons.Default.Repeat, contentDescription = "Repeat Mode")
    }
}
```

#### Button Group Accessibility Standards
* **Group Context**: Screen readers must announce the selected option and total item count (e.g. `"Equalizer Presets, Rock selected, 2 of 5"`).
* **Touch Target**: Every button in a group must enforce a minimum **$48\text{dp} \times 48\text{dp}$** touch box.

### F. Split Buttons (Dual-Action Button Specifications)

Based on official [Material 3 Split Button Specs](https://m3.material.io/components/split-button/specs) and [Guidelines](https://m3.material.io/components/split-button/guidelines).

Combines a primary direct action with a secondary dropdown menu in a unified container (e.g., "Play Now" + "▼" options for Add to Queue / Play Next):

| Split Button Component | Position | Container Token | Function |
|:---|:---|:---|:---|
| **Primary Action Button** | Left | `primary` / `secondaryContainer` | Immediate primary action ("Play Now") |
| **Divider Line** | Center | $1\text{dp}$ `outlineVariant` | Visual separation |
| **Trailing Menu Button** | Right | `primary` / `secondaryContainer` | Triggers options menu (Add to Queue, Play Next) |

```kotlin
// Split Button Compose Implementation
Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.height(40.dp).clip(RoundedCornerShape(9999.dp))
) {
    // Primary Action
    Surface(
        onClick = onPlayNow,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.weight(1f).fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Play Now", style = MaterialTheme.typography.labelLarge)
        }
    }
    // 1dp Vertical Divider Line
    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)))
    // Trailing Menu Button
    Surface(
        onClick = onOpenMenu,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxHeight().width(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "More Play Options")
        }
    }
}
```




## 🃏 2. Card Containers Specifications

Based on official [Material 3 Cards Overview](https://m3.material.io/components/cards/overview), [Specs](https://m3.material.io/components/cards/specs), [Guidelines](https://m3.material.io/components/cards/guidelines), and [Accessibility](https://m3.material.io/components/cards/accessibility).

### A. The 3 Card Variants

| Card Variant | Container Token | Border Stroke | Default Elevation | Dragged / Pressed Elevation | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **Elevated Card** | `surfaceContainerLow` | None | **1 dp** | **6 dp** | Highlighted active media card, Now Playing summary |
| **Filled Card** | `surfaceContainerHighest` | None | **0 dp** | **3 dp** | Standard album grid cards, playlist list items |
| **Outlined Card** | `surface` | $1\text{dp}$ `outlineVariant` | **0 dp** | **3 dp** | Audio DSP preset cards, equalizer band containers |

### B. Compose Implementations

```kotlin
// 1. Filled Card (Album Grid Item)
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
    ),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth()
) {
    Column(modifier = Modifier.padding(16.dp)) {
        AsyncImage(
            model = album.artUri,
            contentDescription = null,
            modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)) // Concentric 12dp - 4dp = 8dp
        )
        Spacer(Modifier.height(8.dp))
        Text(album.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(album.artist, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// 2. Elevated Interactive Card (Active Playing Track)
Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ),
    shape = RoundedCornerShape(16.dp)
) {
    // Active track details
}

// 3. Outlined Card (DSP Preset Container)
OutlinedCard(
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(16.dp)
) {
    // DSP Preset controls
}
```


---

## 🎚️ 3. Sliders & Audio Progress Controls Specifications

Based on official Material 3 Sliders Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/sliders/*`).

### A. Slider Variant Matrix

| Slider Variant | Track Height | Thumb Radius | Tick Marks | Primary Usage |
|:---|:---|:---|:---|:---|
| **Continuous Slider** | **4 dp** (8dp active) | **10 dp** ($20\text{dp}$ circle) | None | **Media Seek Bar** & Volume Control |
| **Centered Slider** | **4 dp** (8dp active) | **10 dp** ($20\text{dp}$ circle) | Center zero notch | **16-Band Equalizer Gain Sliders** ($\pm 12\text{dB}$) |
| **Discrete Slider** | **4 dp** (8dp active) | **10 dp** ($20\text{dp}$ circle) | $4\text{dp}$ Ticks | Playback speed selector ($0.5\times \to 2.0\times$) |
| **Range Slider** | **4 dp** (8dp active) | Dual $10\text{dp}$ Thumbs | Optional | Track AB loop trim range selector |

### B. Color Tokens & Touch Target Box
* **Active Track Color**: `primary`
* **Inactive Track Color**: `surfaceContainerHighest`
* **Thumb Color**: `primary` (or `onPrimaryContainer`)
* **Touch Target Box**: Minimum **$48\text{dp} \times 48\text{dp}$** touch target boundary.

### C. Compose Seek Bar & Vertical Equalizer Band Patterns

```kotlin
// 1. Continuous Media Seek Bar
@Composable
fun MediaSeekBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit
) {
    Slider(
        value = positionMs.toFloat(),
        onValueChange = { onSeek(it.toLong()) },
        valueRange = 0f..durationMs.coerceAtLeast(1L).toFloat(),
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    )
}

// 2. Vertical Equalizer Band Slider (+12dB to -12dB)
@Composable
fun VerticalEqBandSlider(
    frequencyHz: String,
    gainDb: Float,
    onGainChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text("${gainDb.toInt()}dB", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(160.dp).width(48.dp)
        ) {
            Slider(
                value = gainDb,
                onValueChange = onGainChange,
                valueRange = -12f..12f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.tertiary,
                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                modifier = Modifier
                    .graphicsLayer {
                        rotationZ = -90f // Rotates horizontal slider into vertical EQ band
                    }
                    .width(160.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(frequencyHz, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
```

### D. Accessibility
* **D-Pad Navigation**: Arrow keys increment/decrement slider values smoothly.
* **Value Announcement**: Screen readers announce formatted audio readout (e.g. `"Seek bar, 2 minutes 45 seconds of 4 minutes 12 seconds"`).


---

## 📑 3. Sheets Specifications (Modal & Standard Bottom Sheets, Side Sheets)

Based on official Material 3 Bottom Sheets and Side Sheets Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/*sheets/*`).

### A. Sheet Variant Matrix

| Sheet Variant | Container Token | Corner Radius | Drag Handle | Width / Placement | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **Modal Bottom Sheet** | `surfaceContainerLow` (Level 1) | **28 dp** (Top Corners) | $32\text{dp} \times 4\text{dp}$ Pill | 100% Mobile Width (Bottom) | **Audio DSP Equalizer Sheet**, Lyrics Options |
| **Standard Bottom Sheet** | `surfaceContainerLow` (Level 1) | **28 dp** (Top Corners) | $32\text{dp} \times 4\text{dp}$ Pill | 100% Mobile Width (Bottom) | Collapsible Mini-Player $\to$ Now Playing Sheet |
| **Side Sheet** (Modal / Standard) | `surfaceContainerLow` (Level 1) | **28 dp** (Inner Corners) | Optional | **256 dp – 400 dp** (Right/Left Side) | Large Screen / Tablet Queue & DSP Controls |

### B. Structural & Drag Handle Tokens
* **Top Corner Radius**: **28 dp** (`shape.extraLarge`).
* **Drag Handle Dimensions**: **32 dp × 4 dp** pill (`onSurfaceVariant` with 40% alpha).
* **Scrim Background**: Level 4 Scrim overlay (`Color.Black.copy(alpha = 0.32f)`).

### C. Compose ModalBottomSheet Pattern

```kotlin
// Audio DSP Equalizer Modal Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDspBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("16-Band Equalizer", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            // Vertical Equalizer Band Sliders
        }
    }
}
```

### D. Accessibility
* **Sheet Dismiss Semantics**: Swiping down past threshold, tapping background scrim, or pressing `Back` hardware button dismisses sheet cleanly.


---

## 🏷️ 5. Chips & Metadata Tags

```kotlin
// High-Res Audio Quality Tag (FLAC / 24-bit / 96kHz)
Surface(
    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
    shape = RoundedCornerShape(6.dp)
) {
    Text(
        text = "24-BIT / 96KHZ FLAC",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
```

---

## 📋 6. List Item Specifications (One-Line, Two-Line & Three-Line Items)

Based on official Material 3 Lists Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/lists/*`).

### A. List Item Variant Matrix

| List Item Variant | Container Height | Leading Slot | Headline Typography | Supporting Text | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **One-Line ListItem** | **56 dp** (48dp dense) | $24\text{dp}$ Icon / Avatar | `bodyLarge` ($16\text{sp}$) | None | Quick setting items, genre list |
| **Two-Line ListItem** | **72 dp** | **48 dp × 48 dp** Art | `titleMedium` ($16\text{sp}$) | `bodyMedium` ($14\text{sp}$) | **Standard Music Track Item** (Title + Artist) |
| **Three-Line ListItem** | **88 dp** | **48 dp × 48 dp** Art | `titleMedium` ($16\text{sp}$) | `bodyMedium` + `bodySmall` | Rich Track Item (Title + Artist + Codec Badges) |

### B. Structural & Slot Spacing Tokens
* **Horizontal Padding**: **16 dp** (`M3Spacing.Large`).
* **Leading Slot Box**: **48 dp × 48 dp** ($8\text{dp}$ concentric corner radius).
* **Leading Slot Gap**: **16 dp** spacing before headline text.
* **Trailing Slot**: Trailing action button (e.g. `MoreVert` menu icon or `DragHandle` for reordering queue).

### C. Compose ListItem Pattern

```kotlin
// Two-Line Track List Item (Standard Music Track Row)
ListItem(
    headlineContent = {
        Text(
            text = track.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    supportingContent = {
        Text(
            text = "${track.artist} • ${track.album}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    leadingContent = {
        AsyncImage(
            model = track.artUri,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    },
    trailingContent = {
        IconButton(onClick = onOpenTrackOptions) {
            Icon(Icons.Default.MoreVert, contentDescription = "Track Options")
        }
    },
    colors = ListItemDefaults.colors(
        containerColor = Color.Transparent,
        headlineColor = MaterialTheme.colorScheme.onSurface,
        supportingColor = MaterialTheme.colorScheme.onSurfaceVariant
    ),
    modifier = Modifier
        .fillMaxWidth()
        .clickable { onPlayTrack(track) }
)
```

### D. Accessibility
* **Row Accessibility Semantics**: Entire list item row acts as a single clickable target so TalkBack reads: `"Song Title, by Artist, double tap to play"`.


## 🏛️ 7. App Bars (Top & Bottom App Bar Specifications)

Based on official [Material 3 App Bars Overview](https://m3.material.io/components/app-bars/overview), [Specs](https://m3.material.io/components/app-bars/specs), [Guidelines](https://m3.material.io/components/app-bars/guidelines), and [Accessibility](https://m3.material.io/components/app-bars/accessibility).

### A. Top App Bar Variant Matrix

| Variant Name | Container Height | Title Alignment | Title Typography | Primary Usage |
|:---|:---|:---|:---|:---|
| `CenterAlignedTopAppBar` | **64 dp** | Center | `titleLarge` ($22\text{sp}$) | Main player header, track details |
| `TopAppBar` (Small) | **64 dp** | Start (Left) | `titleLarge` ($22\text{sp}$) | Standard sub-screens (Settings, EQ) |
| `MediumTopAppBar` | **112 dp** (Expanded) | Start (Bottom) | `headlineMedium` ($28\text{sp}$) | Genre / Playlist headers |
| `LargeTopAppBar` | **152 dp** (Expanded) | Start (Bottom) | `headlineLarge` ($32\text{sp}$) | Album / Artist Hero spotlight headers |

### B. Scroll Behavior & Elevation
* **Unscrolled State**: Container color uses `surface` ($0\text{dp}$ elevation).
* **Scrolled State**: Container transitions dynamically to `surfaceContainerLow` ($1\text{dp}$ elevation) with `onSurface` content tinting.
* **Scroll Connection**: Pair with `TopAppBarDefaults.enterAlwaysScrollBehavior()` or `exitUntilCollapsedScrollBehavior()`.

```kotlin
// Large Collapsing Top App Bar for Album Detail View
val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
        LargeTopAppBar(
            title = {
                Text(
                    text = album.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "Search Album")
                }
                IconButton(onClick = onMenu) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        )
    }
) { innerPadding ->
    // Scrollable Album Content
}
```

### C. Bottom App Bar Spec
* **Container Height**: **80 dp**
* **Container Color**: `surfaceContainer` (Level 2 Elevation / $3\text{dp}$)
* **Action Slots**: Leading action icons + embedded or end-aligned FAB (Play / Pause or Shuffle).

## 🔴 8. Badges (Small & Large Badge Specifications)

Based on official [Material 3 Badges Overview](https://m3.material.io/components/badges/overview), [Specs](https://m3.material.io/components/badges/specs), [Guidelines](https://m3.material.io/components/badges/guidelines), and [Accessibility](https://m3.material.io/components/badges/accessibility).

### A. Badge Variant Specs

| Badge Variant | Dimensions | Container Shape | Primary Usage |
|:---|:---|:---|:---|
| **Small Badge (Dot)** | **6 dp × 6 dp** | Circle (`9999.dp`) | Active mode indicator (Shuffle active dot), unread status |
| **Large Badge (Numbered)** | **16 dp** Height | Full Pill (`9999.dp`) | Queue track count (`12`), pending scrobble count (`5`) |

### B. Color Tokens & Typography
* **Container Color**: `error` (for alerts/notifications) or `tertiary` (for media queue counts).
* **Content Color**: `onError` or `onTertiary`.
* **Typography**: `labelSmall` ($11\text{sp}$) with bold weight.

### C. Jetpack Compose `BadgedBox` Pattern

```kotlin
// Queue Icon with Active Track Count Badge
BadgedBox(
    badge = {
        if (queueCount > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ) {
                Text(
                    text = queueCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
) {
    IconButton(onClick = onOpenQueue) {
        Icon(Icons.Default.QueueMusic, contentDescription = "Play Queue, $queueCount tracks")
    }
}
```

## 🎠 9. Carousel (Multi-Browse Hero Carousel Specifications)

Based on official [Material 3 Carousel Overview](https://m3.material.io/components/carousel/overview), [Specs](https://m3.material.io/components/carousel/specs), [Guidelines](https://m3.material.io/components/carousel/guidelines), and [Accessibility](https://m3.material.io/components/carousel/accessibility).

### A. Carousel Layout Variants

| Carousel Variant | Focal Item Width | Edge Item Preview | Corner Radius | Primary Usage |
|:---|:---|:---|:---|:---|
| **Multi-Browse Carousel** | **70–80%** of screen width | Medium (15%) + Small (5%) edge preview | **28 dp** (`extraLarge`) | **Hero Album Artwork swipeable carousel** |
| **Un-contained Carousel** | Fixed width (e.g. 160dp) | Fully visible overflowing list | **16 dp** (`large`) | Recently played albums row |
| **Hero Carousel** | **100%** of screen width | None | **28 dp** (`extraLarge`) | Full-screen media spotlight |

### B. Compose Morphing Pager Pattern

```kotlin
// Multi-Browse Hero Album Artwork Carousel
val pagerState = rememberPagerState(pageCount = { albums.size })

HorizontalPager(
    state = pagerState,
    contentPadding = PaddingValues(horizontal = 32.dp),
    pageSpacing = 16.dp,
    modifier = Modifier.fillMaxWidth().height(320.dp)
) { page ->
    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

    // Dynamic scale & alpha interpolation based on scroll distance from focal center
    val scale = lerp(0.85f, 1.0f, 1f - pageOffset.coerceIn(0f, 1f))
    val alpha = lerp(0.5f, 1.0f, 1f - pageOffset.coerceIn(0f, 1f))
    val cornerRadius = lerp(16.dp, 28.dp, 1f - pageOffset.coerceIn(0f, 1f))

    Card(
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .fillMaxSize()
    ) {
        AsyncImage(
            model = albums[page].artUri,
            contentDescription = albums[page].title,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

## ☑️ 10. Checkbox (Multi-Selection & Batch State Specifications)

Based on official [Material 3 Checkbox Overview](https://m3.material.io/components/checkbox/overview), [Specs](https://m3.material.io/components/checkbox/specs), [Guidelines](https://m3.material.io/components/checkbox/guidelines), and [Accessibility](https://m3.material.io/components/checkbox/accessibility).

### A. Checkbox State Matrix

| Checkbox State | Container Token | Mark / Stroke Icon | Primary Usage |
|:---|:---|:---|:---|
| **Unchecked** | Transparent | $2\text{dp}$ `outline` border | Unselected track in batch list |
| **Checked** | `primary` | `onPrimary` Checkmark | Selected track for playlist export |
| **Indeterminate (Mixed)** | `primary` | `onPrimary` Horizontal Dash | Partially selected album (some tracks selected) |

### B. Dimensions & Touch Target
* **Visual Box Dimensions**: **18 dp × 18 dp** ($2\text{dp}$ corner radius / `shape.extraSmall`).
* **Touch Target Area**: Minimum **$48\text{dp} \times 48\text{dp}$** touch target boundary.

### C. Compose Toggleable Row Pattern

```kotlin
// Batch Selection Track Row with Merged Accessibility Semantics
Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .fillMaxWidth()
        .toggleable(
            value = isTrackSelected,
            onValueChange = { onToggleTrackSelection(track) },
            role = Role.Checkbox
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
) {
    Checkbox(
        checked = isTrackSelected,
        onCheckedChange = null, // Handled by row toggleable modifier
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.primary,
            uncheckedColor = MaterialTheme.colorScheme.outline
        )
    )
    Spacer(Modifier.width(16.dp))
    Text(
        text = track.title,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
```

## 🏷️ 11. Chips Specifications (Assist, Filter, Input & Suggestion Chips)

Based on official Material 3 Chips Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/chips/*`).

### A. The 4 Chip Variant Matrix

| Chip Variant | Container Token (Unselected) | Container Token (Selected) | Leading Icon | Primary Usage |
|:---|:---|:---|:---|:---|
| **FilterChip** | Transparent + $1\text{dp}$ `outline` | `secondaryContainer` | `Icons.Default.Check` (when selected) | Genre filters (Rock, Jazz), Quality tags (FLAC, MP3) |
| **AssistChip** | Transparent + $1\text{dp}$ `outline` | N/A | Action icon (e.g. `Icons.Default.Folder`) | Quick actions ("Scan Library", "Sync Lyrics") |
| **SuggestionChip** | `surfaceContainerLow` | N/A | Optional suggestion icon | Smart recommendations ("Recently Played", "Favorites") |
| **InputChip** | `surfaceContainerHigh` | `primaryContainer` | Trailing `Close` icon | Custom playlist tags, artist filter pills |

### B. Dimensions & Touch Target
* **Container Height**: **32 dp**
* **Corner Radius**: **8 dp** (`shape.small`)
* **Leading / Trailing Icon Size**: **18 dp × 18 dp**
* **Touch Target Area**: Minimum **$48\text{dp} \times 48\text{dp}$** touch target box boundary.

### C. Compose FilterChip FlowRow Pattern

```kotlin
// Genre Filter Chip Row with Selected Checkmarks
@Composable
fun GenreFilterChipRow(
    selectedGenre: String?,
    genres: List<String>,
    onSelectGenre: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(genres) { genre ->
            val isSelected = selectedGenre == genre
            FilterChip(
                selected = isSelected,
                onClick = { onSelectGenre(genre) },
                label = { Text(genre, style = MaterialTheme.typography.labelMedium) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null,
                shape = RoundedCornerShape(8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = Color.Transparent,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
```

## 📅 12. Date Pickers Specifications (Calendar Dialog & Range Picker)

Based on official Material 3 Date Pickers Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/date-pickers/*`).

### A. Date Picker Variant Matrix

| Variant Name | Container Token | Corner Radius | Primary Usage |
|:---|:---|:---|:---|
| **DatePickerDialog** | `surfaceContainerHigh` (Level 3 / $6\text{dp}$) | **28 dp** (`extraLarge`) | Release date filtering, metadata edit |
| **DateRangePicker** | `surfaceContainerHigh` (Level 3 / $6\text{dp}$) | **28 dp** (`extraLarge`) | Scrobble history date range filtering |
| **DateInputPicker** | `surfaceContainerHigh` | **28 dp** (`extraLarge`) | Direct keyboard text date entry |

### B. Dimensions & Selection Tokens
* **Container Corner Radius**: **28 dp** (`shape.extraLarge`).
* **Selected Day Circle**: **40 dp × 40 dp** (`primary` container + `onPrimary` text).
* **Header Typography**: `headlineLarge` ($32\text{sp}$) for selected date readout.

### C. Compose DatePickerDialog Pattern

```kotlin
// Date Picker Dialog for Filtering Tracks by Release Date
@Composable
fun TrackReleaseDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        },
        shape = RoundedCornerShape(28.dp),
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        DatePicker(state = datePickerState)
    }
}
```

## ⏰ 13. Time Pickers Specifications (Sleep Timer Dial & Text Input)

Based on official Material 3 Time Pickers Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/time-pickers/*`).

### A. Time Picker Variant Matrix

| Variant Name | Container Token | Corner Radius | Primary Usage |
|:---|:---|:---|:---|
| **Dial TimePicker** | `surfaceContainerHigh` (Level 3 / $6\text{dp}$) | **28 dp** (`extraLarge`) | **Sleep Timer clock dial dialog** (Set playback stop time) |
| **Input TimePicker** | `surfaceContainerHigh` (Level 3 / $6\text{dp}$) | **28 dp** (`extraLarge`) | Direct text input for exact hours/minutes |

### B. Selection Selector & Dial Tokens
* **Container Corner Radius**: **28 dp** (`shape.extraLarge`).
* **Hour / Minute Display Box (Selected)**: `primaryContainer` + `onPrimaryContainer` text.
* **Hour / Minute Display Box (Unselected)**: `surfaceContainerHighest` + `onSurface` text.
* **Clock Dial Diameter**: **256 dp** circular dial face (`tertiary` center pivot pin).

### C. Compose Sleep Timer TimePickerDialog Pattern

```kotlin
// Sleep Timer Dialog using M3 TimePicker
@Composable
fun SleepTimerPickerDialog(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(is24Hour = false)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text("Start Timer", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        },
        title = {
            Text("Set Sleep Timer", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            TimePicker(state = timePickerState)
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}
```

### D. Accessibility
* **Dial Accessibility**: D-pad navigation moves through clock dial numbers sequentially.
* **Time Announcement**: TalkBack announces formatted hour/minute selection (e.g. `"11 hours, 30 minutes PM, selected"`).

---

## 💬 14. Dialogs Specifications (Alert & Full-Screen Dialogs)

Based on official Material 3 Dialogs Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/dialogs/*`).

### A. Dialog Variant Matrix

| Dialog Variant | Container Token | Corner Radius | Width Boundaries | Primary Usage |
|:---|:---|:---|:---|:---|
| **Alert / Basic Dialog** | `surfaceContainerHigh` (Level 3 / $6\text{dp}$) | **28 dp** (`extraLarge`) | **280 dp – 560 dp** | Confirmations ("Delete Playlist", "Clear History") |
| **Full-Screen Dialog** | `surface` (Level 0 / $0\text{dp}$) | **0 dp** (Full screen) | 100% Window Width & Height | Complex tasks (Track Tag & Metadata Editor) |

### B. Dimensions, Scrim & Typography Tokens
* **Container Corner Radius**: **28 dp** (`shape.extraLarge`).
* **Container Color**: `surfaceContainerHigh` ($6\text{dp}$ tonal elevation).
* **Scrim Background**: Level 4 Scrim overlay (`Color.Black.copy(alpha = 0.32f)`).
* **Title Typography**: `headlineSmall` ($24\text{sp}$) with optional leading icon ($24\text{dp}$).
* **Body Typography**: `bodyMedium` ($14\text{sp}$) in `onSurfaceVariant`.

### C. Compose AlertDialog Pattern

```kotlin
// Confirmation Alert Dialog for Deleting Playlist
@Composable
fun DeletePlaylistConfirmDialog(
    playlistName: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Delete $playlistName?", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text(
                "This action will permanently delete the playlist from your library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmDelete()
                onDismiss()
            }) {
                Text("Delete", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}
```

## ➖ 15. Dividers Specifications (Horizontal & Vertical Inset Rules)

Based on official Material 3 Dividers Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/divider/*`).

### A. Divider Variant Matrix

| Variant Name | Orientation | Thickness | Color Token | Primary Usage |
|:---|:---|:---|:---|:---|
| **HorizontalDivider** (Full-Width) | Horizontal | **1 dp** | `outlineVariant` | Major section boundaries in settings/libraries |
| **HorizontalDivider** (Inset) | Horizontal | **1 dp** | `outlineVariant` | Separating track list items (Indented $72\text{dp}$ past artwork) |
| **VerticalDivider** | Vertical | **1 dp** | `outlineVariant` | Inline media controls, Split button divider line |

### B. Inset Spacing Rules
* **Start Inset**: Indent start edge by **56 dp – 72 dp** in list views to visually align divider with list item primary text instead of cutting through artwork thumbnails.

### C. Compose Implementation Pattern

```kotlin
// Inset Horizontal Divider for Track List
@Composable
fun TrackListItemDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp), // Inset past 48dp artwork + 16dp padding + 8dp spacing
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
```

### D. Accessibility
* **Decorative Semantics**: Dividers are purely visual grouping elements and are automatically hidden from screen reader accessibility focus trees (`semantics { isTraversalGroup = false }`).

---

## ⏳ 16. Loading & Progress Indicators Specifications

Based on official Material 3 Progress Indicators Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/progress-indicators/*`).

### A. Progress Indicator Variant Matrix

| Indicator Variant | Dimensions / Stroke | Track Color | Active Color | Stop Indicator | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **CircularProgressIndicator** | **48 dp × 48 dp** ($4\text{dp}$ stroke) | `surfaceContainerHighest` | `primary` | N/A | Lyrics fetching spinner, track buffering |
| **CircularProgressIndicator** (Small) | **24 dp × 24 dp** ($3\text{dp}$ stroke) | Transparent | `primary` | N/A | Inline button loading state |
| **LinearProgressIndicator** | **4 dp** Height (or $8\text{dp}$ Expressive) | `surfaceContainerHighest` | `primary` | $4\text{dp}$ Dot | **Media File Scanning Progress**, scrobble sync |

### B. Compose Determinate & Indeterminate Patterns

```kotlin
// 1. Determinate Linear Progress Indicator with Stop Dot for Media Scanning
@Composable
fun MediaScanProgressBar(progress: Float, scannedCount: Int, totalCount: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Scanning local audio...", style = MaterialTheme.typography.bodyMedium)
            Text("$scannedCount / $totalCount", style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            drawStopIndicator = {
                // Draws M3 4dp stop dot at progress end position
                drawStopIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    radius = 2.dp
                )
            }
        )
    }
}

// 2. Circular Progress Indicator for Lyrics Fetching
@Composable
fun LyricsLoadingSpinner() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            strokeWidth = 4.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.size(48.dp)
        )
    }
}
```

## 📜 17. Menus Specifications (Dropdown & Exposed Dropdown Menus)

Based on official Material 3 Menus Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/menus/*`).

### A. Menu Variant Matrix

| Menu Variant | Container Token | Corner Radius | Elevation | Primary Usage |
|:---|:---|:---|:---|:---|
| **DropdownMenu** | `surfaceContainer` | **12 dp** (`medium`) | **3 dp** (Level 2) | **Track Options Menu** ("Add to Playlist", "View Artist") |
| **ExposedDropdownMenu** | `surfaceContainer` | **12 dp** (`medium`) | **3 dp** (Level 2) | Audio DSP Preset selector, ReplayGain mode picker |

### B. Structural & Item Spacing Tokens
* **Width Boundaries**: Minimum **112 dp**, Maximum **280 dp** (or full text field width).
* **Item Height**: **48 dp** minimum height per menu item.
* **Leading Icon Size**: **24 dp × 24 dp** (`onSurfaceVariant`).

### C. Compose Track Options DropdownMenu Pattern

```kotlin
// Track Options Contextual Dropdown Menu
@Composable
fun TrackOptionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        colors = MenuDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        DropdownMenuItem(
            text = { Text("Play Next", style = MaterialTheme.typography.bodyLarge) },
            leadingIcon = { Icon(Icons.Default.QueuePlayNext, contentDescription = null) },
            onClick = {
                onPlayNext()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("Add to Playlist", style = MaterialTheme.typography.bodyLarge) },
            leadingIcon = { Icon(Icons.Default.PlaylistAdd, contentDescription = null) },
            onClick = {
                onAddToPlaylist()
                onDismiss()
            }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        DropdownMenuItem(
            text = { Text("Delete Track", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error) },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            onClick = {
                onDelete()
                onDismiss()
            }
        )
    }
}
```

### D. Accessibility
* **Menu Traversal**: Arrow keys move focus between menu items sequentially.
* **Dismiss Semantics**: Pressing `Escape` key or tapping outside dismisses the menu safely.

---

## 🧭 18. Complete Navigation Suite (NavigationBar, NavigationRail & NavigationDrawer)

Based on official Material 3 Navigation Bar, Navigation Rail, and Navigation Drawer Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/navigation-*`).

### A. Navigation Component Adaptive Matrix

| Navigation Component | Screen Orientation / Device | Container Dimensions | Container Token | Active Indicator Pill | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **NavigationBar** | Mobile Portrait | **80 dp** Height | `surfaceContainer` (3dp) | **64 dp × 32 dp** (`secondaryContainer`) | Primary 3–5 main tabs (Tracks, Albums, Artists, Settings) |
| **NavigationRail** | Tablet / Mobile Landscape | **80 dp** Width | `surface` (0dp) | **56 dp × 32 dp** (`secondaryContainer`) | Side vertical navigation bar for wide screens |
| **ModalNavigationDrawer** | Foldables / Tablets | **360 dp** Width | `surfaceContainerLow` (1dp) | Full row highlight ($28\text{dp}$ radius) | Slide-out library filter & playlist drawer |
| **DismissibleNavigationDrawer** | Desktop / Large Tablet | **360 dp** Width | `surface` (0dp) | Full row highlight ($28\text{dp}$ radius) | Permanent side library drawer |

### B. Structural & Active Indicator Tokens
* **Active Indicator Pill**: **64 dp × 32 dp** pill in `secondaryContainer` containing active filled icon in `onSecondaryContainer`.
* **Icon States**: Unselected icons use `Outline` variant; selected active icons use `Filled` variant.
* **Typography**: `labelMedium` ($12\text{sp}$) with medium weight.

### C. Compose Adaptive Navigation Pattern

```kotlin
// Adaptive App Navigation Layout (Mobile NavigationBar vs Tablet NavigationRail)
@Composable
fun AuraAdaptiveNavigation(
    currentDestination: String,
    onNavigate: (String) -> Unit,
    windowWidthSizeClass: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    val items = listOf("Tracks", "Albums", "Artists", "Settings")

    if (windowWidthSizeClass == WindowWidthSizeClass.Expanded) {
        // Landscape / Tablet Wide Screen -> NavigationRail
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                header = {
                    FloatingActionButton(onClick = { /* Quick Action */ }, containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play All")
                    }
                }
            ) {
                items.forEach { item ->
                    val selected = currentDestination == item
                    NavigationRailItem(
                        selected = selected,
                        onClick = { onNavigate(item) },
                        icon = {
                            Icon(
                                imageVector = if (selected) getFilledIcon(item) else getOutlinedIcon(item),
                                contentDescription = item
                            )
                        },
                        label = { Text(item, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) { content() }
        }
    } else {
        // Mobile Portrait -> Bottom NavigationBar
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 3.dp
                ) {
                    items.forEach { item ->
                        val selected = currentDestination == item
                        NavigationBarItem(
                            selected = selected,
                            onClick = { onNavigate(item) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) getFilledIcon(item) else getOutlinedIcon(item),
                                    contentDescription = item
                                )
                            },
                            label = { Text(item, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) { content() }
        }
    }
}
```

### D. Accessibility
* **Active Destination Announcement**: TalkBack automatically announces selected navigation destination (e.g. `"Tracks, Selected, Tab 1 of 4"`).
* **Target Box**: Every navigation item enforces a $48\text{dp} \times 48\text{dp}$ touch target area.

---

## 🔘 19. Radio Buttons Specifications (Mutually Exclusive Settings)

Based on official Material 3 Radio Button Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/radio-button/*`).

### A. Radio Button State Matrix

| Radio State | Outer Ring Token | Inner Dot Token | Primary Usage |
|:---|:---|:---|:---|
| **Selected** | $2\text{dp}$ `primary` stroke | $10\text{dp}$ `primary` solid dot | Active Audio Quality selection (Lossless FLAC) |
| **Unselected** | $2\text{dp}$ `onSurfaceVariant` stroke | Transparent (empty) | Inactive quality options (AAC / MP3) |

### B. Dimensions & Touch Target
* **Visual Outer Ring Diameter**: **20 dp**
* **Visual Inner Dot Diameter**: **10 dp**
* **Touch Target Area**: Minimum **$48\text{dp} \times 48\text{dp}$** touch target boundary.

### C. Compose SelectableGroup Pattern

```kotlin
// Mutually Exclusive Audio Streaming Quality Selector
@Composable
fun AudioQualityRadioGroup(
    selectedQuality: String,
    onSelectQuality: (String) -> Unit
) {
    val options = listOf("Lossless FLAC (24-bit / 96kHz)", "High Quality AAC (320 kbps)", "Standard MP3 (128 kbps)")

    Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
        options.forEach { option ->
            val isSelected = option == selectedQuality
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = { onSelectQuality(option) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null, // Handled by row selectable modifier
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### D. Accessibility
* **Group Context**: Wrap radio button rows inside `Modifier.selectableGroup()` so TalkBack announces radio index and total options (e.g. `"Lossless FLAC, selected, radio button 1 of 3"`).

---

## 🔍 20. Search Specifications (SearchBar & Full-Screen Expanded Search)

Based on official Material 3 Search Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/search/*`).

### A. Search Component Variant Matrix

| Search Variant | Container Height (Collapsed) | Container Token (Collapsed) | Corner Radius | Primary Usage |
|:---|:---|:---|:---|:---|
| **SearchBar** (Morphing) | **56 dp** | `surfaceContainerHigh` | **28 dp** (Pill) | **Library Search Bar** (Morps to full-screen results on focus) |
| **DockedSearchBar** | **56 dp** | `surfaceContainerHigh` | **28 dp** (Pill) | Desktop / Tablet docked search window |

### B. Structural & State Morphing Tokens
* **Collapsed Search Bar**: $56\text{dp}$ height pill in `surfaceContainerHigh` ($6\text{dp}$ tonal elevation).
* **Leading Icon Transition**: `Icons.Default.Search` (Collapsed) $\to$ `Icons.AutoMirrored.Filled.ArrowBack` (Expanded).
* **Trailing Actions**: Clear search query button (`Icons.Default.Clear`) + Voice Search icon.
* **Expanded Results View**: Smooth container transform expansion to 100% screen height over background content.

### C. Compose SearchBar Pattern

```kotlin
// Collapsible Full-Screen Library SearchBar
@Composable
fun LibrarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<Track>,
    onSelectTrack: (Track) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { isSearchActive = false },
        active = isSearchActive,
        onActiveChange = { isSearchActive = it },
        placeholder = { Text("Search tracks, albums, artists...", style = MaterialTheme.typography.bodyLarge) },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(onClick = { isSearchActive = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Search")
                }
            } else {
                Icon(Icons.Default.Search, contentDescription = "Search Library")
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear query")
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = Modifier.fillMaxWidth().padding(horizontal = if (isSearchActive) 0.dp else 16.dp)
    ) {
        // Expanded Search Results List
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults) { track ->
                ListItem(
                    headlineContent = { Text(track.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    supportingContent = { Text(track.artist, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = {
                        AsyncImage(model = track.artUri, contentDescription = null, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)))
                    },
                    modifier = Modifier.clickable {
                        onSelectTrack(track)
                        isSearchActive = false
                    }
                )
            }
        }
    }
}
```

### D. Accessibility
* **Live Results Announcement**: Screen readers announce result count changes as user types (e.g. `"5 tracks found for query Daft Punk"`).

---

## 🍞 21. Snackbar Specifications (Toast Notifications)

Based on official Material 3 Snackbar Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/snackbar/*`).

### A. Snackbar Component Matrix

| Component | Container Token | Content Token | Action Button Token | Corner Radius | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| **Snackbar** | `inverseSurface` | `inverseOnSurface` | `inversePrimary` | **12 dp** (`medium`) | Quick feedback toasts ("Added to Queue", "Lyrics Synced") |

### B. Dimensions & Elevation Tokens
* **Container Height**: Minimum **48 dp** height.
* **Tonal Elevation**: Level 3 Elevation (**6 dp**).
* **Container Shape**: **12 dp** (`shape.medium`) or $4\text{dp}$ (`shape.extraSmall`).
* **Horizontal Margin**: **16 dp** screen edge margins.

### C. Compose SnackbarHost Pattern

```kotlin
// Snackbar Host Implementation for Instant Audio Player Notifications
@Composable
fun AuraSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { snackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionColor = MaterialTheme.colorScheme.inversePrimary,
            dismissActionColor = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

// Triggering Snackbar Toast in ViewModel / Composable
// scope.launch {
//     snackbarHostState.showSnackbar(
//         message = "Track added to Play Queue",
//         actionLabel = "UNDO",
//         duration = SnackbarDuration.Short
//     )
// }
```

### D. Accessibility
* **Live Region Announcement**: TalkBack immediately reads snackbar toast messages via polite live region semantics.
* **Display Duration**: Minimum 4–7 seconds display duration ensures users with visual/cognitive impairments have adequate reading time.

---

## 🎚️ 22. Switch Specifications (DSP & Feature Toggles)

Based on official Material 3 Switch Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/switch/*`).

### A. Switch State & Dimensional Matrix

| Switch State | Track Token (52dp × 32dp) | Thumb Token & Dimensions | Thumb Icon | Primary Usage |
|:---|:---|:---|:---|:---|
| **Checked (On)** | `primary` container fill (No border) | **24 dp × 24 dp** (`onPrimary`) | `Icons.Default.Check` | **Equalizer ON**, Limiter ON, ReplayGain ON |
| **Unchecked (Off)** | `surfaceContainerHighest` + $2\text{dp}$ `outline` | **16 dp × 16 dp** (`outline`) | None | Feature disabled state |

### B. Dimensions & Touch Target
* **Visual Track Dimensions**: **52 dp × 32 dp** Full Pill (`9999.dp`).
* **Thumb Expansion Motion**: Unchecked thumb ($16\text{dp}$) expands smoothly to Checked thumb ($24\text{dp}$) when toggled ON.
* **Touch Target Area**: Minimum **$48\text{dp} \times 48\text{dp}$** touch target boundary.

### C. Compose Toggleable Switch Row Pattern

```kotlin
// Audio DSP Feature Switch Row with Merged Semantics
@Composable
fun DspFeatureSwitchRow(
    title: String,
    subtitle: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = enabled,
                onValueChange = onToggle,
                role = Role.Switch
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(16.dp))
        Switch(
            checked = enabled,
            onCheckedChange = null, // Handled by row toggleable modifier
            thumbContent = if (enabled) {
                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        )
    }
}
```

### D. Accessibility
* **Merged Row Semantics**: Entire setting row is wrapped with `Modifier.toggleable(role = Role.Switch)` so TalkBack reads: `"16-Band Equalizer, On, double tap to toggle"`.

---

## 📑 23. Tabs Specifications (Primary & Secondary Tab Rows)

Based on official Material 3 Tabs Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/tabs/*`).

### A. Tab Variant Matrix

| Tab Variant | Container Height | Active Indicator | Active Color | Primary Usage |
|:---|:---|:---|:---|:---|
| **Primary TabRow** (Fixed) | **48 dp** (Text) / **64 dp** (Icon+Text) | **3 dp** Underline Pill | `primary` | **Main Library Navigation** (Tracks, Albums, Artists) |
| **ScrollableTabRow** | **48 dp** | **3 dp** Underline Pill | `primary` | 5+ Library Tabs (Tracks, Albums, Artists, Playlists, Genres, Folders) |
| **Secondary TabRow** | **48 dp** | $2\text{dp}$ Bottom Stroke | `onSurface` | Sub-screen sections (Equalizer Presets vs Custom Sliders) |

### B. Dimensions & Active Indicator Tokens
* **Container Height**: **48 dp** (Text only) or **64 dp** (Icon above Text).
* **Active Underline Indicator**: **3 dp** thickness with top-rounded corners ($3\text{dp}$ radius).
* **Selected Text Token**: `primary` color with `titleMedium` ($16\text{sp}$) bold weight.
* **Unselected Text Token**: `onSurfaceVariant` color with `titleMedium` medium weight.

### C. Compose ScrollableTabRow Pattern

```kotlin
// Main Music Library Scrollable Tab Row
@Composable
fun LibraryTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = selectedTabIndex == index
            Tab(
                selected = selected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}
```

### D. Accessibility
* **Tab Traversal & Selection**: TalkBack announces tab index and selection state (e.g. `"Albums, Selected, Tab 2 of 6"`).
* **Swipe Connection**: Synchronize tab state with `HorizontalPager` so swiping pages updates active tab indicator smoothly.

---

## ✏️ 24. Text Fields Specifications (Filled & Outlined Input Fields)

Based on official Material 3 Text Fields Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/text-fields/*`).

### A. Text Field Variant Matrix

| Text Field Variant | Container Token | Border / Active Line | Corner Radius | Primary Usage |
|:---|:---|:---|:---|:---|
| **OutlinedTextField** | Transparent | $1\text{dp}$ `outline` ($2\text{dp}$ focused `primary`) | **4 dp** (`extraSmall`) | **Playlist creation**, tag editor, rename dialogs |
| **TextField** (Filled) | `surfaceContainerHighest` | $1\text{dp}$ Bottom line ($2\text{dp}$ focused `primary`) | **4 dp** Top corners | Quick settings input, inline forms |

### B. Dimensions & Focus / Error Tokens
* **Container Height**: Minimum **56 dp** height.
* **Focused State**: Border/Line turns **2 dp** `primary` stroke, floating label turns `primary` (`bodySmall` / $12\text{sp}$).
* **Error State**: Border/Line turns **2 dp** `error` stroke, supporting helper text turns `error`.
* **Leading / Trailing Icons**: **24 dp × 24 dp** (`onSurfaceVariant`).

### C. Compose OutlinedTextField Pattern

```kotlin
// Playlist Name Input Field inside Dialog
@Composable
fun PlaylistNameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Playlist Name") },
        placeholder = { Text("e.g. Chill Synthwave") },
        leadingIcon = { Icon(Icons.Default.QueueMusic, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear input text")
                }
            }
        },
        isError = errorMessage != null,
        supportingText = errorMessage?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
```

### D. Accessibility
* **Label Reading**: Screen readers announce field label, current text value, and error message state.
* **IME Actions**: Pair with `KeyboardOptions(imeAction = ImeAction.Done)` to allow IME keyboard dismissal.

---

## 🛠️ 25. Toolbars Specifications (Floating Batch Action Bar)

Based on official Material 3 Toolbars Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/toolbars/*`).

### A. Toolbar Component Matrix

| Component Variant | Height | Container Token | Shape Token | Primary Usage |
|:---|:---|:---|:---|:---|
| **Floating Action Toolbar** | **48 dp – 56 dp** | `surfaceContainerHigh` (Level 3 / $6\text{dp}$) | Full Pill (`9999.dp`) | **Multi-Selection Batch Action Bar** (Batch Play, Queue, Delete) |

### B. Dimensions & Elevation Tokens
* **Container Height**: **56 dp** (or $48\text{dp}$ dense).
* **Container Shape**: Full Pill (`RoundedCornerShape(9999.dp)`).
* **Shadow Elevation**: Level 3 Elevation (**6 dp**).
* **Action Icon Spacing**: **8 dp** gap between $40\text{dp} \times 40\text{dp}$ action icon buttons.

### C. Compose Floating Batch Selection Toolbar Pattern

```kotlin
// Floating Multi-Selection Batch Toolbar
@Composable
fun BatchSelectionToolbar(
    selectedCount: Int,
    onBatchPlay: () -> Unit,
    onBatchAddQueue: () -> Unit,
    onBatchDelete: () -> Unit,
    onClearSelection: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(9999.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shadowElevation = 6.dp,
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Default.Close, contentDescription = "Clear Selection")
            }
            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onBatchPlay) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Selected")
            }
            IconButton(onClick = onBatchAddQueue) {
                Icon(Icons.Default.QueueMusic, contentDescription = "Add Selected to Queue")
            }
            IconButton(onClick = onBatchDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
```

### D. Accessibility
* **Traversal Group**: Mark floating toolbar as an accessibility group (`semantics { isTraversalGroup = true }`) so screen readers navigate batch actions sequentially.

---

## 💬 26. Tooltips Specifications (Plain & Rich Tooltips)

Based on official Material 3 Tooltips Overview, Specs, Guidelines, and Accessibility (`m3.material.io/components/tooltips/*`).

### A. Tooltip Variant Matrix

| Tooltip Variant | Container Token | Content Token | Corner Radius | Primary Usage |
|:---|:---|:---|:---|:---|
| **Plain Tooltip** | `inverseSurface` | `inverseOnSurface` | **4 dp** (`extraSmall`) | **Icon Button labels on long-press** ("16-Band Equalizer", "ReplayGain") |
| **Rich Tooltip** | `surfaceContainer` | `onSurface` | **12 dp** (`medium`) | Explanatory DSP feature tooltips (Title + Body description) |

### B. Dimensions & Elevation Tokens
* **Plain Tooltip Height**: **24 dp – 32 dp** (Horizontal padding **8 dp**).
* **Rich Tooltip Width**: Max **320 dp** ($3\text{dp}$ tonal elevation).
* **Typography**: `labelMedium` ($12\text{sp}$) for Plain Tooltips; `titleSmall` ($14\text{sp}$) + `bodySmall` ($12\text{sp}$) for Rich Tooltips.

### C. Compose TooltipBox Pattern

```kotlin
// Plain Tooltip for Audio Control Icon Button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioControlIconButton(
    tooltipText: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                shape = RoundedCornerShape(4.dp),
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface
            ) {
                Text(tooltipText, style = MaterialTheme.typography.labelMedium)
            }
        },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
            Icon(icon, contentDescription = tooltipText)
        }
    }
}
```

### D. Accessibility
* **Screen Reader Integration**: Tooltip text automatically populates as `contentDescription` fallback for icon buttons.
* **Dismiss Semantics**: Tapping outside or scrolling dismisses active tooltip smoothly.

















