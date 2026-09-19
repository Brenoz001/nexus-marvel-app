package com.example.nexus_marvel_app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.util.PowerCategory
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Hexagonal radar chart for the six power categories. Draws grid rings, axes,
 * the data polygon (animated outward on appear) and short axis labels.
 */
@Composable
fun PowerRadar(
    scores: Map<PowerCategory, Int>,
    modifier: Modifier = Modifier,
    color: Color = NexusColors.Gold,
    maxValue: Int = 10,
) {
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = NexusColors.TextSecondary, fontSize = 10.sp)
    val cats = PowerCategory.entries
    val progress = remember { Animatable(0f) }
    LaunchedEffect(scores) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(900))
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = min(cx, cy) * 0.62f
        val rings = 4

        fun angle(i: Int): Double = Math.toRadians(-90.0 + 60.0 * i)
        fun vertex(i: Int, r: Float): Offset {
            val a = angle(i)
            return Offset(cx + r * cos(a).toFloat(), cy + r * sin(a).toFloat())
        }

        // Grid rings
        for (ring in 1..rings) {
            val r = radius * ring / rings
            val path = Path()
            cats.forEachIndexed { i, _ ->
                val p = vertex(i, r)
                if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
            }
            path.close()
            drawPath(path, color = NexusColors.White.copy(alpha = 0.07f), style = Stroke(width = 1f))
        }

        // Axes
        cats.forEachIndexed { i, _ ->
            drawLine(
                color = NexusColors.White.copy(alpha = 0.10f),
                start = Offset(cx, cy),
                end = vertex(i, radius),
                strokeWidth = 1f,
            )
        }

        // Data polygon
        val dataPath = Path()
        cats.forEachIndexed { i, cat ->
            val v = (scores[cat] ?: 0).coerceIn(0, maxValue).toFloat() / maxValue
            val p = vertex(i, radius * v * progress.value)
            if (i == 0) dataPath.moveTo(p.x, p.y) else dataPath.lineTo(p.x, p.y)
        }
        dataPath.close()
        drawPath(dataPath, color = color.copy(alpha = 0.28f))
        drawPath(dataPath, color = color, style = Stroke(width = 2.dp.toPx()))

        // Vertices
        cats.forEachIndexed { i, cat ->
            val v = (scores[cat] ?: 0).coerceIn(0, maxValue).toFloat() / maxValue
            drawCircle(color = color, radius = 3.dp.toPx(), center = vertex(i, radius * v * progress.value))
        }

        // Labels
        cats.forEachIndexed { i, cat ->
            val p = vertex(i, radius + 20.dp.toPx())
            val layout = measurer.measure(cat.short, style = labelStyle)
            drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
        }
    }
}
