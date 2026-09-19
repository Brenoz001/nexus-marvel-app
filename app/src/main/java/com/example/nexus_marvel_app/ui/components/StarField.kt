package com.example.nexus_marvel_app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.nexus_marvel_app.ui.theme.NexusColors
import kotlin.math.sin
import kotlin.random.Random

private data class Star(val xFrac: Float, val yFrac: Float, val radius: Float, val phase: Float, val baseAlpha: Float)

/** Subtle animated starfield used behind the constellation graph. */
@Composable
fun StarField(modifier: Modifier = Modifier, count: Int = 90) {
    val stars = remember(count) {
        val rnd = Random(42)
        List(count) {
            Star(
                xFrac = rnd.nextFloat(),
                yFrac = rnd.nextFloat(),
                radius = 0.6f + rnd.nextFloat() * 1.6f,
                phase = rnd.nextFloat() * 6.283f,
                baseAlpha = 0.25f + rnd.nextFloat() * 0.5f,
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "stars")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "twinkle",
    )

    Canvas(modifier = modifier) {
        stars.forEach { star ->
            val twinkle = 0.5f + 0.5f * sin(t + star.phase)
            drawCircle(
                color = NexusColors.White,
                radius = star.radius,
                center = Offset(star.xFrac * size.width, star.yFrac * size.height),
                alpha = (star.baseAlpha * twinkle).coerceIn(0f, 1f),
            )
        }
    }
}
