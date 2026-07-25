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

## 🎚️ 3. Sliders & Audio Progress Controls

### Continuous Slider Spec (Audio Position & DSP Bands)
* **Track Height**: $4\text{dp}$ inactive, $8\text{dp}$ active/pressed.
* **Thumb Radius**: $10\text{dp}$ ($20\text{dp}$ diameter) with touch target $\ge 48\text{dp}$.
* **Colors**: `activeTrackColor = primary`, `inactiveTrackColor = surfaceContainerHighest`, `thumbColor = primary`.

```kotlin
Slider(
    value = positionMs.toFloat(),
    onValueChange = { onSeek(it.toLong()) },
    valueRange = 0f..durationMs.toFloat(),
    colors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
    ),
    modifier = Modifier.fillMaxWidth().height(48.dp)
)
```

---

## 📑 4. Sheets & Dialogs

### Modal Bottom Sheet (Audio DSP / Lyrics Options)
* **Container**: `surfaceContainerLow` or `surfaceContainer`
* **Drag Handle**: $32\text{dp} \times 4\text{dp}$ pill in `onSurfaceVariant.copy(alpha = 0.4f)`
* **Shape**: `RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)`

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

## 📋 6. List Items (Track / Album Rows)

Use standard 3-line structural layout for media lists:

```kotlin
ListItem(
    headlineContent = {
        Text(
            text = track.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    supportingContent = {
        Text(
            text = "${track.artistName} • ${track.albumName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    leadingContent = {
        // 48dp Rounded Album Art Thumbnail
        AsyncImage(
            model = track.albumArtUri,
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp))
        )
    },
    trailingContent = {
        IconButton(onClick = { /* More actions */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
    },
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

### D. Accessibility
* **Screen Reader Announcement**: Always merge the badge's numeric count into the parent icon's `contentDescription` (e.g. `"Queue, 12 items"`) so TalkBack announces the combined state.


