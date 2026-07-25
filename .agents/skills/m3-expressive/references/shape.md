# Material 3 Shape System & Corner Scale Specification

Based on official [Material 3 Corner Radius Scale](https://m3.material.io/styles/shape/corner-radius-scale) and [Shape Morphing Guidelines](https://m3.material.io/styles/shape/shape-morph).

## 📐 1. Shape Principles

1. **Expressive**: Shape choices define component personality, component boundaries, and visual emphasis.
2. **Flexible**: Corner radii adapt and morph across layout transitions, screen sizes, and interactive states.
3. **Systematic**: A standardized 7-level corner radius scale brings visual harmony across all Android UI components.

---

## 🟩 2. The 7-Level M3 Shape Scale

| Shape Family Token | Radius Value | Component Mapping |
|:---|:---|:---|
| `shape.none` | `0.dp` | Full-screen app canvas, edge-to-edge root backgrounds |
| `shape.extraSmall` | `4.dp` | Tooltips, small badges, snackbar containers |
| `shape.small` | `8.dp` | Assist/Filter chips, album thumbnail clips, small buttons |
| `shape.medium` | `12.dp` | Cards, popover menus, alert dialogs |
| `shape.large` | `16.dp` | Prominent cards, floating action buttons (FABs), player cards |
| `shape.extraLarge` | `28.dp` | Modal bottom sheets, navigation drawers, full-screen modals |
| `shape.full` | `9999.dp` / `CircleShape` | Filled buttons, search bars, active indicator pills, play/pause controls |

---

## 📐 3. Nested Container Radius Math

To maintain concentric visual alignment when placing a child container inside a parent container, follow the **Concentric Corner Radius Formula**:

$$R_{\text{child}} = \max(0\text{dp},\, R_{\text{parent}} - P)$$

Where:
* $R_{\text{parent}}$ = Outer container corner radius
* $P$ = Internal padding between parent container and child element
* $R_{\text{child}}$ = Inner child corner radius

```
┌─────────────────────────────────────────┐  R_parent = 16dp
│  PADDING (P = 4dp)                      │
│   ┌─────────────────────────────────┐   │
│   │                                 │   │  R_child = 16dp - 4dp = 12dp
│   │   Album Art / Child Image       │   │  (Concentric concentric alignment)
│   │                                 │   │
│   └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

*Example*: If a player card has $R_{\text{parent}} = 16\text{dp}$ and internal padding $P = 4\text{dp}$, the inner album art thumbnail radius must be $R_{\text{child}} = 12\text{dp}$.

---

## 🔄 4. Advanced Shape Morphing Mechanics

Shape morphing creates smooth visual continuity when components change state or size.

### A. Corner Radius Interpolation (MiniPlayer → FullPlayer)
Animate container corner radii smoothly during screen transitions using Compose `animateDpAsState` or `Transition`:

```kotlin
@Composable
fun MorphingPlayerCard(
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "ShapeMorph"
    )

    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier
    ) {
        // Player Content
    }
}
```

### B. Asymmetrical Keyframe Morphing (Bottom Sheet Reveal)
Morph specific corners independently during multi-phase transitions:

```kotlin
@Composable
fun AsymmetricBottomSheetMorph(
    expansionProgress: Float // 0f (collapsed) to 1f (fully expanded)
) {
    // Top corners morph from 28dp (sheet) to 0dp (full screen)
    val topRadius = lerp(28.dp, 0.dp, expansionProgress)

    Surface(
        shape = RoundedCornerShape(
            topStart = topRadius,
            topEnd = topRadius,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        // Sheet Content
    }
}
```

### C. Press & Selection Tactile Morphing
During user interaction (press, drag, or focus), components morph slightly to provide immediate feedback:
* **Press State**: Buttons or cards morph slightly towards tighter radii (e.g. $16\text{dp} \to 12\text{dp}$) paired with scale reduction (`scale(0.96f)`).
* **Selection State**: Active filters or selected list items morph to a higher emphasis radius token.
