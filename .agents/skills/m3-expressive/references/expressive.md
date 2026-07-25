# Material 3 Expressive Extensions for Audio & Media UIs

## 🎨 1. Dynamic Artwork Palette Engine

M3 Expressive UIs adapt dynamically to media content. The engine extracts key vibrant and muted tones from artwork and maps them to player surfaces.

### Palette Extraction Logic

```kotlin
object DynamicPaletteEngine {

    data class PlayerColors(
        val accent: Color,      // High emphasis controls & play buttons
        val dominant: Color,    // Background ambient glow
        val secondary: Color    // Subtitles, progress bar fills
    )

    fun extract(bitmap: Bitmap): PlayerColors {
        val palette = Palette.from(bitmap).generate()

        // 1. Accent: Prefer Vibrant -> LightVibrant -> DarkVibrant -> Fallback Accent
        val accentInt = palette.getVibrantColor(
            palette.getLightVibrantColor(
                palette.getDarkVibrantColor(0xFFD0BCFF.toInt())
            )
        )

        // 2. Dominant: Prefer DarkMuted -> Muted -> Dominant -> Fallback Dark
        val dominantInt = palette.getDarkMutedColor(
            palette.getMutedColor(
                palette.getDominantColor(0xFF141218.toInt())
            )
        )

        // 3. Secondary: Prefer LightMuted -> Muted
        val secondaryInt = palette.getLightMutedColor(
            palette.getMutedColor(accentInt)
        )

        return PlayerColors(
            accent = Color(accentInt),
            dominant = guardDarkness(Color(dominantInt)),
            secondary = Color(secondaryInt)
        )
    }

    // Ensure dominant background color remains dark enough for 4.5:1 text contrast
    private fun guardDarkness(color: Color): Color {
        val hsl = FloatArray(3)
        androidx.core.graphics.ColorUtils.colorToHSL(color.toArgb(), hsl)
        if (hsl[2] > 0.25f) {
            hsl[2] = 0.15f // Clamp lightness to max 15%
        }
        return Color(androidx.core.graphics.ColorUtils.HSLToColor(hsl))
    }
}
```

---

## 🌊 2. Continuous Brownian Motion Background

Instead of a static gradient, use two overlapping infinite spring loops to create organic fluid motion:

```kotlin
@Composable
Composable BrownianMotionBackground(
    dominantColor: Color,
    accentColor: Color,
    secondaryColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BrownianMotion")

    // Offset X loop (14s cycle)
    val offsetX by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OffsetX"
    )

    // Offset Y loop (18s cycle)
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OffsetY"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .blur(140.dp) // Deep Gaussian blur for fluid liquid effect
    ) {
        val width = size.width
        val height = size.height

        // Base dominant layer
        drawRect(color = dominantColor)

        // Floating accent orb 1
        drawCircle(
            color = accentColor.copy(alpha = 0.35f),
            radius = width * 0.75f,
            center = Offset(width * (0.5f + offsetX), height * (0.3f + offsetY))
        )

        // Floating secondary orb 2
        drawCircle(
            color = secondaryColor.copy(alpha = 0.25f),
            radius = width * 0.65f,
            center = Offset(width * (0.3f - offsetY), height * (0.7f - offsetX))
        )
    }
}
```

---

## 📝 3. Karaoke-Style Word-Level Lyrics Engine

M3 Expressive lyrics highlight active word tokens with dynamic scale, opacity, and soft accent glow:

```kotlin
@Composable
fun KaraokeWordToken(
    token: LyricToken,
    currentPositionMs: Long,
    accentColor: Color
) {
    val isActive = currentPositionMs in token.startMs..token.endMs
    val isPast = currentPositionMs > token.endMs

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1.0f else if (isPast) 0.85f else 0.4f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Text(
        text = "${token.text} ",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        color = if (isActive) accentColor else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
    )
}
```
