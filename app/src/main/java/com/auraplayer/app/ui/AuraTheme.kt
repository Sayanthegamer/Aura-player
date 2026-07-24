package com.auraplayer.app.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Google Pixel Material 3 Expressive Color Palette
val PixelDarkPrimary = Color(0xFFD0BCFF)
val PixelDarkOnPrimary = Color(0xFF381E72)
val PixelDarkPrimaryContainer = Color(0xFF4F378B)
val PixelDarkOnPrimaryContainer = Color(0xFFEADDFF)

val PixelDarkSecondary = Color(0xFFCCC2DC)
val PixelDarkOnSecondary = Color(0xFF332D41)
val PixelDarkSecondaryContainer = Color(0xFF4A4458)
val PixelDarkOnSecondaryContainer = Color(0xFFE8DEF8)

val PixelDarkTertiary = Color(0xFFEFB8C8)
val PixelDarkOnTertiary = Color(0xFF492532)
val PixelDarkTertiaryContainer = Color(0xFF633B48)
val PixelDarkOnTertiaryContainer = Color(0xFFFFD8E4)

val PixelDarkBackground = Color(0xFF141218)
val PixelDarkOnBackground = Color(0xFFE6E0E9)

val PixelDarkSurface = Color(0xFF141218)
val PixelDarkOnSurface = Color(0xFFE6E0E9)
val PixelDarkSurfaceVariant = Color(0xFF49454F)
val PixelDarkOnSurfaceVariant = Color(0xFFCAC4D0)

val PixelDarkSurfaceContainerLowest = Color(0xFF0F0D13)
val PixelDarkSurfaceContainerLow = Color(0xFF1D1B20)
val PixelDarkSurfaceContainer = Color(0xFF211F26)
val PixelDarkSurfaceContainerHigh = Color(0xFF2B2930)
val PixelDarkSurfaceContainerHighest = Color(0xFF36343B)

private val PixelDarkColorScheme = darkColorScheme(
    primary = PixelDarkPrimary,
    onPrimary = PixelDarkOnPrimary,
    primaryContainer = PixelDarkPrimaryContainer,
    onPrimaryContainer = PixelDarkOnPrimaryContainer,
    secondary = PixelDarkSecondary,
    onSecondary = PixelDarkOnSecondary,
    secondaryContainer = PixelDarkSecondaryContainer,
    onSecondaryContainer = PixelDarkOnSecondaryContainer,
    tertiary = PixelDarkTertiary,
    onTertiary = PixelDarkOnTertiary,
    tertiaryContainer = PixelDarkTertiaryContainer,
    onTertiaryContainer = PixelDarkOnTertiaryContainer,
    background = PixelDarkBackground,
    onBackground = PixelDarkOnBackground,
    surface = PixelDarkSurface,
    onSurface = PixelDarkOnSurface,
    surfaceVariant = PixelDarkSurfaceVariant,
    onSurfaceVariant = PixelDarkOnSurfaceVariant,
    outline = Color(0xFF938F96),
    outlineVariant = Color(0xFF49454F)
)

val PixelTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

@Composable
fun AuraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PixelDarkColorScheme
        else -> PixelDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PixelTypography,
        content = content
    )
}
