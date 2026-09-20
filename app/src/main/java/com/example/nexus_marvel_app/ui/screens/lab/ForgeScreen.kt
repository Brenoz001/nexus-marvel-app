package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.data.FORGE_ACCENTS
import com.example.nexus_marvel_app.data.FORGE_EMBLEMS
import com.example.nexus_marvel_app.data.FORGE_ORIGINS
import com.example.nexus_marvel_app.data.FORGE_POWERS
import com.example.nexus_marvel_app.data.FORGE_TEAMS
import com.example.nexus_marvel_app.data.FORGE_WEAKNESSES
import com.example.nexus_marvel_app.data.ForgeEmblem
import com.example.nexus_marvel_app.data.ForgedHero
import com.example.nexus_marvel_app.data.HeroNameGen
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.util.shareText

private const val MAX_POWERS = 4

@Composable
fun ForgeScreen(onBack: () -> Unit) {
    var codename by remember { mutableStateOf("") }
    var originId by remember { mutableStateOf<String?>(null) }
    val powerIds = remember { mutableStateOf(setOf<String>()) }
    var team by remember { mutableStateOf<String?>(null) }
    var weaknessId by remember { mutableStateOf<String?>(null) }
    var emblemId by remember { mutableStateOf<String?>(null) }
    var accentIndex by remember { mutableStateOf(0) }
    var forged by remember { mutableStateOf(false) }

    val accent = FORGE_ACCENTS[accentIndex]

    val ready = codename.isNotBlank() && originId != null && powerIds.value.isNotEmpty() &&
        team != null && weaknessId != null && emblemId != null

    LabScaffold(title = "Forje seu Herói", onBack = onBack) {
        if (forged && ready) {
            val hero = ForgedHero(
                codename = codename.trim(),
                origin = FORGE_ORIGINS.first { it.id == originId },
                powers = FORGE_POWERS.filter { it.id in powerIds.value },
                team = team!!,
                weakness = FORGE_WEAKNESSES.first { it.id == weaknessId },
                emblem = FORGE_EMBLEMS.first { it.id == emblemId },
                accent = accent,
            )
            DossierView(hero = hero, onEdit = { forged = false })
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.md),
            ) {
                Text(
                    "Monte sua identidade. Combine origem, poderes e emblema — o NEXUS calcula seu nível de ameaça.",
                    color = NexusColors.TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = Spacing.md),
                )

                // Codename
                SectionLabel("Codinome")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Radius.md))
                            .background(NexusColors.Surface)
                            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(Radius.md))
                            .padding(horizontal = Spacing.md, vertical = 14.dp),
                    ) {
                        BasicTextField(
                            value = codename,
                            onValueChange = { if (it.length <= 24) codename = it },
                            singleLine = true,
                            textStyle = TextStyle(color = NexusColors.TextPrimary, fontSize = 16.sp, fontFamily = BebasNeue, letterSpacing = 1.sp),
                            cursorBrush = SolidColor(accent),
                            decorationBox = { inner ->
                                if (codename.isEmpty()) {
                                    Text("EX: FÊNIX NEGRA", color = NexusColors.TextMuted, fontFamily = BebasNeue, fontSize = 16.sp, letterSpacing = 1.sp)
                                }
                                inner()
                            },
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(Radius.md))
                            .background(accent.copy(alpha = 0.16f))
                            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(Radius.md))
                            .clickable { codename = HeroNameGen.random() },
                    ) {
                        Icon(Icons.Filled.Casino, contentDescription = "Gerar nome", tint = accent)
                    }
                }

                // Origin
                SectionLabel("Origem")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FORGE_ORIGINS.forEach { o ->
                        PickChip(label = o.label, icon = o.icon, selected = originId == o.id, accent = accent) { originId = o.id }
                    }
                }

                // Powers
                SectionLabel("Poderes  (${powerIds.value.size}/$MAX_POWERS)")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FORGE_POWERS.forEach { p ->
                        val on = p.id in powerIds.value
                        PickChip(label = p.label, icon = p.icon, selected = on, accent = accent) {
                            powerIds.value = when {
                                on -> powerIds.value - p.id
                                powerIds.value.size < MAX_POWERS -> powerIds.value + p.id
                                else -> powerIds.value
                            }
                        }
                    }
                }

                // Team
                SectionLabel("Afinidade de time")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FORGE_TEAMS.forEach { t ->
                        PickChip(label = t, icon = Icons.Filled.Groups, selected = team == t, accent = accent) { team = t }
                    }
                }

                // Weakness
                SectionLabel("Fraqueza")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FORGE_WEAKNESSES.forEach { w ->
                        PickChip(label = w.label, icon = w.icon, selected = weaknessId == w.id, accent = accent) { weaknessId = w.id }
                    }
                }

                // Emblem
                SectionLabel("Emblema")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FORGE_EMBLEMS.forEach { e ->
                        EmblemPick(emblem = e, selected = emblemId == e.id, accent = accent) { emblemId = e.id }
                    }
                }

                // Accent color
                SectionLabel("Cor")
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    FORGE_ACCENTS.forEachIndexed { i, c ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(if (i == accentIndex) 3.dp else 0.dp, NexusColors.TextPrimary, CircleShape)
                                .clickable { accentIndex = i },
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.lg))

                // Forge button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.lg))
                        .background(if (ready) accent else NexusColors.SurfaceLight)
                        .clickable(enabled = ready) { forged = true }
                        .padding(vertical = 16.dp),
                ) {
                    Text(
                        "FORJAR HERÓI",
                        fontFamily = BebasNeue,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp,
                        color = if (ready) NexusColors.Black else NexusColors.TextMuted,
                    )
                }
                if (!ready) {
                    Text(
                        "Preencha codinome, origem, ao menos 1 poder, time, fraqueza e emblema.",
                        color = NexusColors.TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                    )
                }

                Spacer(Modifier.height(Spacing.xxl))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(),
        fontFamily = BebasNeue,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = NexusColors.GoldSoft,
        modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.sm),
    )
}

