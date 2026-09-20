package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.ui.components.DualPowerRadar
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.util.PowerCategory
import com.example.nexus_marvel_app.util.computePowerScores
import com.example.nexus_marvel_app.util.overallRating

private val COLOR_A = NexusColors.Red
private val COLOR_B = NexusColors.Info

@Composable
fun ConfrontoScreen(onBack: () -> Unit) {
    val vm: ConfrontoViewModel = viewModel(factory = ConfrontoViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    LabScaffold(title = "Confronto", onBack = onBack) {
        when {
            state.loading -> Box(Modifier.fillMaxWidth().padding(Spacing.xxl), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NexusColors.Red)
            }
            state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
            else -> Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = Spacing.md)) {
                // VS slots
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Slot(state.fighterA, COLOR_A, active = state.activeSlot == 0, onClick = { vm.setActiveSlot(0) }, modifier = Modifier.weight(1f))
                    Text("VS", color = NexusColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.padding(horizontal = Spacing.sm))
                    Slot(state.fighterB, COLOR_B, active = state.activeSlot == 1, onClick = { vm.setActiveSlot(1) }, modifier = Modifier.weight(1f))
                }

                if (state.fighterA != null && state.fighterB != null) {
                    DualPowerRadar(
                        scoresA = computePowerScores(state.fighterA!!.powers),
                        colorA = COLOR_A,
                        scoresB = computePowerScores(state.fighterB!!.powers),
                        colorB = COLOR_B,
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                    )
                    Verdict(state.fighterA!!, state.fighterB!!)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = Spacing.md), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        PowerColumn(state.fighterA!!, COLOR_A, Modifier.weight(1f))
                        PowerColumn(state.fighterB!!, COLOR_B, Modifier.weight(1f))
                    }
                } else {
                    Text(
                        "Toque num lado e escolha um personagem abaixo para montar o confronto.",
                        color = NexusColors.TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xl),
                    )
                }

                Text("ESCOLHA OS LUTADORES", color = NexusColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.sm))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    items(state.pool, key = { it.id }) { c ->
                        PickerAvatar(c, onClick = { vm.pick(c) })
                    }
                }
                Box(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}

@Composable
private fun Slot(fighter: Character?, color: androidx.compose.ui.graphics.Color, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(color.copy(alpha = if (active) 0.16f else 0.06f))
            .border(if (active) 2.dp else 1.dp, color.copy(alpha = if (active) 0.7f else 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (fighter != null) {
            LabAvatar(url = fighter.imageMedium, size = 72.dp, ringColor = color, contentDescription = fighter.name)
            Text(fighter.name, color = NexusColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        } else {
            Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(NexusColors.SurfaceLight), contentAlignment = Alignment.Center) {
                Text("?", color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Text(if (active) "Escolhendo…" else "Escolher", color = NexusColors.TextSecondary, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PowerColumn(fighter: Character, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(fighter.name, color = NexusColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (fighter.powers.isEmpty()) {
            Text("Sem poderes catalogados.", color = NexusColors.TextMuted, fontSize = 11.sp)
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                fighter.powers.take(8).forEach { p -> NexusBadge(label = p.name, color = color, small = true) }
            }
        }
    }
}

@Composable
private fun Verdict(a: Character, b: Character) {
    val sa = computePowerScores(a.powers)
    val sb = computePowerScores(b.powers)
    val ra = overallRating(sa)
    val rb = overallRating(sb)
    val winnerName = when { ra > rb -> a.name; rb > ra -> b.name; else -> "Empate técnico" }
    val winnerColor = when { ra > rb -> COLOR_A; rb > ra -> COLOR_B; else -> NexusColors.Gold }
    val catsA = PowerCategory.entries.count { (sa[it] ?: 0) > (sb[it] ?: 0) }
    val catsB = PowerCategory.entries.count { (sb[it] ?: 0) > (sa[it] ?: 0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.md)
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.Surface)
            .border(1.dp, winnerColor.copy(alpha = 0.4f), RoundedCornerShape(Radius.lg))
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text("VEREDITO", color = NexusColors.TextMuted, fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.lg)) {
            RatingBlock(ra, a.name, COLOR_A)
            Text("VS", color = NexusColors.TextSecondary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            RatingBlock(rb, b.name, COLOR_B)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = winnerColor, modifier = Modifier.size(20.dp))
            Text("Vencedor: $winnerName", color = winnerColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Text("$catsA × $catsB nas categorias", color = NexusColors.TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun RatingBlock(rating: Double, name: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(rating.toString(), color = color, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(name, color = NexusColors.TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun PickerAvatar(character: Character, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(72.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        LabAvatar(url = character.imageMedium, size = 60.dp, ringColor = NexusColors.BorderStrong, contentDescription = character.name)
        Text(character.name, color = NexusColors.TextSecondary, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}
