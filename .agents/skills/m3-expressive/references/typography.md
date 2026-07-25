# Material 3 Typography System & Type Scale Specifications

Based on official [Material 3 Typography Overview](https://m3.material.io/styles/typography/overview), [Fonts](https://m3.material.io/styles/typography/fonts), and [Type Scale Tokens](https://m3.material.io/styles/typography/type-scale-tokens).

## ✏️ 1. Typography Principles & Font Families

1. **Hierarchy & Clarity**: Typography establishes visual structure, directing the user's focus through distinct size, weight, and line height contrast.
2. **Font Family Selection**:
   * **Primary Display & Text**: `Roboto` / `Roboto Flex` / `Google Sans` (for primary UI, track titles, album names).
   * **Monospace Quality & Tech Text**: `Roboto Mono` / `Google Sans Mono` (for track duration timestamps, audio sample rates, FLAC/DSD bitrates, EQ Hz values).
3. **Variable Type Axes**: Material 3 variable fonts use 4 key variable axes:
   - `wght` (Weight): $100 \to 900$ (Regular = 400, Medium = 500, SemiBold = 600, Bold = 700).
   - `wdth` (Width): $75\% \to 125\%$ (Condensed to Expanded).
   - `opsz` (Optical Size): Auto-calibrates stroke thickness for small captions vs. large display headers.
   - `GRAD` (Grade): Adjusts optical weight without altering layout width.

---

## 📊 2. Complete M3 15-Role Type Scale Token Table

| Type Role | Font Size (sp) | Line Height (sp) | Tracking / Letter Spacing | Weight | Primary Usage |
|:---|:---|:---|:---|:---|:---|
| `displayLarge` | **57 sp** | **64 sp** | `-0.25 sp` | Regular (400) | Hero stats, key visual numbers |
| `displayMedium` | **45 sp** | **52 sp** | `0.0 sp` | Regular (400) | Primary display headers |
| `displaySmall` | **36 sp** | **44 sp** | `0.0 sp` | Regular (400) | Large section display titles |
| `headlineLarge` | **32 sp** | **40 sp** | `0.0 sp` | Bold (700) | Full-screen player track title |
| `headlineMedium` | **28 sp** | **36 sp** | `0.0 sp` | SemiBold (600) | Major section headers |
| `headlineSmall` | **24 sp** | **32 sp** | `0.0 sp` | SemiBold (600) | Modal & dialog titles |
| `titleLarge` | **22 sp** | **28 sp** | `0.0 sp` | Medium (500) | Top app bar titles |
| `titleMedium` | **16 sp** | **24 sp** | `+0.15 sp` | Medium (500) | Card titles, list headers |
| `titleSmall` | **14 sp** | **20 sp** | `+0.1 sp` | Medium (500) | Subheaders, chip labels |
| `bodyLarge` | **16 sp** | **24 sp** | `+0.5 sp` | Regular (400) | Primary body text, synced lyrics lines |
| `bodyMedium` | **14 sp** | **20 sp** | `+0.25 sp` | Regular (400) | Secondary description text, supporting text |
| `bodySmall` | **12 sp** | **16 sp** | `+0.4 sp` | Regular (400) | Captions, timestamps, metadata |
| `labelLarge` | **14 sp** | **20 sp** | `+0.1 sp` | Medium (500) | Button labels, active tab text |
| `labelMedium` | **12 sp** | **16 sp** | `+0.5 sp` | Medium (500) | Badges, quality tags (FLAC/24-bit) |
| `labelSmall` | **11 sp** | **16 sp** | `+0.5 sp` | Medium (500) | Micro badges, secondary quality indicators |

---

## 🛠️ 3. Jetpack Compose Typography Token Implementation

```kotlin
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val M3Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace, // Monospace for audio technical tags
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

---

## 📐 4. Text Truncation & Layout Safety

In media applications, long track titles or artist names must never break row layouts or push action buttons off-screen:

```kotlin
Text(
    text = track.title,
    style = MaterialTheme.typography.titleMedium,
    fontWeight = FontWeight.Bold,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = Modifier.weight(1f) // Ensures text yields remaining space to trailing buttons
)
```
