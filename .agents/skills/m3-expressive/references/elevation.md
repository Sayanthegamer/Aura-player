# Material 3 Elevation & Tonal Surface Specifications

Based on official [Material 3 Elevation Overview](https://m3.material.io/styles/elevation/overview), [Applying Elevation](https://m3.material.io/styles/elevation/applying-elevation), and [Elevation Tokens](https://m3.material.io/styles/elevation/tokens).

## 🏔️ 1. Elevation Principles

1. **Tonal Elevation & Color Overlays**: Material 3 uses **surface color shifts** (`surfaceContainer` tiers) as the primary indicator of elevation, replacing heavy traditional drop shadows.
2. **Dynamic Surface Shifts**: As components increase in elevation, their surface tint brightens slightly in dark mode and receives subtle ambient shadow overlays.
3. **Scrim Hierarchy**: High-elevation overlays (modal bottom sheets, full-screen dialogs) dim lower content layers with an semi-transparent scrim (`Color(0x99000000)`).

---

## 📊 2. The 6-Level M3 Elevation Scale

| Elevation Level | Value (dp) | Container Token | Component Mapping |
|:---|:---|:---|:---|
| **Level 0** | **0 dp** | `surface` / `surfaceDim` | Root app background canvas, flat list containers |
| **Level 1** | **1 dp** | `surfaceContainerLow` | Subtle cards, list items on scroll, top bar on scroll |
| **Level 2** | **3 dp** | `surfaceContainer` | Standard cards, floating mini-player bar, elevated buttons |
| **Level 3** | **6 dp** | `surfaceContainerHigh` | Floating Action Buttons (FABs), alert dialogs |
| **Level 4** | **8 dp** | `surfaceContainerHighest` | Modal bottom sheets, navigation drawers |
| **Level 5** | **12 dp** | `surfaceContainerHighest` + Shadow | Tooltips, popover menus, pickers |

---

## 🛠️ 3. State Elevation Transitions

Components shift elevation state dynamically during user interaction:

```kotlin
@Composable
fun InteractiveElevatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Elevate card on press / touch
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = if (isPressed) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        content = content
    )
}
```

---

## 📐 4. Scrim Overlay Rules

When modal components (Level 4/5) appear over lower content:
* **Scrim Color**: Use `MaterialTheme.colorScheme.scrim` (`Color.Black.copy(alpha = 0.6f)`).
* **Blur Effect**: On supported API levels (Android 12+), pair the scrim with a `Modifier.blur(16.dp)` background blur on lower content layers for an expressive glassmorphic depth effect.
