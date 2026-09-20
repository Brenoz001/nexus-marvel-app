package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.util.PowerCategory
import com.example.nexus_marvel_app.util.computePowerScores
import com.example.nexus_marvel_app.util.overallRating
import kotlin.random.Random
import kotlinx.coroutines.delay

private val COLOR_A = NexusColors.Red
private val COLOR_B = NexusColors.Info

@Composable
fun ConfrontoScreen(onBack: () -> Unit) {
    val vm: ConfrontoViewModel = viewModel(factory = ConfrontoViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    var battleActive by remember { mutableStateOf(false) }

    LabScaffold(title = "Confronto", onBack = onBack) {
        when {
            state.loading -> Box(Modifier.fillMaxWidth().padding(Spacing.xxl), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NexusColors.Red)
            }
            state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
            battleActive && state.fighterA != null && state.fighterB != null ->
                BattleArena(state.fighterA!!, state.fighterB!!, onExit = { battleActive = false })
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
                    // Enter the turn-based battle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.md)
                            .clip(RoundedCornerShape(Radius.lg))
                            .background(NexusColors.Gold)
                            .clickable { battleActive = true }
                            .padding(vertical = 14.dp),
                    ) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, tint = NexusColors.Black, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("BATALHAR", fontFamily = BebasNeue, fontSize = 22.sp, letterSpacing = 2.sp, color = NexusColors.Black)
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

// ─────────────────────────────  BATALHA POR TURNOS  ─────────────────────────────

private enum class MoveKind { ATTACK, GUARD }
private data class Move(val name: String, val category: PowerCategory, val kind: MoveKind)
private enum class Turn { PLAYER, ENEMY, OVER }

private fun attackName(c: PowerCategory): String = when (c) {
    PowerCategory.FORCA -> "Golpe Esmagador"
    PowerCategory.VELOCIDADE -> "Ataque Relâmpago"
    PowerCategory.INTELIGENCIA -> "Golpe Tático"
    PowerCategory.ENERGIA -> "Rajada de Energia"
    PowerCategory.RESISTENCIA -> "Investida"
    PowerCategory.COMBATE -> "Combo Marcial"
}

private fun movesFor(scores: Map<PowerCategory, Int>): List<Move> =
    PowerCategory.entries.sortedByDescending { scores[it] ?: 0 }.take(3).map { c ->
        if (c == PowerCategory.RESISTENCIA) Move("Postura Defensiva", c, MoveKind.GUARD)
        else Move(attackName(c), c, MoveKind.ATTACK)
    }

private fun maxHpOf(scores: Map<PowerCategory, Int>): Int =
    70 + (scores[PowerCategory.RESISTENCIA] ?: 1) * 6 + (scores[PowerCategory.FORCA] ?: 1) * 2

/** Returns (damage, isCrit). */
private fun damageOf(atk: Map<PowerCategory, Int>, def: Map<PowerCategory, Int>, cat: PowerCategory): Pair<Int, Boolean> {
    val base = (atk[cat] ?: 1) * 3
    val variance = Random.nextInt(0, 9)
    val mitig = (def[PowerCategory.RESISTENCIA] ?: 1) / 2
    var dmg = (base + variance - mitig).coerceAtLeast(4)
    val crit = Random.nextInt(0, 100) < (atk[PowerCategory.VELOCIDADE] ?: 1) * 3
    if (crit) dmg = (dmg * 1.5f).toInt()
    return dmg to crit
}

private fun healOf(scores: Map<PowerCategory, Int>): Int = (scores[PowerCategory.RESISTENCIA] ?: 1) * 3 + 8

@Composable
private fun BattleArena(a: Character, b: Character, onExit: () -> Unit) {
    val scoresA = remember(a.id) { computePowerScores(a.powers) }
    val scoresB = remember(b.id) { computePowerScores(b.powers) }
    val movesA = remember(a.id) { movesFor(scoresA) }
    val movesB = remember(b.id) { movesFor(scoresB) }
    val maxA = remember(a.id) { maxHpOf(scoresA) }
    val maxB = remember(b.id) { maxHpOf(scoresB) }

    var hpA by remember(a.id, b.id) { mutableStateOf(maxA) }
    var hpB by remember(a.id, b.id) { mutableStateOf(maxB) }
    var turn by remember(a.id, b.id) { mutableStateOf(Turn.PLAYER) }
    var winner by remember(a.id, b.id) { mutableStateOf<String?>(null) }
    var log by remember(a.id, b.id) { mutableStateOf(listOf("A batalha começou! ${a.name} enfrenta ${b.name}.")) }
    var popA by remember(a.id, b.id) { mutableStateOf<Pair<String, Color>?>(null) }
    var popB by remember(a.id, b.id) { mutableStateOf<Pair<String, Color>?>(null) }

    fun pushLog(line: String) { log = (log + line).takeLast(6) }

    fun applyMove(move: Move, byA: Boolean) {
        val attackerName = if (byA) a.name else b.name
        val atkScores = if (byA) scoresA else scoresB
        val defScores = if (byA) scoresB else scoresA
        if (move.kind == MoveKind.GUARD) {
            val heal = healOf(atkScores)
            if (byA) { hpA = (hpA + heal).coerceAtMost(maxA); popA = "+$heal" to NexusColors.Success }
            else { hpB = (hpB + heal).coerceAtMost(maxB); popB = "+$heal" to NexusColors.Success }
            pushLog("$attackerName usa ${move.name} e recupera $heal de vida.")
        } else {
            val (dmg, crit) = damageOf(atkScores, defScores, move.category)
            if (byA) { hpB = (hpB - dmg).coerceAtLeast(0); popB = "-$dmg" to NexusColors.RedLight }
            else { hpA = (hpA - dmg).coerceAtLeast(0); popA = "-$dmg" to NexusColors.RedLight }
            pushLog("$attackerName usa ${move.name} e causa $dmg${if (crit) " (CRÍTICO!)" else ""} de dano.")
        }
        val targetDead = if (byA) hpB <= 0 else hpA <= 0
        when {
            targetDead -> {
                winner = attackerName
                turn = Turn.OVER
                pushLog("$attackerName venceu o confronto!")
            }
            else -> turn = if (byA) Turn.ENEMY else Turn.PLAYER
        }
    }

    // Enemy AI turn
    LaunchedEffect(turn) {
        if (turn == Turn.ENEMY && winner == null) {
            delay(850)
            val guard = movesB.firstOrNull { it.kind == MoveKind.GUARD }
            val move = if (guard != null && hpB < maxB * 0.3f && Random.nextInt(100) < 60) {
                guard
            } else {
                movesB.filter { it.kind == MoveKind.ATTACK }.ifEmpty { movesB }.random()
            }
            applyMove(move, byA = false)
        }
    }
    // Clear damage popups
    LaunchedEffect(popA) { if (popA != null) { delay(900); popA = null } }
    LaunchedEffect(popB) { if (popB != null) { delay(900); popB = null } }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md)) {
        // Fighters + HP
        Row(modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm), verticalAlignment = Alignment.Top) {
            FighterPanel(a.name, a.imageMedium, hpA, maxA, COLOR_A, popA, Modifier.weight(1f))
            Text("VS", color = NexusColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xl))
            FighterPanel(b.name, b.imageMedium, hpB, maxB, COLOR_B, popB, Modifier.weight(1f))
        }

        // Battle log
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md)
                .clip(RoundedCornerShape(Radius.md))
                .background(NexusColors.Surface)
                .border(1.dp, NexusColors.Border, RoundedCornerShape(Radius.md))
                .padding(Spacing.md)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            log.forEach { line ->
                Text(line, color = NexusColors.TextSecondary, fontSize = 12.sp, fontFamily = JetBrainsMono)
            }
        }

        // Controls
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            when (turn) {
                Turn.PLAYER -> {
                    Text("SUA VEZ — ${a.name}", color = COLOR_A, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        movesA.forEach { m -> MoveButton(m, COLOR_A) { applyMove(m, byA = true) } }
                    }
                }
                Turn.ENEMY -> Text("${b.name} está atacando…", color = COLOR_B, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Turn.OVER -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = NexusColors.Gold, modifier = Modifier.size(24.dp))
                        Text("$winner venceu!", color = NexusColors.Gold, fontFamily = BebasNeue, fontSize = 26.sp, letterSpacing = 1.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        BattleActionButton("REVANCHE", NexusColors.Gold, filled = true, Modifier.weight(1f)) {
                            hpA = maxA; hpB = maxB; winner = null; turn = Turn.PLAYER
                            log = listOf("Revanche! ${a.name} enfrenta ${b.name}.")
                        }
                        BattleActionButton("SAIR", NexusColors.TextSecondary, filled = false, Modifier.weight(1f), onClick = onExit)
                    }
                }
            }
        }
    }
}

