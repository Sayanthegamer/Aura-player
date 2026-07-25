# Material 3 Spacing System & Grid Specifications

Based on official [Material 3 Spacing Overview](https://m3.material.io/styles/spacing/overview), [Applying Spacing](https://m3.material.io/styles/spacing/applying-spacing), and [Spacing Tokens](https://m3.material.io/styles/spacing/tokens).

## 📏 1. Spacing Principles & Grid Architecture

Material 3 Spacing uses two complementary grid systems:
1. **8dp Baseline Grid**: Primary spatial grid for layout components, card margins, list item heights, and container padding.
2. **4dp Micro-Grid**: Fine-grained grid used exclusively for inline element gaps (e.g. icon-to-text spacing, chip padding, badge offsets).

---

## 🔢 2. Complete M3 Spacing Token Scale

| Spacing Token Name | Value | Recommended Usage |
|:---|:---|:---|
| `spacing.none` | **0 dp** | Edge-to-edge container alignment |
| `spacing.micro` | **2 dp** | Micro badge padding, tiny tag stroke offset |
| `spacing.extraSmall` | **4 dp** | Icon-to-label inline gaps, chip internal vertical padding |
| `spacing.small` | **8 dp** | Gap between chips in a FlowRow, internal card element spacing |
| `spacing.medium` | **12 dp** | List item horizontal padding, button-to-button gaps |
| `spacing.large` | **16 dp** | Standard screen edge margin (mobile), card-to-card vertical gap |
| `spacing.extraLarge` | **24 dp** | Major section dividers, screen margin (tablet), sheet top margin |
| `spacing.xxl` | **32 dp** | Hero album art top padding, empty state illustration margins |
| `spacing.xxxl` | **48 dp** | Minimum interactive touch target boundary |
| `spacing.huge` | **64 dp** | Large hero header vertical offsets |

---

## 🛠️ 3. Jetpack Compose Spacing Implementation

```kotlin
import androidx.compose.ui.unit.dp

object M3Spacing {
    val None = 0.dp
    val Micro = 2.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val XXL = 32.dp
    val TouchTarget = 48.dp
    val Huge = 64.dp
}
```

---

## 📐 4. Applying Spacing: Layout Rules

### A. Screen Edge Margins
* **Mobile (< 600dp width)**: $16\text{dp}$ outer screen padding.
* **Tablet / Foldable (≥ 600dp width)**: $24\text{dp}$ outer screen padding.

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = M3Spacing.Large, vertical = M3Spacing.Medium)
) {
    // Screen content
}
```

### B. List Item & Grid Gaps
* **List Row Spacing**: Use `Arrangement.spacedBy(M3Spacing.Small)` ($8\text{dp}$) between media list items.
* **Grid Gaps**: Use `Arrangement.spacedBy(M3Spacing.Large)` ($16\text{dp}$) between album grid cards.

```kotlin
LazyColumn(
    verticalArrangement = Arrangement.spacedBy(M3Spacing.Small),
    contentPadding = PaddingValues(bottom = M3Spacing.Huge)
) {
    // Media list items
}
```

### C. Touch Target Enforcement
All interactive controls (icon buttons, checkboxes, sliders) must maintain a minimum touch target box of **$48\text{dp} \times 48\text{dp}$**, even if the visual icon size is $24\text{dp}$:

```kotlin
IconButton(
    onClick = onPlayPause,
    modifier = Modifier.size(M3Spacing.TouchTarget) // Enforces 48dp minimum touch target
) {
    Icon(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Play",
        modifier = Modifier.size(24.dp)
    )
}
```
