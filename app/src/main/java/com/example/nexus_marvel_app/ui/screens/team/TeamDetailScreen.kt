package com.example.nexus_marvel_app.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.NamedRef
import com.example.nexus_marvel_app.domain.model.Team
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.components.SkeletonBox
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.nexusBackground
import com.example.nexus_marvel_app.ui.theme.teamColor
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TeamDetailScreen(teamId: Int, onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    var team by remember { mutableStateOf<Team?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var reload by remember { mutableStateOf(0) }

    LaunchedEffect(teamId, reload) {
        loading = true
        error = null
        try {
            team = Graph.repository.getTeam(teamId)
        } catch (e: ComicVineException) {
            error = e.message
        }
        loading = false
    }

    val heroHeight = LocalConfiguration.current.screenWidthDp.dp * 0.95f

    Box(modifier = Modifier.fillMaxSize().nexusBackground()) {
        if (error != null) {
            ErrorState(message = error!!, onRetry = { reload++ })
        } else {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().height(heroHeight)) {
                    if (loading) {
                        SkeletonBox(modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp)
                    } else {
                        AsyncPoster(
                            url = team?.imageOriginal ?: team?.imageMedium,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            contentDescription = team?.name,
                        )
                    }
                    GradientScrim(modifier = Modifier.fillMaxSize(), intensity = 0.98f)
                    team?.let { t ->
                        val accent = teamColor(t.name)
                        Column(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(accent))
                            Text(t.name.uppercase(), fontFamily = BebasNeue, fontSize = 44.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, lineHeight = 46.sp)
                        }
                    }
                }

                team?.let { t -> Body(t, onCharacterClick) }
            }
        }

        NexusBackButton(onClick = onBack, modifier = Modifier.statusBarsPadding().padding(Spacing.md).align(Alignment.TopStart))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Body(team: Team, onCharacterClick: (Int) -> Unit) {
    val accent = teamColor(team.name)
    Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md)) {
            Stat((team.memberCount ?: team.members.size).takeIf { it > 0 }?.toString() ?: "—", "Membros")
            Stat(team.appearances?.let { NumberFormat.getInstance(Locale("pt", "BR")).format(it) } ?: "—", "Aparições")
        }
        HorizontalDivider(color = NexusColors.Border)

        team.deck?.let {
            Text("SOBRE", fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, modifier = Modifier.padding(top = Spacing.lg))
            Text(it, color = NexusColors.TextSecondary, fontSize = 15.sp, lineHeight = 24.sp, modifier = Modifier.padding(top = Spacing.sm))
        }

        if (team.members.isNotEmpty()) {
            Text("MEMBROS", fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.sm))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
                team.members.take(40).forEach { ref -> MemberAvatar(ref, accent, onClick = { onCharacterClick(ref.id) }) }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Stat(value: String, label: String) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexusColors.TextPrimary)
        Text(label.uppercase(), color = NexusColors.TextMuted, fontSize = 10.sp, letterSpacing = 0.5.sp)
    }
}

@Composable
private fun MemberAvatar(ref: NamedRef, accent: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(72.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp).clip(CircleShape).background(NexusColors.SurfaceLight)) {
            Text(initials(ref.name), color = accent, fontFamily = BebasNeue, fontSize = 22.sp)
        }
        Text(ref.name, color = NexusColors.TextSecondary, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}
