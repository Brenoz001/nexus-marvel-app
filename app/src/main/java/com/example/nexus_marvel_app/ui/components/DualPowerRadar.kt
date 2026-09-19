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
import androidx.compose.ui.graphics.drawscope.DrawScope
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

/** Radar chart overlaying two datasets (used in Confronto). */
@Composable
fun DualPowerRadar(
    scoresA: Map<PowerCategory, Int>,
    colorA: Color,
    scoresB: Map<PowerCategory, Int>,
    colorB: Color,
    modifier: Modifier = Modifier,
    maxValue: Int = 10,
) {
    val measurer = rememberTextMeasurer()
    val labelStyle = TextStyle(color = NexusColors.TextSecondary, fontSize = 10.sp)
    val cats = PowerCategory.entries
    val progress = remember { Animatable(0f) }
    LaunchedEffect(scoresA, scoresB) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(900))
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = min(cx, cy) * 0.62f

        fun angle(i: Int) = Math.toRadians(-90.0 + 60.0 * i)
        fun vertex(i: Int, r: Float) = Offset(cx + r * cos(angle(i)).toFloat(), cy + r * sin(angle(i)).toFloat())

        // Grid rings
        for (ring in 1..4) {
            val r = radius * ring / 4
            val path = Path()
            cats.forEachIndexed { i, _ ->
                val p = vertex(i, r)
                if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
            }
            path.close()
            drawPath(path, color = NexusColors.White.copy(alpha = 0.07f), style = Stroke(width = 1f))
        }
        cats.forEachIndexed { i, _ ->
            drawLine(NexusColors.White.copy(alpha = 0.10f), Offset(cx, cy), vertex(i, radius), strokeWidth = 1f)
        }

        drawDataset(cats, scoresA, colorA, radius, progress.value, maxValue, ::vertex)
        drawDataset(cats, scoresB, colorB, radius, progress.value, maxValue, ::vertex)

        cats.forEachIndexed { i, cat ->
            val p = vertex(i, radius + 20.dp.toPx())
            val layout = measurer.measure(cat.short, style = labelStyle)
            drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
        }
    }
}

private fun DrawScope.drawDataset(
    cats: List<PowerCategory>,
    scores: Map<PowerCategory, Int>,
    color: Color,
    radius: Float,
    progress: Float,
    maxValue: Int,
    vertex: (Int, Float) -> Offset,
) {
    val path = Path()
    cats.forEachIndexed { i, cat ->
        val v = (scores[cat] ?: 0).coerceIn(0, maxValue).toFloat() / maxValue
        val p = vertex(i, radius * v * progress)
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    path.close()
    drawPath(path, color = color.copy(alpha = 0.25f))
    drawPath(path, color = color, style = Stroke(width = 2.dp.toPx()))
}
