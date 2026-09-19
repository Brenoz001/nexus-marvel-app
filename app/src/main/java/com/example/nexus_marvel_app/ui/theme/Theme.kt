package com.example.nexus_marvel_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/** Always-dark scheme derived from the NEXUS palette. */
private val NexusColorScheme = darkColorScheme(
    primary = NexusColors.Red,
    onPrimary = NexusColors.White,
    secondary = NexusColors.Gold,
    onSecondary = NexusColors.Black,
    tertiary = NexusColors.Info,
    background = NexusColors.Background,
    onBackground = NexusColors.TextPrimary,
    surface = NexusColors.Surface,
    onSurface = NexusColors.TextPrimary,
    surfaceVariant = NexusColors.SurfaceLight,
    onSurfaceVariant = NexusColors.TextSecondary,
    error = NexusColors.Danger,
    outline = NexusColors.BorderStrong,
)

@Composable
fun NexusTheme(
    // NEXUS is always dark; the parameter exists for completeness.
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NexusColorScheme,
        typography = NexusTypography,
        content = content,
    )
}
