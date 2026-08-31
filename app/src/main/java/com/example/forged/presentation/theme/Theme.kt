package com.example.forged.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ForgedColorScheme = darkColorScheme(
    primary = ForgedOrange,
    onPrimary = Color.White,
    background = ForgedBackground,
    onBackground = ForgedOnBackground,
    surface = ForgedSurface,
    onSurface = ForgedOnBackground,
    surfaceVariant = ForgedSurfaceRaised,
    onSurfaceVariant = ForgedMuted,
    outline = ForgedBorder,
    error = ForgedDelete,
    onError = Color.White,
)

@Composable
fun ForgedTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ForgedColorScheme,
        typography = Typography,
        content = content,
    )
}
