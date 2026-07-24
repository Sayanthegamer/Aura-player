package com.auraplayer.app.ui

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
    val primaryAccent: Color = Color(0xFFFF8906),
    val secondaryAccent: Color = Color(0xFFE53170),
    val surfaceContainer: Color = Color(0xFF252030)
)

@Composable
fun rememberArtworkColors(bitmap: Bitmap?): AlbumArtworkColors {
    var targetColors by remember { mutableStateOf(AlbumArtworkColors()) }

    LaunchedEffect(bitmap) {
        if (bitmap != null) {
            withContext(Dispatchers.Default) {
                val palette = Palette.from(bitmap).generate()

                val dominantArgb = palette.getDominantColor(0xFF141218.toInt())
                val vibrantArgb = palette.getVibrantColor(palette.getLightVibrantColor(0xFFFF8906.toInt()))
                val darkVibrantArgb = palette.getDarkVibrantColor(palette.getDarkMutedColor(0xFF281C30.toInt()))
                val mutedArgb = palette.getMutedColor(0xFF252030.toInt())

                targetColors = AlbumArtworkColors(
                    dominantBg = Color(dominantArgb).darken(0.65f),
                    secondaryBg = Color(darkVibrantArgb).darken(0.50f),
                    primaryAccent = Color(vibrantArgb).brighten(0.2f),
                    secondaryAccent = Color(mutedArgb).brighten(0.3f),
                    surfaceContainer = Color(dominantArgb).darken(0.40f)
                )
            }
        }
    }

    val animatedDominantBg by animateColorAsState(
        targetValue = targetColors.dominantBg,
        animationSpec = tween(1000),
        label = "dominantBg"
    )
    val animatedSecondaryBg by animateColorAsState(
        targetValue = targetColors.secondaryBg,
        animationSpec = tween(1000),
        label = "secondaryBg"
    )
    val animatedPrimaryAccent by animateColorAsState(
        targetValue = targetColors.primaryAccent,
        animationSpec = tween(1000),
        label = "primaryAccent"
    )
    val animatedSecondaryAccent by animateColorAsState(
        targetValue = targetColors.secondaryAccent,
        animationSpec = tween(1000),
        label = "secondaryAccent"
    )
    val animatedSurfaceContainer by animateColorAsState(
        targetValue = targetColors.surfaceContainer,
        animationSpec = tween(1000),
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
