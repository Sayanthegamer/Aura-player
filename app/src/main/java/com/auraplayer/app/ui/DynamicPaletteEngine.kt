package com.auraplayer.app.ui

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AlbumArtworkColors(
    val dominantBg: Color = Color(0xFF141218),
    val secondaryBg: Color = Color(0xFF281C30),
    val primaryAccent: Color = Color(0xFFD0BCFF),
    val secondaryAccent: Color = Color(0xFFCCC2DC),
    val surfaceContainer: Color = Color(0xFF2B2930)
)

@Composable
fun rememberArtworkColors(
    bitmap: Bitmap?,
    fallbackColorScheme: ColorScheme = MaterialTheme.colorScheme
): AlbumArtworkColors {
    var targetColors by remember(fallbackColorScheme) {
        mutableStateOf(
            AlbumArtworkColors(
                dominantBg = fallbackColorScheme.background,
                secondaryBg = fallbackColorScheme.surfaceContainerLow,
                primaryAccent = fallbackColorScheme.primary,
                secondaryAccent = fallbackColorScheme.secondary,
                surfaceContainer = fallbackColorScheme.surfaceContainerHigh
            )
        )
    }

    LaunchedEffect(bitmap, fallbackColorScheme) {
        if (bitmap != null) {
            withContext(Dispatchers.Default) {
                val palette = Palette.from(bitmap).generate()

                val dominantArgb = palette.getDominantColor(fallbackColorScheme.background.toArgb())
                val vibrantArgb = palette.getVibrantColor(palette.getLightVibrantColor(fallbackColorScheme.primary.toArgb()))
                val darkVibrantArgb = palette.getDarkVibrantColor(palette.getDarkMutedColor(fallbackColorScheme.surfaceContainerLow.toArgb()))
                val mutedArgb = palette.getMutedColor(fallbackColorScheme.secondary.toArgb())

                targetColors = AlbumArtworkColors(
                    dominantBg = Color(dominantArgb).darken(0.65f),
                    secondaryBg = Color(darkVibrantArgb).darken(0.50f),
                    primaryAccent = Color(vibrantArgb).brighten(0.2f),
                    secondaryAccent = Color(mutedArgb).brighten(0.3f),
                    surfaceContainer = Color(dominantArgb).darken(0.40f)
                )
            }
        } else {
            targetColors = AlbumArtworkColors(
                dominantBg = fallbackColorScheme.background,
                secondaryBg = fallbackColorScheme.surfaceContainerLow,
                primaryAccent = fallbackColorScheme.primary,
                secondaryAccent = fallbackColorScheme.secondary,
                surfaceContainer = fallbackColorScheme.surfaceContainerHigh
            )
        }
    }

    val animatedDominantBg by animateColorAsState(
        targetValue = targetColors.dominantBg,
        animationSpec = tween(800),
        label = "dominantBg"
    )
    val animatedSecondaryBg by animateColorAsState(
        targetValue = targetColors.secondaryBg,
        animationSpec = tween(800),
        label = "secondaryBg"
    )
    val animatedPrimaryAccent by animateColorAsState(
        targetValue = targetColors.primaryAccent,
        animationSpec = tween(800),
        label = "primaryAccent"
    )
    val animatedSecondaryAccent by animateColorAsState(
        targetValue = targetColors.secondaryAccent,
        animationSpec = tween(800),
        label = "secondaryAccent"
    )
    val animatedSurfaceContainer by animateColorAsState(
        targetValue = targetColors.surfaceContainer,
        animationSpec = tween(800),
        label = "surfaceContainer"
    )

    return AlbumArtworkColors(
        dominantBg = animatedDominantBg,
        secondaryBg = animatedSecondaryBg,
        primaryAccent = animatedPrimaryAccent,
        secondaryAccent = animatedSecondaryAccent,
        surfaceContainer = animatedSurfaceContainer
    )
}

private fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1f - factor)).coerceIn(0f, 1f),
        green = (green * (1f - factor)).coerceIn(0f, 1f),
        blue = (blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

private fun Color.brighten(factor: Float): Color {
    return Color(
        red = (red + (1f - red) * factor).coerceIn(0f, 1f),
        green = (green + (1f - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}
