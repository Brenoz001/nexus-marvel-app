package com.example.nexus_marvel_app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * The NEXUS screen background: a quiet vertical wash from a lifted navy at the
 * top to a deeper ink at the bottom, so screens read as depth, not flat black.
 */
fun Modifier.nexusBackground(): Modifier = this.background(
    Brush.verticalGradient(
        0.0f to Color(0xFF0E1526),
        0.45f to NexusColors.Background,
        1.0f to NexusColors.BackgroundDeep,
    )
)
