package com.example.nexus_marvel_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.nexus_marvel_app.ui.theme.NexusColors

/** Full-bleed poster image with a dark placeholder background. */
@Composable
fun AsyncPoster(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    val context = LocalContext.current
    Box(modifier = modifier.background(NexusColors.SurfaceLight)) {
        if (url != null) {
            AsyncImage(
                model = ImageRequest.Builder(context).data(url).crossfade(true).build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Vertical dark gradient used over imagery for the cinematic look. */
@Composable
fun GradientScrim(
    modifier: Modifier = Modifier,
    color: Color = NexusColors.Background,
    fromBottom: Boolean = true,
    intensity: Float = 0.95f,
) {
    val transparent = color.copy(alpha = 0f)
    val mid = color.copy(alpha = intensity * 0.5f)
    val solid = color.copy(alpha = intensity)
    val colors = if (fromBottom) {
        listOf(transparent, mid, solid)
    } else {
        listOf(solid, mid, transparent)
    }
    Box(modifier = modifier.background(Brush.verticalGradient(colors)))
}