@Composable
private fun PickChip(label: String, icon: ImageVector, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(if (selected) accent.copy(alpha = 0.18f) else NexusColors.Surface)
            .border(1.dp, if (selected) accent else NexusColors.Border, RoundedCornerShape(Radius.full))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) accent else NexusColors.TextSecondary, modifier = Modifier.size(16.dp))
        Text(label, color = if (selected) NexusColors.TextPrimary else NexusColors.TextSecondary, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun EmblemPick(emblem: ForgeEmblem, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(Radius.md))
            .background(if (selected) accent.copy(alpha = 0.20f) else NexusColors.Surface)
            .border(1.dp, if (selected) accent else NexusColors.Border, RoundedCornerShape(Radius.md))
            .clickable(onClick = onClick),
    ) {
        Icon(emblem.icon, contentDescription = null, tint = if (selected) accent else NexusColors.TextSecondary, modifier = Modifier.size(26.dp))
    }
}

@Composable
private fun DossierView(hero: ForgedHero, onEdit: () -> Unit) {
    val context = LocalContext.current
    val level by animateFloatAsState(targetValue = hero.powerLevel / 100f, animationSpec = tween(900), label = "level")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Spacing.sm))

        // Emblem medallion
        AnimatedVisibility(visible = true, enter = fadeIn(tween(400)) + scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy))) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(132.dp)
                    .drawBehind {
                        val r = size.minDimension * 0.85f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colorStops = arrayOf(
                                    0.45f to hero.accent.copy(alpha = 0.45f),
                                    1.0f to Color.Transparent,
                                ),
                                center = center,
                                radius = r,
                            ),
                            radius = r,
                        )
                    }
                    .clip(CircleShape)
                    .background(NexusColors.Surface)
                    .border(2.dp, hero.accent, CircleShape),
            ) {
                Icon(hero.emblem.icon, contentDescription = null, tint = hero.accent, modifier = Modifier.size(60.dp))
            }
        }

        Spacer(Modifier.height(Spacing.md))

        Text(hero.codename.uppercase(), fontFamily = BebasNeue, fontSize = 44.sp, letterSpacing = 2.sp, color = NexusColors.TextPrimary, textAlign = TextAlign.Center)

        // Classification badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(Radius.full))
                .background(hero.accent.copy(alpha = 0.16f))
                .border(1.dp, hero.accent.copy(alpha = 0.5f), RoundedCornerShape(Radius.full))
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text("CLASSE ${hero.classification}", fontFamily = BebasNeue, fontSize = 16.sp, letterSpacing = 1.sp, color = hero.accent)
        }

        Spacer(Modifier.height(Spacing.lg))

        // Power level bar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("NÍVEL DE PODER", fontFamily = BebasNeue, fontSize = 15.sp, letterSpacing = 1.sp, color = NexusColors.TextSecondary)
            Text("${hero.powerLevel}", fontFamily = BebasNeue, fontSize = 15.sp, color = hero.accent)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(Radius.full))
                .background(NexusColors.SurfaceLight),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(level)
                    .height(10.dp)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(Brush.horizontalGradient(listOf(hero.accent.copy(alpha = 0.6f), hero.accent))),
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // Origin + team
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            InfoCard(label = "ORIGEM", value = hero.origin.label, icon = hero.origin.icon, accent = hero.accent, modifier = Modifier.weight(1f))
            InfoCard(label = "TIME", value = hero.team, icon = Icons.Filled.Groups, accent = hero.accent, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(Spacing.md))

        // Powers
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("PODERES", fontFamily = BebasNeue, fontSize = 15.sp, letterSpacing = 1.sp, color = NexusColors.TextSecondary, modifier = Modifier.padding(bottom = Spacing.sm))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                hero.powers.forEach { p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.full))
                            .background(hero.accent.copy(alpha = 0.14f))
                            .border(1.dp, hero.accent.copy(alpha = 0.4f), RoundedCornerShape(Radius.full))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Icon(p.icon, contentDescription = null, tint = hero.accent, modifier = Modifier.size(16.dp))
                        Text(p.label, color = NexusColors.TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Story card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.lg))
                .background(NexusColors.Surface)
                .border(1.dp, NexusColors.Border, RoundedCornerShape(Radius.lg))
                .padding(Spacing.md),
        ) {
            Text("HISTÓRIA DE ORIGEM", fontFamily = BebasNeue, fontSize = 15.sp, letterSpacing = 1.sp, color = hero.accent, modifier = Modifier.padding(bottom = 6.dp))
            Text(hero.story, color = NexusColors.TextPrimary, fontSize = 14.sp, lineHeight = 20.sp)
            Text("Fraqueza: ${hero.weakness.label}", color = NexusColors.TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = Spacing.sm))
        }

        Spacer(Modifier.height(Spacing.lg))

        // Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            ActionButton(label = "EDITAR", icon = Icons.Filled.Edit, filled = false, accent = hero.accent, modifier = Modifier.weight(1f), onClick = onEdit)
            ActionButton(label = "COMPARTILHAR", icon = Icons.Filled.Share, filled = true, accent = hero.accent, modifier = Modifier.weight(1f)) {
                shareText(context, buildShareText(hero), title = "Compartilhar herói")
            }
        }

        Spacer(Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun InfoCard(label: String, value: String, icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.Surface)
            .border(1.dp, NexusColors.Border, RoundedCornerShape(Radius.lg))
            .padding(Spacing.md),
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Text(label, color = NexusColors.TextMuted, fontSize = 11.sp, letterSpacing = 1.sp, modifier = Modifier.padding(top = 6.dp))
        Text(value, color = NexusColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActionButton(label: String, icon: ImageVector, filled: Boolean, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.lg))
            .background(if (filled) accent else Color.Transparent)
            .border(1.dp, accent, RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
    ) {
        Icon(icon, contentDescription = null, tint = if (filled) NexusColors.Black else accent, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontFamily = BebasNeue, fontSize = 16.sp, letterSpacing = 1.sp, color = if (filled) NexusColors.Black else accent)
    }
}

private fun buildShareText(hero: ForgedHero): String = buildString {
    appendLine("⚡ ${hero.codename} — Classe ${hero.classification} (Nível ${hero.powerLevel})")
    appendLine("Origem: ${hero.origin.label}")
    appendLine("Poderes: ${hero.powers.joinToString { it.label }}")
    appendLine("Time: ${hero.team}")
    appendLine("Fraqueza: ${hero.weakness.label}")
    appendLine()
    appendLine(hero.story)
    appendLine()
    append("Forjado no NEXUS — Todo herói está conectado.")
}
