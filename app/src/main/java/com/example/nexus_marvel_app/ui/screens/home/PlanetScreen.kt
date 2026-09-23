package com.example.nexus_marvel_app.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.data.MARVEL_PLANETS
import com.example.nexus_marvel_app.data.Planet
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.components.StarField
import com.example.nexus_marvel_app.ui.screens.lab.CosmosViewModel
import com.example.nexus_marvel_app.ui.screens.lab.LabAvatar
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.nexusBackground
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PlanetScreen(planetIndex: Int, onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    val planet = MARVEL_PLANETS.getOrNull(planetIndex) ?: MARVEL_PLANETS.first()
    val vm: CosmosViewModel = viewModel(factory = CosmosViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    androidx.compose.runtime.LaunchedEffect(planetIndex) { vm.loadFor(planet.characters) }

    Box(modifier = Modifier.fillMaxSize().nexusBackground()) {
        StarField(modifier = Modifier.fillMaxSize())

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = planet.color)
            }
        } else {
            OrbitField(planet = planet, heroes = vm.charactersFor(planet.characters), onCharacterClick = onCharacterClick)
        }

        // Top: back + planet title
        Row(
            modifier = Modifier.statusBarsPadding().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            NexusBackButton(onClick = onBack)
            Column {
                Text(planet.name.uppercase(), fontFamily = BebasNeue, fontSize = 30.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary)
                Text(planet.realm, color = planet.color, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun OrbitField(planet: Planet, heroes: List<Character>, onCharacterClick: (Int) -> Unit) {
    val transition = rememberInfiniteTransition(label = "orbit")
    val orbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(38000, easing = LinearEasing), RepeatMode.Restart),
        label = "orbitAngle",
    )
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2400), RepeatMode.Reverse),
        label = "pulse",
    )

    // Spread heroes across concentric rings so a crowded galaxy stays legible.
    val ring0 = heroes.take(6)
    val ring1 = heroes.drop(6)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Glow halo
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(planet.color.copy(alpha = 0.18f), Color.Transparent))),
        )
        // Galaxy core — real nebula image
        PlanetSphere(
            planet = planet,
            diameter = 120.dp,
            modifier = Modifier.scale(pulse),
        )

        OrbitRing(heroes = ring0, radiusDp = 92.dp, orbit = orbit, direction = 1f, avatar = 50.dp, planet = planet, startIndex = 0, onCharacterClick = onCharacterClick)
        OrbitRing(heroes = ring1, radiusDp = 150.dp, orbit = orbit, direction = -1f, avatar = 44.dp, planet = planet, startIndex = ring0.size, onCharacterClick = onCharacterClick)
    }
}

@Composable
private fun OrbitRing(
    heroes: List<Character>,
    radiusDp: androidx.compose.ui.unit.Dp,
    orbit: Float,
    direction: Float,
    avatar: androidx.compose.ui.unit.Dp,
    planet: Planet,
    startIndex: Int,
    onCharacterClick: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val rPx = with(density) { radiusDp.toPx() }
    val n = heroes.size.coerceAtLeast(1)
    heroes.forEachIndexed { i, hero ->
        val entrance = remember(hero.id) { Animatable(0f) }
        androidx.compose.runtime.LaunchedEffect(hero.id) {
            kotlinx.coroutines.delay((startIndex + i) * 60L)
            entrance.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
        val angle = (2.0 * Math.PI * i / n).toFloat() + orbit * direction
        val dx = rPx * cos(angle)
        val dy = rPx * sin(angle)
        Box(
            modifier = Modifier
                .offset { IntOffset(dx.roundToInt(), dy.roundToInt()) }
                .graphicsLayer {
                    scaleX = entrance.value
                    scaleY = entrance.value
                    alpha = entrance.value
                }
                .clip(CircleShape)
                .clickable { onCharacterClick(hero.id) },
        ) {
            LabAvatar(url = hero.imageMedium, size = avatar, ringColor = planet.color, contentDescription = hero.name)
        }
    }
}
