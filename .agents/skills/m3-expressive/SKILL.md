---
name: m3-expressive
description: Official Material 3 (M3) Expressive design system guidance for Jetpack Compose, focusing on dynamic color palettes, spring motion, surface containers, and expressive typography.
---

# Material 3 (M3) Expressive Design Guidance

This skill provides authoring and implementation guidelines based on official [Material Design 3 (m3.material.io)](https://m3.material.io) standards for Jetpack Compose UIs.

## 🎨 1. Dynamic Color & Surface Hierarchy

Material 3 replaces rigid background layers with a 5-tier **Surface Container** hierarchy:

| Token Name | Intended Usage | Compose Equivalent |
|:---|:---|:---|
| `surfaceDim` | Lowest elevation background | `MaterialTheme.colorScheme.surfaceDim` |
| `surface` | Base app background | `MaterialTheme.colorScheme.surface` |
| `surfaceContainerLow` | Low elevation card / grouping | `MaterialTheme.colorScheme.surfaceContainerLow` |
| `surfaceContainer` | Standard cards & dialogue boxes | `MaterialTheme.colorScheme.surfaceContainer` |
| `surfaceContainerHigh` | Higher emphasis cards & sheets | `MaterialTheme.colorScheme.surfaceContainerHigh` |
| `surfaceContainerHighest` | Topmost controls & inputs | `MaterialTheme.colorScheme.surfaceContainerHighest` |

### Palette Extraction Rules
* Always derive `accent`, `dominant`, and `secondary` colors from album artwork using `AndroidX Palette`.
* Use `animateColorAsState` with a `tween(durationMillis = 800, easing = FastOutSlowInEasing)` to prevent abrupt color snaps during track navigation.
* Ensure text contrast ratio is $\ge 4.5:1$ against custom or extracted surfaces.

---

## 🌊 2. Expressive Motion & Micro-Interactions

Expressive UIs rely on spring physics instead of rigid linear timelines:

### Spring Physics Presets
```kotlin
// Bouncy tactile response for icon buttons & FABs
val BouncySpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

// Smooth layout transition for sheets & screens
val ExpressiveTransition = tween<Float>(
    durationMillis = 600,
    easing = FastOutSlowInEasing
)
```

### Motion Best Practices
* **Micro-Feedback**: Shrink/scale interactive buttons slightly (`scale(0.92f)`) on press state.
* **Continuous Background Motion**: Use infinite transitions for ambient backgrounds (e.g., blurred Gaussian motion with 14s/18s cycle periods).
* **Zero-Flash Navigation**: Pre-calculate and pass animated color states down from host activities to prevent default theme flashing when launching screens.

---

## ✏️ 3. Expressive Typography & Hierarchy

Follow strict heading hierarchy:
* **`HeadlineLarge` / `TitleLarge`**: 24sp–32sp bold, extra weight for song titles and primary headers.
* **`LabelMedium` / `BodyMedium`**: 12sp–14sp medium weight for secondary metadata (artist, album, bitrate).
* **Truncation Safety**: Always set `maxLines = 1`, `overflow = TextOverflow.Ellipsis`, and apply `Modifier.weight(1f)` on title text containers inside header rows to prevent pushing action buttons off-screen.

---

## 🧩 4. Component Patterns

* **Floating Bottom Player**: Semi-transparent elevated surface (`surfaceContainerHigh`) with $24\text{dp}$ corner rounding and subtle border stroke (`BorderStroke(1.dp, color.copy(alpha = 0.1f))`).
* **Active State Indicators**: Highlight active mode toggles (such as Shuffle or Repeat) using a combined **Accent Color Tint + 4dp Dot Indicator** beneath the icon.
* **Audio DSP Controls**: Use continuous sliders (`SliderDefaults.colors`) with dynamic accent fills and numeric value labels on touch.
