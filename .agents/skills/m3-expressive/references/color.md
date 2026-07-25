# Material 3 Color System Architecture & HCT Specification

Based on official [Material 3 Color System Overview](https://m3.material.io/styles/color/system/overview), [How the Color System Works](https://m3.material.io/styles/color/system/how-the-system-works), and [Color Roles](https://m3.material.io/styles/color/roles).

## 🎨 1. The HCT Color Model (Hue, Chroma, Tone)

Material 3 uses the **HCT (Hue, Chroma, Tone)** color space instead of RGB, HSL, or HSV. HCT combines the perceptual accuracy of CAM16 with the accessibility guarantees of L*a*b*.

```
   HUE (H)           CHROMA (C)            TONE (T)
   Color Angle       Color Purity /        Perceptual Lightness
   0° to 360°        Intensity (0 to 120+) 0 = Black, 100 = White
```

---

## 🌈 2. The 5 Core Tonal Palettes

From a single seed color (extracted from artwork or system wallpaper), the M3 algorithm generates **5 core Tonal Palettes**:

```
                       SEED COLOR (Hue H, Chroma C)
                                    │
    ┌───────────────┬───────────────┼───────────────┬───────────────┐
    ▼               ▼               ▼               ▼               ▼
Primary         Secondary       Tertiary        Neutral         Neutral Variant
(H, C)          (H, C/3)        (H + 60°, C)    (H, C/12)       (H, C/8)
Key Accent      Muted Details   Contrast Accent Backgrounds     Borders & Outlines
```

Each Tonal Palette produces 13 tone levels: **T0 (Black), T10, T20, T30, T40, T50, T60, T70, T80, T90, T95, T99, T100 (White)**.

---

## 🏛️ 3. Complete Color Roles Reference Table

### A. Key Accent Color Groups

| Color Role | Dark Theme Tone | Light Theme Tone | Primary UI Usage |
|:---|:---|:---|:---|
| `primary` | **Tone 80** | **Tone 40** | Primary buttons, active play state, active tab indicators |
| `onPrimary` | **Tone 20** | **Tone 100** | Text and icons rendered on top of `primary` |
| `primaryContainer` | **Tone 30** | **Tone 90** | Prominent container fills, FABs, selected chips |
| `onPrimaryContainer` | **Tone 90** | **Tone 10** | Text and icons rendered on top of `primaryContainer` |
| `secondary` | **Tone 80** | **Tone 40** | Filter chips, secondary toggles, badge fills |
| `onSecondary` | **Tone 20** | **Tone 100** | Text and icons rendered on top of `secondary` |
| `secondaryContainer` | **Tone 30** | **Tone 90** | Unselected elevated card fills, secondary buttons |
| `onSecondaryContainer` | **Tone 90** | **Tone 10** | Text on top of `secondaryContainer` |
| `tertiary` | **Tone 80** | **Tone 40** | Highlighting badges, audio DSP EQ band highlights, progress bars |
| `onTertiary` | **Tone 20** | **Tone 100** | Text on top of `tertiary` |
| `tertiaryContainer` | **Tone 30** | **Tone 90** | Highlighted notification banners, WIP info cards |
| `onTertiaryContainer` | **Tone 90** | **Tone 10** | Text on top of `tertiaryContainer` |
| `error` | **Tone 80** | **Tone 40** | Destructive actions, delete buttons, error alerts |
| `onError` | **Tone 20** | **Tone 100** | Text and icons on top of `error` |
| `errorContainer` | **Tone 30** | **Tone 90** | Error card fills, alert banners |
| `onErrorContainer` | **Tone 90** | **Tone 10** | Text on top of `errorContainer` |

### B. Surface Container Family Roles

```
  DARK THEME SURFACE HIERARCHY (Perceptual Lightness Increasing)
  ┌─────────────────────────────────────────────────────────────┐
  │ SurfaceDim                Tone 6   (Darkest background)     │
  │ Surface                   Tone 6   (Base app canvas)        │
  │ SurfaceContainerLow       Tone 10  (Flat cards on canvas)   │
  │ SurfaceContainer          Tone 12  (Standard cards & lists) │
  │ SurfaceContainerHigh      Tone 17  (Elevated player bar)    │
  │ SurfaceContainerHighest   Tone 22  (Inputs, active controls)│
  │ SurfaceBright             Tone 24  (Brightest surface)      │
  └─────────────────────────────────────────────────────────────┘
```

| Surface Role | Dark Tone | Light Tone | Component Mapping |
|:---|:---|:---|:---|
| `surfaceDim` | **Tone 6** | **Tone 87** | Dimmed background underneath sheets |
| `surface` | **Tone 6** | **Tone 98** | Default root app canvas |
| `surfaceBright` | **Tone 24** | **Tone 98** | High-emphasis bright background |
| `surfaceContainerLow` | **Tone 10** | **Tone 96** | Cards sitting directly on the canvas |
| `surfaceContainer` | **Tone 12** | **Tone 94** | Default cards, dialogs, and media lists |
| `surfaceContainerHigh` | **Tone 17** | **Tone 92** | Floating mini-player bar, bottom sheets |
| `surfaceContainerHighest` | **Tone 22** | **Tone 90** | Text field fills, active sliders, selected items |

### C. Utility & Inverse Roles
* `outline`: **Tone 60** (Dark) / **Tone 50** (Light) — Text field borders and prominent outlines.
* `outlineVariant`: **Tone 30** (Dark) / **Tone 80** (Light) — Subtle list dividers and card outlines.
* `scrim`: **Tone 0** (Black with alpha) — Modal background dimming overlay.
* `inverseSurface`: **Tone 90** (Dark) / **Tone 20** (Light) — High-contrast snackbars.
* `inverseOnSurface`: **Tone 10** (Dark) / **Tone 95** (Light) — Text inside inverse snackbars.
* `inversePrimary`: **Tone 40** (Dark) / **Tone 80** (Light) — Primary button inside an inverse surface.

### D. Fixed Color Roles (Media Controls)
Fixed tokens maintain constant color values regardless of Light or Dark theme:
* `primaryFixed`: **Tone 90** (Constant light accent for media play buttons).
* `primaryFixedDim`: **Tone 80** (Subtle fixed accent for secondary controls).
* `onPrimaryFixed`: **Tone 10** (Constant dark icon on top of `primaryFixed`).
* `onPrimaryFixedVariant`: **Tone 30** (Secondary text on top of `primaryFixed`).

---

## ⚡ 4. Algorithmic Contrast Guarantees

Because M3 assigns specific Tone gaps between containers and text, WCAG contrast ratios are **algorithmically guaranteed**:

* **Container to On-Container Contrast**:
  - Dark Theme: Container (`Tone 30`) + On-Container (`Tone 90`) $\to \Delta\text{Tone} = 60$ ($\ge 7:1$ contrast ratio).
  - Light Theme: Container (`Tone 90`) + On-Container (`Tone 10`) $\to \Delta\text{Tone} = 80$ ($\ge 10:1$ contrast ratio).
## 🎯 5. Choosing a Color Scheme: Static vs. Dynamic Variants

Material 3 supports two primary scheme generation modes: **Dynamic Color** (derived from user wallpaper or media artwork) and **Static Color** (fixed brand palette).

### A. The 6 Dynamic Scheme Variants

| Variant Name | Intent / Aesthetic | Recommended Media Usage |
|:---|:---|:---|
| `Content` | Directly mirrors the exact hue and chroma of media artwork | **Now Playing player screen & artwork canvas** |
| `Expressive` | Shifts Tertiary hue for maximum visual energy & contrast | **Audio DSP sheet & active karaoke lyrics** |
| `Vibrant` | Maximizes Chroma across primary and tertiary accents | High-emphasis playback controls & FABs |
| `Fidelity` | Retains exact seed color lightness & saturation without alteration | Custom theme previews |
| `TonalSpot` | Balanced default Material You theme (moderate chroma) | Default library screens (Tracks, Albums, Artists) |
| `Neutral` | Desaturates chroma for an ultra-clean, monochrome aesthetic | Minimalist / AMOLED dark mode |

### B. Dynamic Color Decision Flow
### C. Custom Brand Colors & Color Harmonization (Blend)

When integrating custom semantic colors (e.g., custom brand accents, FLAC/DSD quality badges, genre tags) alongside dynamic artwork palettes, custom colors must be **harmonized** to prevent visual clashing.

#### The Harmonization Algorithm
Color Harmonization shifts the HCT **Hue** of a custom design color slightly towards the primary seed color's Hue, while preserving the custom color's original Chroma and Tone.

```kotlin
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// Harmonize custom brand color with dynamic primary seed
fun harmonizeColor(
    customColor: Color,
    primarySeedColor: Color
): Color {
    val customArgb = customColor.toArgb()
    val primarySeedArgb = primarySeedColor.toArgb()
    
    // Shifts customColor hue towards primarySeedColor hue
    val harmonizedArgb = com.google.android.material.color.MaterialColors.harmonize(
        customArgb,
        primarySeedArgb
    )
    return Color(harmonizedArgb)
}
```

#### Custom Color Role Quadrant
Each custom brand color expands into 4 harmonized roles:
1. `customColor`: Base harmonized brand color.
2. `onCustomColor`: Text/icons placed on top of `customColor`.
3. `customColorContainer`: Tonal container background fill for badges/chips.
4. `onCustomColorContainer`: Text/icons placed on top of `customColorContainer`.

### D. Material 3 Baseline Color Scheme (Static Fallback)

When dynamic artwork colors or system wallpaper colors are unavailable (e.g. offline audio without embedded art or initial app launch), Material 3 uses the **Purple Baseline Seed** (`#6750A4`):

```kotlin
// Material 3 Baseline Dark Color Scheme (Static Fallback)
val BaselineDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
## 🌊 6. Dynamic Color Sources: User-Generated vs. Content-Based

Material 3 distinguishes between two dynamic color sources:

```
                    DYNAMIC COLOR SOURCES
                              │
         ┌────────────────────┴────────────────────┐
         ▼                                         ▼
USER-GENERATED SOURCE                      CONTENT-BASED SOURCE
(System Wallpaper / Android 12+)          (Album Artwork / Video Poster)
Used for: App Shell, Library Lists,       Used for: Now Playing Player,
Navigation Bars, Settings                 Artwork Background Canvas
```

---

### A. User-Generated Dynamic Color (System Wallpaper)
* **API Engine**: `dynamicDarkColorScheme(context)` / `dynamicLightColorScheme(context)` (API 31+).
* **Usage**: Governs global application scaffolding (Main Tab Navigation, Library List Views, Settings Screen).
* **Intent**: Maintains visual consistency with the user's Android system-wide Material You customization.

```kotlin
// App Level Theme Wrapping
@Composable
fun AuraAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        else -> BaselineDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ExpressiveShapes,
        content = content
    )
}
```

---

### B. Content-Based Dynamic Color (Album Artwork)
* **Extraction Engine**: AndroidX `Palette` / `Quantizer` on album artwork bitmap.
* **Content-Fidelity Rule**: Content-based schemes preserve high fidelity to the media artwork's primary hue and saturation so the player interface feels like a natural extension of the music.
* **Scoped Lifecycle**: Confined strictly to the **Now Playing Player Screen** and **Lyric Canvas**.

```kotlin
// Dynamic Player Theme Scoping
@Composable
fun ScopedPlayerTheme(
    artworkBitmap: Bitmap?,
    content: @Composable () -> Unit
) {
    val playerColors = remember(artworkBitmap) {
        artworkBitmap?.let { DynamicPaletteEngine.extract(it) }
            ?: DefaultPlayerColors
    }

    // Smoothly animate color scheme transitions when track changes
    val animatedAccent by animateColorAsState(
        targetValue = playerColors.accent,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "AccentAnim"
    )
    val animatedDominant by animateColorAsState(
        targetValue = playerColors.dominant,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "DominantAnim"
    )

    CompositionLocalProvider(
        LocalPlayerColors provides PlayerColors(
            accent = animatedAccent,
            dominant = animatedDominant,
            secondary = playerColors.secondary
        )
    ) {
        content()
    }
}
```




