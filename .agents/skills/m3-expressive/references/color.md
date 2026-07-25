# Material 3 Color System Architecture & HCT Specification

Based on official [Material 3 Color System Overview](https://m3.material.io/styles/color/system/overview) and [How the Color System Works](https://m3.material.io/styles/color/system/how-the-system-works).

## 🎨 1. The HCT Color Model (Hue, Chroma, Tone)

Material 3 uses the **HCT (Hue, Chroma, Tone)** color space instead of RGB, HSL, or HSV. HCT combines the perceptual accuracy of CAM16 with the accessibility guarantees of L*a*b*.

```
   HUE (H)           CHROMA (C)            TONE (T)
   Color Angle       Color Purity /        Perceptual Lightness
   0° to 360°        Intensity (0 to 120+) 0 = Black, 100 = White
```

### Why HCT Matters
Traditional HSL lightness is uncalibrated — yellow at HSL lightness 50% appears blindingly bright while blue at 50% appears dark. **HCT Tone (T)** guarantees that Tone 50 has the exact same perceptual lightness regardless of Hue or Chroma, making contrast calculations 100% predictable.

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

## 🏛️ 3. Complete Color Token System Mapping

### A. Accent & Container Tokens

| Role | Dark Theme Tone | Light Theme Tone | Intent / Component Usage |
|:---|:---|:---|:---|
| `primary` | **Tone 80** | **Tone 40** | Key active buttons, prominent FABs, active tab indicators |
| `onPrimary` | **Tone 20** | **Tone 100** | Text and icons placed on top of `primary` |
| `primaryContainer` | **Tone 30** | **Tone 90** | Prominent container fills, selected chips |
| `onPrimaryContainer` | **Tone 90** | **Tone 10** | Text and icons placed on top of `primaryContainer` |
| `secondary` | **Tone 80** | **Tone 40** | Filter chips, secondary toggles, badge fills |
| `onSecondary` | **Tone 20** | **Tone 100** | Text and icons placed on top of `secondary` |
| `secondaryContainer` | **Tone 30** | **Tone 90** | Unselected elevated card fills, secondary buttons |
| `onSecondaryContainer` | **Tone 90** | **Tone 10** | Text on top of `secondaryContainer` |
| `tertiary` | **Tone 80** | **Tone 40** | Highlighting badges, playback progress, special tags |
| `onTertiary` | **Tone 20** | **Tone 100** | Text on top of `tertiary` |
| `tertiaryContainer` | **Tone 30** | **Tone 90** | Highlighted notification banners, WIP cards |
| `onTertiaryContainer` | **Tone 90** | **Tone 10** | Text on top of `tertiaryContainer` |

### B. Surface Container Family Tokens

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

### C. Outline & Border Tokens
* `outline`: **Tone 60** (Dark) / **Tone 50** (Light) — Primary input borders and cards.
* `outlineVariant`: **Tone 30** (Dark) / **Tone 80** (Light) — Subtle list dividers and card outlines.

### D. Fixed Color Tokens (Media Controls)
Fixed tokens maintain consistent color values regardless of whether the app is in Light or Dark theme:
* `primaryFixed`: **Tone 90** (Constant light accent for media play buttons).
* `onPrimaryFixed`: **Tone 10** (Constant dark icon on top of `primaryFixed`).
* `primaryFixedDim`: **Tone 80** (Subtle fixed accent for secondary controls).

---

## ⚡ 4. Algorithmic Contrast Guarantees

Because M3 assigns specific Tone gaps between containers and text, WCAG contrast ratios are **algorithmically guaranteed**:

* **Container to On-Container Contrast**:
  - Dark Theme: Container (`Tone 30`) + On-Container (`Tone 90`) $\to \Delta\text{Tone} = 60$ ($\ge 7:1$ contrast ratio).
  - Light Theme: Container (`Tone 90`) + On-Container (`Tone 10`) $\to \Delta\text{Tone} = 80$ ($\ge 10:1$ contrast ratio).
* **Body Text to Surface Contrast**:
  - Dark Theme: Surface (`Tone 6`) + OnSurface (`Tone 90`) $\to \Delta\text{Tone} = 84$ ($\ge 12:1$ contrast ratio).
