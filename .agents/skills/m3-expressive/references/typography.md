# Material 3 Typography System & Type Scale Specifications

Based on official [Material 3 Typography Overview](https://m3.material.io/styles/typography/overview).

## ✏️ 1. Typography Principles

1. **Hierarchy & Clarity**: Typography establishes visual structure, directing the user's focus through distinct size, weight, and line height contrast.
2. **Variable Type Axes**: Material 3 typography leverages variable font axes (`wght`, `wdth`, `opsz`) for crisp optical rendering across small watch screens up to large foldable tablets.
3. **The 5 Scale Families**: Divided into 5 key categories (**Display**, **Headline**, **Title**, **Body**, **Label**), with 3 size variants each (**Large**, **Medium**, **Small**) for a total of 15 standard type roles.

---

## 📊 2. Complete M3 15-Role Typography Token Scale

| Type Role | Font Size (sp) | Line Height (sp) | Weight | Primary Usage |
|:---|:---|:---|:---|:---|
| `displayLarge` | **57 sp** | **64 sp** | Regular | Hero stats, key visual numbers |
| `displayMedium` | **45 sp** | **52 sp** | Regular | Primary display headers |
| `displaySmall` | **36 sp** | **44 sp** | Regular | Large section display titles |
| `headlineLarge` | **32 sp** | **40 sp** | Bold | Full-screen player track title |
| `headlineMedium` | **28 sp** | **36 sp** | SemiBold | Major section headers |
| `headlineSmall` | **24 sp** | **32 sp** | SemiBold | Modal & dialog titles |
| `titleLarge` | **22 sp** | **28 sp** | Medium | Top app bar titles |
| `titleMedium` | **16 sp** | **24 sp** | Medium | Card titles, list headers |
| `titleSmall` | **14 sp** | **20 sp** | Medium | Subheaders, chip labels |
| `bodyLarge` | **16 sp** | **24 sp** | Regular | Primary body text, synced lyrics lines |
| `bodyMedium` | **14 sp** | **20 sp** | Regular | Secondary description text, supporting text |
| `bodySmall` | **12 sp** | **16 sp** | Regular | Captions, timestamps, metadata |
| `labelLarge` | **14 sp** | **20 sp** | Medium | Button labels, active tab text |
| `labelMedium` | **12 sp** | **16 sp** | Medium | Badges, quality tags (FLAC/24-bit) |
| `labelSmall` | **11 sp** | **16 sp** | Medium | Micro badges, secondary quality indicators |

---

## 🛠️ 3. Jetpack Compose Typography Implementation

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
        lineHeight = 64.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
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
