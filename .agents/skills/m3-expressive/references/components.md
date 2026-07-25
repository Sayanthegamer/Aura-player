# Material 3 Component Specifications

## 🔘 1. Buttons & Floating Action Buttons (FAB)

| Component | Container Color | Content Color | Corner Radius | Usage |
|:---|:---|:---|:---|:---|
| **FilledButton** | `primary` | `onPrimary` | Full (9999dp) | Primary action (Play, Save, Confirm) |
| `FilledTonalButton` | `secondaryContainer` | `onSecondaryContainer` | Full (9999dp) | Medium-emphasis actions (Shuffle All) |
| `OutlinedButton` | Transparent + `outline` stroke | `primary` | Full (9999dp) | Secondary non-destructive actions |
| `TextButton` | Transparent | `primary` | Full (9999dp) | Dialog actions, inline text links |
| `FloatingActionButton` | `primaryContainer` | `onPrimaryContainer` | 16dp (Large) | Primary screen action |
| `SmallFloatingActionButton` | `primaryContainer` | `onPrimaryContainer` | 12dp (Medium) | Secondary floating action |

---

## 🃏 2. Card Containers

```kotlin
// Filled Card (Default list items)
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ),
    shape = RoundedCornerShape(12.dp)
)

// Elevated Card (Highlighted items, active media)
Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ),
    shape = RoundedCornerShape(16.dp)
)

// Outlined Card (Selected presets / settings)
OutlinedCard(
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(16.dp)
)
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

### D. Accessibility Standards
* **Action Target Box**: Every action and navigation icon in the app bar must maintain a minimum touch target area of **$48\text{dp} \times 48\text{dp}$**.
* **Icon Labels**: All app bar icon buttons must provide localized, descriptive `contentDescription` strings for screen readers.