@Composable
private fun FighterPanel(name: String, url: String?, hp: Int, max: Int, color: Color, pop: Pair<String, Color>?, modifier: Modifier = Modifier) {
    val frac by animateFloatAsState(targetValue = (hp.toFloat() / max).coerceIn(0f, 1f), animationSpec = tween(450), label = "hp")
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Box(contentAlignment = Alignment.TopCenter) {
            LabAvatar(url = url, size = 76.dp, ringColor = color, ringWidth = 2.5.dp, contentDescription = name)
            if (pop != null) {
                Text(pop.first, color = pop.second, fontFamily = BebasNeue, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(name, color = NexusColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        // HP bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(Radius.full))
                .background(NexusColors.SurfaceLight),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(frac)
                    .height(10.dp)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(if (frac < 0.3f) NexusColors.Danger else color),
            )
        }
        Text("$hp / $max", color = NexusColors.TextSecondary, fontSize = 11.sp, fontFamily = JetBrainsMono)
    }
}

@Composable
private fun MoveButton(move: Move, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(color.copy(alpha = 0.16f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(Radius.md))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        Text(move.name, color = NexusColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(
            if (move.kind == MoveKind.GUARD) "Defesa • ${move.category.short}" else "Ataque • ${move.category.short}",
            color = NexusColors.TextMuted,
            fontSize = 10.sp,
        )
    }
}

@Composable
private fun BattleActionButton(label: String, color: Color, filled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.lg))
            .background(if (filled) color else Color.Transparent)
            .border(1.dp, color, RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.md),
    ) {
        Text(label, fontFamily = BebasNeue, fontSize = 18.sp, letterSpacing = 1.sp, color = if (filled) NexusColors.Black else color)
    }
}
