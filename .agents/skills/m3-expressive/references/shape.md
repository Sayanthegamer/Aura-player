# Material 3 Shape System & Corner Scale Specification

Based on official [Material 3 Shape Principles](https://m3.material.io/styles/shape/overview-principles).

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

## 🔷 3. Jetpack Compose M3 Shape Scale Implementation

```kotlin
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val M3ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
```

---

## 🎨 4. Asymmetric Corners & Dynamic Shape Morphing

### Asymmetric Corner Styling
Material 3 permits asymmetrical corner radii to indicate directional flow or container attachment:

```kotlin
// Bottom Sheet Top-Rounded Container
val SheetTopRoundedShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

// Floating Player Card with Directional Anchor
val PlayerFloatingCardShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)
```

### Dynamic Shape Morphing Guidelines
* **State Change**: When a card or sheet expands into a full screen, animate the corner radius from `16.dp` (or `28.dp`) to `0.dp` using `animateDpAsState` with an `Emphasized` easing curve.
* **Selection State**: Selected or active cards can morph to a slightly tighter or wider corner radius to reinforce tactile selection feedback.
