# Material 3 Design Primitives & Foundations

## 🎨 1. HCT Color System & Dynamic Tonal Palettes

Material 3 uses the **HCT (Hue, Chroma, Tone)** color space instead of RGB or HSL. This ensures perceptual lightness and consistent contrast across dark and light themes.

### Tonal Roles & Color Tokens

```
  PRIMARY TONAL PALETTE        SECONDARY TONAL PALETTE       TERTIARY TONAL PALETTE
  [Primary] [OnPrimary]        [Secondary] [OnSecondary]     [Tertiary] [OnTertiary]
  [PrimaryContainer]           [SecondaryContainer]          [TertiaryContainer]
  [OnPrimaryContainer]         [OnSecondaryContainer]        [OnTertiaryContainer]

  SURFACE CONTAINERS & BACKGROUNDS
  [SurfaceDim]         [Surface]          [SurfaceBright]
  [SurfaceContainerLow] [SurfaceContainer] [SurfaceContainerHigh] [SurfaceContainerHighest]
  [OnSurface]          [OnSurfaceVariant] [Outline]           [OutlineVariant]
```

### Color Token Mapping Table

| Token Name | Dark Value (Baseline) | Usage / Intent |
|:---|:---|:---|
| `primary` | `Color(0xFFD0BCFF)` | Key active actions, prominent buttons, active tab indicators |
| `onPrimary` | `Color(0xFF381E72)` | Text/icons rendered on top of `primary` |
| `primaryContainer` | `Color(0xFF4F378B)` | Standout container fills, FABs, selected chips |
| `onPrimaryContainer` | `Color(0xFFEADDFF)` | Text/icons rendered on top of `primaryContainer` |
| `secondary` | `Color(0xFFCCC2DC)` | Less prominent components, filter chips, secondary toggles |
| `tertiary` | `Color(0xFFEFB8C8)` | Contrasting accent for highlights, badges, progress bars |
| `surfaceDim` | `Color(0xFF141218)` | Lowest elevation background layer |
| `surface` | `Color(0xFF141218)` | Default background |
| `surfaceContainerLow` | `Color(0xFF1D1B20)` | Cards sitting directly on the background |
| `surfaceContainer` | `Color(0xFF211F26)` | Default cards, modals, and sheets |
| `surfaceContainerHigh` | `Color(0xFF2B2930)` | Elevated sheets, navigation bars, player controls |
| `surfaceContainerHighest` | `Color(0xFF36343B)` | Text field inputs, active selection fills |
| `onSurface` | `Color(0xFFE6E0E9)` | High emphasis body text and icons |
| `onSurfaceVariant` | `Color(0xFFCAC4D0)` | Secondary text, captions, inactive icons |
| `outline` | `Color(0xFF938F99)` | Important borders, text field outlines |
| `outlineVariant` | `Color(0xFF49454F)` | Subtle dividers, card borders |

---

## 📐 2. Shape Scale & Corner Radii

Material 3 defines a strict geometric shape scale for component containers:

```kotlin
val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Tooltips, small badges
    small = RoundedCornerShape(8.dp),        // Chips, small buttons, text field outlines
    medium = RoundedCornerShape(12.dp),      // Cards, alert dialogs, dropdown menus
    large = RoundedCornerShape(16.dp),       // Extended FABs, floating bottom bars
    extraLarge = RoundedCornerShape(28.dp),  // Bottom sheets, full-screen dialogs
)
```

### Morphing & Dynamic Radii
* Components transitioning state (e.g., collapsed mini-player to full-screen player) morph smoothly between `Shape` values using `CornerBasedShape` interpolation or animated clipping.

---

## ✏️ 3. Typography Token Scale

Material 3 uses 5 type scales with 3 size variants each (15 total type roles):

| Role | Font Size | Line Height | Weight | Usage |
|:---|:---|:---|:---|:---|
| `displayLarge` | 57sp | 64sp | Regular | Hero stats, key visual numbers |
| `displayMedium` | 45sp | 52sp | Regular | Primary header visuals |
| `displaySmall` | 36sp | 44sp | Regular | Large title displays |
| `headlineLarge` | 32sp | 40sp | Bold | Full-screen player track title |
| `headlineMedium` | 28sp | 36sp | SemiBold | Section headers |
| `headlineSmall` | 24sp | 32sp | SemiBold | Modal & dialog titles |
| `titleLarge` | 22sp | 28sp | Medium | Top app bar titles |
| `titleMedium` | 16sp | 24sp | Medium | Card titles, list headers |
| `titleSmall` | 14sp | 20sp | Medium | Subheaders, chip labels |
| `bodyLarge` | 16sp | 24sp | Regular | Primary body text, lyrics lines |
| `bodyMedium` | 14sp | 20sp | Regular | Secondary description text |
| `bodySmall` | 12sp | 16sp | Regular | Captions, timestamps, metadata |
| `labelLarge` | 14sp | 20sp | Medium | Button labels, active tab text |
| `labelMedium` | 12sp | 16sp | Medium | Badges, quality tags (FLAC/24-bit) |
| `labelSmall` | 11sp | 16sp | Medium | Micro badges, secondary tags |

---

## 🎬 4. Motion System & Physics Specifications

Material 3 Expressive motion prioritizes **Spring Physics** over linear durations for organic, tactile feedback.

### Motion Easing Curves & Durations
```kotlin
object M3Motion {
    // Easing Curves
    val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    // Standard Durations
    const val Short1 = 50   // Micro-presses
    const val Short2 = 100  // Icon state toggles
    const val Medium1 = 250 // Card expansion
    const val Medium2 = 300 // Sheet expansion
    const val Long1 = 450   // Screen transitions
    const val Long2 = 500   // Complex layout morphs

    // Expressive Springs
    val BouncySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val SmoothSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}
```

---

## 👁️ 5. Accessibility & Contrast Standards

1. **Body & Metadata Text**: Minimum contrast ratio of **4.5:1** against the background.
2. **Interactive Controls**: Minimum contrast ratio of **3.0:1** for touch targets and icon buttons.
3. **Touch Targets**: All interactive elements must maintain a minimum touch target area of **$48\text{dp} \times 48\text{dp}$**.
4. **Color Independence**: Never rely solely on color to convey state — always pair color changes with shape morphs, icons, or text indicators (e.g., active dot under shuffle button).
