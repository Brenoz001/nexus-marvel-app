package com.example.nexus_marvel_app.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.nexus_marvel_app.data.MARVEL_PLANETS
import com.example.nexus_marvel_app.data.Planet
import com.example.nexus_marvel_app.ui.components.StarField
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.nexusBackground
import kotlin.math.abs

/**
 * Home = the NEXUS cosmos: Marvel galaxies stacked in depth (a coverflow).
 * Drag sideways or tap a galaxy to bring it to the front; tap the front one to enter.
 */
@Composable
fun HomeScreen(contentPadding: PaddingValues, onPlanetClick: (Int) -> Unit) {
    val galaxies = MARVEL_PLANETS
    val n = galaxies.size
    var current by remember { mutableStateOf(0) }
    var drag by remember { mutableStateOf(0f) }

    val infinite = rememberInfiniteTransition(label = "home")
    val spin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(120000, easing = LinearEasing), RepeatMode.Restart),
        label = "spin",
    )

    Box(modifier = Modifier.fillMaxSize().nexusBackground()) {
        StarField(modifier = Modifier.fillMaxSize())

        // Galaxy stage
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(n) {
                    detectHorizontalDragGestures(
                        onDragEnd = { drag = 0f },
                        onDragCancel = { drag = 0f },
                    ) { change, delta ->
                        drag += delta
                        if (drag <= -55f && current < n - 1) { current++; drag = 0f }
                        else if (drag >= 55f && current > 0) { current--; drag = 0f }
                        change.consume()
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            galaxies.forEachIndexed { index, galaxy ->
                val rel = index - current
                if (abs(rel) <= 2) {
                    GalaxyCard(
                        galaxy = galaxy,
                        rel = rel,
                        spin = spin,
                        onClick = { if (rel == 0) onPlanetClick(index) else current = index },
                    )
                }
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
            Text("Arraste ou toque numa galáxia.", color = NexusColors.GoldSoft, fontSize = 12.sp, letterSpacing = 0.6.sp, modifier = Modifier.padding(top = 10.dp))
        }

        // Bottom HUD: arrows + galaxy name/realm + dots
        val g = galaxies[current]
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = contentPadding.calculateBottomPadding() + Spacing.xl)
                .padding(horizontal = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                ArrowButton(Icons.Filled.ChevronLeft, enabled = current > 0) { if (current > 0) current-- }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(g.name.uppercase(), fontFamily = BebasNeue, fontSize = 34.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, textAlign = TextAlign.Center)
                    Text(g.realm, color = g.color, fontSize = 12.sp, letterSpacing = 0.5.sp)
                }
                ArrowButton(Icons.Filled.ChevronRight, enabled = current < n - 1) { if (current < n - 1) current++ }
            }
            // Dots
            Row(modifier = Modifier.padding(top = Spacing.md), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                galaxies.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (i == current) 9.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (i == current) g.color else NexusColors.TextMuted),
                    )
                }
            }
            Text("Toque na galáxia para entrar", color = NexusColors.TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = Spacing.sm))
        }
    }
}

@Composable
private fun GalaxyCard(galaxy: Planet, rel: Int, spin: Float, onClick: () -> Unit) {
    val a = abs(rel)
    val scale by animateFloatAsState((1f - 0.17f * a).coerceAtLeast(0.5f), tween(360), label = "scale")
    val alpha by animateFloatAsState((1f - 0.32f * a).coerceIn(0f, 1f), tween(360), label = "alpha")
    val xdp by animateFloatAsState(rel * 98f, tween(360), label = "x")

    Box(
        modifier = Modifier
            .offset(x = xdp.dp)
            .zIndex(10f - a)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                rotationZ = spin
            }
            .clickable(onClick = onClick),
    ) {
        PlanetSphere(planet = galaxy, diameter = 176.dp)
    }
}

@Composable
private fun ArrowButton(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(NexusColors.Surface)
            .border(1.dp, if (enabled) NexusColors.GoldBorder else NexusColors.Border, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) NexusColors.Gold else NexusColors.TextMuted)
    }
}

/** A real galaxy/nebula photo shaded as a luminous disc with an atmospheric glow. */
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
            // Soft atmospheric rim glow that hugs the disc and fades out cleanly.
            .drawBehind {
                val r = size.minDimension
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.50f to Color.Transparent,
                            0.60f to glow.copy(alpha = 0.30f),
                            0.85f to Color.Transparent,
                        ),
                        center = center,
                        radius = r,
                    ),
                    radius = r,
                )
            }
            .clip(CircleShape)
            .border(1.dp, glow.copy(alpha = 0.45f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(planet.imageRes),
            contentDescription = planet.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
        )
        // Gentle vignette so the disc reads round without looking like a lit planet.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0.0f to Color.White.copy(alpha = 0.10f),
                                0.55f to Color.Transparent,
                                0.85f to Color.Black.copy(alpha = 0.18f),
                                1.0f to Color.Black.copy(alpha = 0.42f),
                            ),
                            center = Offset(size.width * 0.36f, size.height * 0.34f),
                            radius = size.minDimension * 0.98f,
                        ),
                    )
                },
        )
    }
}
