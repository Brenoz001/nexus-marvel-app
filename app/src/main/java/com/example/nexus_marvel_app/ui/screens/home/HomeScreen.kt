package com.example.nexus_marvel_app.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.nexus_marvel_app.data.MARVEL_PLANETS
import com.example.nexus_marvel_app.data.Planet
import com.example.nexus_marvel_app.ui.components.StarField
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.nexusBackground
import kotlin.math.sin

/** Layout of each planet in the depth field (index matches MARVEL_PLANETS). */
private data class PlanetPos(val xFrac: Float, val yFrac: Float, val size: Dp, val depth: Float, val phase: Float)

private val LAYOUT = listOf(
    PlanetPos(0.50f, 0.48f, 132.dp, 1.00f, 0.0f),  // Terra — closest
    PlanetPos(0.23f, 0.28f, 82.dp, 0.70f, 1.2f),   // Asgard
    PlanetPos(0.80f, 0.66f, 70.dp, 0.55f, 2.1f),   // Titã
    PlanetPos(0.82f, 0.30f, 76.dp, 0.62f, 3.0f),   // Cosmos
    PlanetPos(0.25f, 0.70f, 94.dp, 0.80f, 4.2f),   // Krakoa
    PlanetPos(0.58f, 0.84f, 58.dp, 0.45f, 5.1f),   // Klyntar
)

@Composable
fun HomeScreen(contentPadding: PaddingValues, onPlanetClick: (Int) -> Unit) {
    var pan by remember { mutableStateOf(Offset.Zero) }
    val transition = rememberInfiniteTransition(label = "float")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Restart),
        label = "t",
    )

    Box(modifier = Modifier.fillMaxSize().nexusBackground()) {
        StarField(modifier = Modifier.fillMaxSize())

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        pan = Offset(
                            (pan.x + drag.x).coerceIn(-130f, 130f),
                            (pan.y + drag.y).coerceIn(-130f, 130f),
                        )
                        change.consume()
                    }
                },
        ) {
            val w = maxWidth
            val h = maxHeight
            MARVEL_PLANETS.forEachIndexed { index, planet ->
                val pos = LAYOUT.getOrElse(index) { LAYOUT.last() }
                val parallax = 0.25f + pos.depth * 0.6f
                val bob = sin(t + pos.phase) * (6f + pos.depth * 8f)
                PlanetBody(
                    planet = planet,
                    size = pos.size,
                    alpha = (0.55f + pos.depth * 0.45f).coerceIn(0f, 1f),
                    modifier = Modifier
                        .offset(x = w * pos.xFrac - pos.size / 2, y = h * pos.yFrac - pos.size / 2)
                        .graphicsLayer {
                            translationX = pan.x * parallax
                            translationY = bob + pan.y * parallax
                        },
                    onClick = { onPlanetClick(index) },
                )
            }
        }

        // Masthead
        Column(modifier = Modifier.statusBarsPadding().padding(horizontal = Spacing.md, vertical = Spacing.sm)) {
            Text("NEXUS", fontFamily = BebasNeue, fontSize = 50.sp, letterSpacing = 8.sp, color = NexusColors.TextPrimary)
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(0.44f)
                    .height(2.dp)
                    .background(Brush.horizontalGradient(listOf(NexusColors.Gold, NexusColors.Gold.copy(alpha = 0f)))),
            )
            Text("Arraste para explorar. Toque num mundo.", color = NexusColors.GoldSoft, fontSize = 12.sp, letterSpacing = 0.6.sp, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun PlanetBody(
    planet: Planet,
    size: Dp,
    alpha: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.width(size).graphicsLayer { this.alpha = alpha },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PlanetSphere(
            planet = planet,
            diameter = size,
            modifier = Modifier.clickable(onClick = onClick),
        )
        Text(
            planet.name.uppercase(),
            fontFamily = BebasNeue,
            fontSize = 16.sp,
            letterSpacing = 1.sp,
            color = NexusColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/** A real planet photo shaded as a 3D sphere with an atmospheric glow. */
@Composable
fun PlanetSphere(
    planet: Planet,
    diameter: Dp,
    modifier: Modifier = Modifier,
) {
    val glow = planet.color
    Box(
        modifier = modifier
            .size(diameter)
            // Atmospheric glow bleeds beyond the disc without affecting layout.
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow.copy(alpha = 0.40f), glow.copy(alpha = 0.10f), Color.Transparent),
                        center = center,
                        radius = size.minDimension * 0.85f,
                    ),
                    radius = size.minDimension * 0.85f,
                )
            }
            .clip(CircleShape)
            .border(1.dp, glow.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(planet.imageRes),
            contentDescription = planet.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
        )
        // Spherical shading: light from top-left, shadow toward bottom-right.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.16f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.34f),
                                Color.Black.copy(alpha = 0.70f),
                            ),
                            center = Offset(size.width * 0.32f, size.height * 0.30f),
                            radius = size.minDimension * 0.92f,
                        ),
                    )
                },
        )
    }
}
