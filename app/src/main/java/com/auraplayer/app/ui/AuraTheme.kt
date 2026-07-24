package com.auraplayer.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8906),
    secondary = Color(0xFFE53170),
    background = Color(0xFF0F0E17),
    surface = Color(0xFF1E1C2A),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AuraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
