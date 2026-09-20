package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.launch

/** The six Infinity Stones — name, color and a base position in the play field. */
private data class Stone(val name: String, val color: Color, val fx: Float, val fy: Float, val phase: Float)

private val STONES = listOf(
    Stone("Espaço", Color(0xFF3B82F6), 0.20f, 0.16f, 0.0f),
    Stone("Mente", Color(0xFFF5C518), 0.76f, 0.14f, 1.1f),
    Stone("Realidade", Color(0xFFE23636), 0.50f, 0.34f, 2.2f),
    Stone("Poder", Color(0xFF9B59B6), 0.16f, 0.54f, 3.3f),
    Stone("Tempo", Color(0xFF46A46B), 0.82f, 0.50f, 4.1f),
    Stone("Alma", Color(0xFFE67E22), 0.46f, 0.68f, 5.0f),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SnapScreen(onBack: () -> Unit) {
    val vm: ThanosViewModel = viewModel(factory = ThanosViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val flash = remember { Animatable(0f) }

    // Mini-game state (local): which stones are collected + timing.
    var collected by remember { mutableStateOf(setOf<Int>()) }
    var startMs by remember { mutableStateOf(System.currentTimeMillis()) }
    var timeMs by remember { mutableStateOf(0L) }
    val complete = collected.size == STONES.size

    fun resetGame() {
        collected = emptySet()
        startMs = System.currentTimeMillis()
        timeMs = 0L
    }

    LabScaffold(title = "Manopla do Infinito", onBack = onBack) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NexusColors.Gold)
                }
                state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    // Play area: heroes as backdrop + floating stones overlay.
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(Spacing.md),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                            maxItemsInEachRow = 5,
                        ) {
                            state.pool.forEachIndexed { index, character ->
                                SnapAvatar(
                                    character = character,
                                    index = index,
                                    snapped = state.snapped,
                                    victim = character.id in state.snappedIds,
                                )
                            }
                        }

                        // Floating stones (only while gathering, before the snap)
                        if (!state.snapped) {
                            StoneField(
                                collected = collected,
                                onCollect = { idx ->
                                    if (idx !in collected) {
                                        val next = collected + idx
                                        collected = next
                                        if (next.size == STONES.size) timeMs = System.currentTimeMillis() - startMs
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                },
                            )
                        }
                    }

                    // Controls
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        if (state.snapped) {
                            val survivors = state.pool.count { it.id !in state.snappedIds }
                            val thanos = state.pool.firstOrNull { it.name.contains("Thanos", ignoreCase = true) }
                            thanos?.let {
                                LabAvatar(url = it.imageMedium, size = 76.dp, ringColor = NexusColors.Gold, ringWidth = 3.dp, contentDescription = "Thanos")
                            }
                            Text(
                                "\"Perfeitamente equilibrado, como tudo deveria ser.\"",
                                color = NexusColors.TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                "$survivors de ${state.pool.size} sobreviveram ao estalo",
                                color = NexusColors.Success,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            ActionButton("REVERTER", NexusColors.Success, enabled = true) {
                                vm.revert()
                                resetGame()
                            }
                        } else {
                            // Gauntlet gem tracker
                            GauntletTracker(collected = collected)
                            Text(
                                if (complete) {
                                    "Manopla completa em ${"%.1f".format(timeMs / 1000f)}s. Estale."
                                } else {
                                    "Toque nas 6 Joias do Infinito  (${collected.size}/6)"
                                },
                                color = if (complete) NexusColors.Gold else NexusColors.TextSecondary,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                            )
                            ActionButton(
                                label = if (complete) "ESTALAR" else "REÚNA AS JOIAS",
                                color = if (complete) NexusColors.Gold else NexusColors.SurfaceLight,
                                enabled = complete,
                            ) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    flash.snapTo(0.85f)
                                    flash.animateTo(0f, tween(750))
                                }
                                vm.snap()
                            }
                        }
                    }
                }
            }

            // gold flash overlay
            Box(modifier = Modifier.fillMaxSize().background(NexusColors.Gold.copy(alpha = flash.value.coerceIn(0f, 1f))))
        }
    }
}

