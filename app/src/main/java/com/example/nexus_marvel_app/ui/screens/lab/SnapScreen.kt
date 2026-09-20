package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.random.Random
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SnapScreen(onBack: () -> Unit) {
    val vm: ThanosViewModel = viewModel(factory = ThanosViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val flash = remember { Animatable(0f) }

    LabScaffold(title = "Efeito Thanos", onBack = onBack) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NexusColors.Gold)
                }
                state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
                else -> Column(modifier = Modifier.fillMaxSize()) {
                    FlowRow(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        maxItemsInEachRow = 5,
                    ) {
                        state.pool.forEach { character ->
                            SnapAvatar(
                                character = character,
                                snapped = state.snapped,
                                victim = character.id in state.snappedIds,
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
                            ActionButton("REVERTER", NexusColors.Success) { vm.revert() }
                        } else {
                            ActionButton("SNAP", NexusColors.Gold) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    flash.snapTo(0.7f)
                                    flash.animateTo(0f, tween(650))
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
private fun SnapAvatar(character: Character, snapped: Boolean, victim: Boolean) {
    val density = LocalDensity.current
    val driftX = remember(character.id) { Random(character.id).nextInt(-30, 31).toFloat() }
    val driftY = remember(character.id) { Random(character.id * 7 + 1).nextInt(-70, -10).toFloat() }
    val rot = remember(character.id) { Random(character.id * 13 + 3).nextInt(-45, 46).toFloat() }

    // presence: 1 = fully here, 0 = dusted
    val presence by animateFloatAsState(
        targetValue = if (victim) 0f else 1f,
        animationSpec = tween(if (victim) 900 else 500),
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
private fun ActionButton(label: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xxl, vertical = Spacing.md),
    ) {
        Text(label, color = NexusColors.Black, fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
    }
}
