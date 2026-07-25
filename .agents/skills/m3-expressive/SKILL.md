---
name: m3-expressive
description: Official Material 3 (M3) Expressive design system guidance for Jetpack Compose, covering design primitives, HCT color palettes, motion physics, component specifications, and dynamic media interfaces.
---

# Material 3 (M3) Expressive Design Skill

This skill provides full-spectrum guidelines based on official [Material Design 3 (m3.material.io)](https://m3.material.io) standards for Jetpack Compose applications.

## 📚 Reference Documentation Index

Detailed specifications are organized into focused reference guides:

* 🎨 **[Foundations & Primitives](references/foundations.md)**: HCT Color Space, 5-Tier Surface Container Roles, Shape Scale, Typography Scale (15 Roles), Motion Physics & Accessibility.
* 🧩 **[Component Specifications](references/components.md)**: Buttons, Cards, Sliders, Bottom Sheets, Chips, and ListItem Structural Specifications.
* 🌊 **[Expressive Extensions](references/expressive.md)**: Dynamic Palette Engine, Gaussian Brownian Motion Backgrounds, and Word-Level Karaoke Lyric Highlights.

---

## ⚡ Core Rules & Authoring Guidelines

### 1. Dynamic Surface & Color Container Rules
* **Never use hardcoded background colors**: Map container backgrounds to `MaterialTheme.colorScheme.surfaceContainer` family tokens (`surfaceDim`, `surfaceContainerLow`, `surfaceContainer`, `surfaceContainerHigh`, `surfaceContainerHighest`).
* **Dynamic Palette Guard**: Clamp background lightness (`hsl[2] <= 0.15f`) to ensure body text maintains a minimum contrast ratio of **$\ge 4.5:1$**.
* **Color Animation**: Animate dynamic color changes using `animateColorAsState` with a $800\text{ms}$ `FastOutSlowInEasing` curve to prevent hard snaps when changing tracks or wallpapers.

### 2. Motion Physics & Touch Feedback
* **Spring Animations**: Use `spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)` for icon buttons and tactile touch targets.
* **Touch Target Area**: Ensure interactive components maintain a minimum touch target size of **$48\text{dp} \times 48\text{dp}$**.
* **State Feedback**: Always pair color changes with visual shape, scale, or indicator dots (e.g., active 4dp dot below shuffle button).

### 3. Layout Safety & Truncation
* **Header Text Safety**: Apply `Modifier.weight(1f)`, `maxLines = 1`, and `TextOverflow.Ellipsis` on title/subtitle text elements in row headers to prevent pushing trailing action buttons off-screen.
* **Navigation Flash Elimination**: Pass animated palette colors down directly from host activities to prevent default theme flashing during screen transitions.