@Composable
private fun StoneField(collected: Set<Int>, onCollect: (Int) -> Unit) {
    val transition = rememberInfiniteTransition(label = "stones")
    val t by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "bob",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1300), RepeatMode.Reverse),
        label = "pulse",
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        val box = 60.dp
        STONES.forEachIndexed { i, stone ->
            if (i in collected) return@forEachIndexed
            val bob = (sin(t + stone.phase) * 8f).dp
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(x = w * stone.fx - box / 2, y = h * stone.fy - box / 2 + bob)
                    .size(box)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
                    .clickable { onCollect(i) },
            ) {
                Gem(color = stone.color, size = 40.dp)
            }
        }
    }
}

/** A rotated gem with a bright core and colored glow. */
@Composable
private fun Gem(color: Color, size: androidx.compose.ui.unit.Dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size * 1.5f)
            .drawBehind {
                val r = this.size.minDimension * 0.5f
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(0.0f to color.copy(alpha = 0.55f), 1.0f to Color.Transparent),
                        center = center,
                        radius = r,
                    ),
                    radius = r,
                )
            },
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer { rotationZ = 45f }
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.radialGradient(listOf(Color.White, color, color.copy(alpha = 0.85f))))
                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp)),
        )
    }
}

/** Six gem slots that fill as stones are collected. */
@Composable
private fun GauntletTracker(collected: Set<Int>) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
        STONES.forEachIndexed { i, stone ->
            val on = i in collected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(30.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = 45f }
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (on) stone.color else NexusColors.SurfaceLight)
                        .border(1.dp, if (on) Color.White.copy(alpha = 0.7f) else NexusColors.Border, RoundedCornerShape(4.dp)),
                )
            }
        }
    }
}

@Composable
private fun SnapAvatar(character: Character, index: Int, snapped: Boolean, victim: Boolean) {
    val density = LocalDensity.current
    val driftX = remember(character.id) { Random(character.id).nextInt(-30, 31).toFloat() }
    val driftY = remember(character.id) { Random(character.id * 7 + 1).nextInt(-70, -10).toFloat() }
    val rot = remember(character.id) { Random(character.id * 13 + 3).nextInt(-45, 46).toFloat() }

    // presence: 1 = fully here, 0 = dusted. Staggered by index for a wave effect.
    val presence by animateFloatAsState(
        targetValue = if (victim) 0f else 1f,
        animationSpec = tween(
            durationMillis = if (victim) 900 else 500,
            delayMillis = if (victim) index * 55 else 0,
        ),
        label = "presence",
    )

    val driftXpx = with(density) { driftX.dp.toPx() }
    val driftYpx = with(density) { driftY.dp.toPx() }
    val ring = if (snapped && !victim) NexusColors.Success else NexusColors.BorderStrong

    LabAvatar(
        url = character.imageMedium,
        size = 58.dp,
        ringColor = ring,
        ringWidth = if (snapped && !victim) 2.5.dp else 1.5.dp,
        contentDescription = character.name,
        modifier = Modifier.graphicsLayer {
            alpha = presence.coerceIn(0f, 1f)
            val s = 0.5f + 0.5f * presence
            scaleX = s
            scaleY = s
            translationX = (1f - presence) * driftXpx
            translationY = (1f - presence) * driftYpx
            rotationZ = (1f - presence) * rot
        },
    )
}

@Composable
private fun ActionButton(label: String, color: Color, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(color)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Spacing.xxl, vertical = Spacing.md),
    ) {
        Text(
            label,
            color = if (enabled) NexusColors.Black else NexusColors.TextMuted,
            fontFamily = BebasNeue,
            fontSize = 24.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
