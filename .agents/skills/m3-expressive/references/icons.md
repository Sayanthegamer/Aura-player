# Material 3 Iconography & Variable Symbol Specifications

Based on official [Material 3 Icons Overview](https://m3.material.io/styles/icons/overview), [Designing Icons](https://m3.material.io/styles/icons/designing-icons), and [Applying Icons](https://m3.material.io/styles/icons/applying-icons).

## 🎨 1. Iconography Principles

1. **Clear & Universal**: Icons must communicate core actions, navigation destinations, and media controls instantly without ambiguity.
2. **Outlined vs. Filled Active States**:
   * **Outlined**: Default unselected state (inactive tabs, unselected toggles).
   * **Filled**: Selected / Active state (active navigation tab, enabled shuffle/repeat mode, favorited track).
3. **Variable Axes**: Material Symbols use 4 variable axes (`FILL`, `wght`, `GRAD`, `opsz`) for precise optical alignment.

---

## 📏 2. Icon Sizing Token Scale

| Icon Size Token | Dimensions | Touch Target Box | Primary Usage |
|:---|:---|:---|:---|
| `icon.small` | **18 dp** | $32\text{dp} \times 32\text{dp}$ | Inline metadata tags, list ondersteuning text |
| `icon.medium` | **24 dp** | **$48\text{dp} \times 48\text{dp}$** | Standard top bar, navigation rail, list action buttons |
| `icon.large` | **32 dp** | $48\text{dp} \times 48\text{dp}$ | Secondary player controls (Skip Next / Prev, Shuffle) |
| `icon.extraLarge` | **48 dp** | $64\text{dp} \times 64\text{dp}$ | Hero Play / Pause central control |

---

## 🛠️ 3. Variable Axes Specifications

```kotlin
// Material Symbols Variable Axes Definitions
object M3IconAxes {
    // Fill Axis: 0 = Outlined, 1 = Filled
    const val FillUnselected = 0f
    const val FillSelected = 1f

    // Weight Axis (wght): 100 to 700 (Default = 400)
    const val WeightDefault = 400f
    const val WeightEmphasis = 600f

    // Grade Axis (GRAD): -25 to 200 (Default = 0)
    const val GradeDefault = 0f

    // Optical Size (opsz): Matches visual icon size in dp
    const val OpszSmall = 20f
    const val OpszMedium = 24f
    const val OpszLarge = 40f
    const val OpszExtraLarge = 48f
}
```

---

## 🧩 4. Applying Icons in Jetpack Compose

### A. Active vs Inactive State Morphing (Outlined ↔ Filled)

```kotlin
@Composable
fun ToggleableIconButton(
    isActive: Boolean,
    onToggle: () -> Unit,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    contentDescription: String,
    accentColor: Color
) {
    val iconTint by animateColorAsState(
        targetValue = if (isActive) accentColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 200)
    )

    IconButton(
        onClick = onToggle,
        modifier = Modifier.size(48.dp) // Enforces 48dp M3 touch target
    ) {
        Icon(
            imageVector = if (isActive) activeIcon else inactiveIcon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}
```

### B. Icon & Label Alignment Rules
When placing an icon alongside text (e.g., in a Button, Chip, or Header):
* **Inline Gap**: Use $8\text{dp}$ (`M3Spacing.Small`) between icon and label text.
* **Optical Alignment**: Align icon vertically to the baseline center of the accompanying text line.
