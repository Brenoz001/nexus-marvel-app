package com.example.nexus_marvel_app.ui.screens.character

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.domain.model.FavoriteItem
import com.example.nexus_marvel_app.domain.model.NamedRef
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.FavoriteButton
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.components.PowerRadar
import com.example.nexus_marvel_app.ui.components.SkeletonBox
import com.example.nexus_marvel_app.ui.favorites.LocalFavorites
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.teamColor
import com.example.nexus_marvel_app.util.Soundtrack
import com.example.nexus_marvel_app.util.Soundtracks
import com.example.nexus_marvel_app.util.computePowerScores
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBack: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
) {
    val vm: CharacterDetailViewModel = viewModel(factory = CharacterDetailViewModel.factory(characterId))
    val state by vm.uiState.collectAsStateWithLifecycle()
    val favorites = LocalFavorites.current
    val favoriteIds by favorites.favoriteIds.collectAsStateWithLifecycle()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val heroHeight = screenWidth * 1.15f

    Box(modifier = Modifier.fillMaxSize().background(NexusColors.Background)) {
        when {
            state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
            else -> Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Hero(state.loading, state.character, heroHeight)
                state.character?.let { c ->
                    Body(character = c, onCharacterClick = onCharacterClick, onTeamClick = onTeamClick)
                }
            }
        }

        NexusBackButton(
            onClick = onBack,
            modifier = Modifier.statusBarsPadding().padding(Spacing.md).align(Alignment.TopStart),
        )
        state.character?.let { c ->
            FavoriteButton(
                favorite = c.id in favoriteIds,
                onClick = {
                    favorites.toggle(FavoriteItem(c.id, c.name, "character", c.imageMedium, c.publisherName))
                },
                modifier = Modifier.statusBarsPadding().padding(Spacing.md).align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun Hero(loading: Boolean, character: Character?, heroHeight: Dp) {
    Box(modifier = Modifier.fillMaxWidth().height(heroHeight)) {
        if (loading) {
            SkeletonBox(modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp)
        } else {
            AsyncPoster(
                url = character?.imageOriginal ?: character?.imageMedium,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                contentDescription = character?.name,
            )
        }
        GradientScrim(modifier = Modifier.fillMaxSize(), intensity = 0.98f)
        GradientScrim(modifier = Modifier.fillMaxSize(), fromBottom = false, intensity = 0.5f)

        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (!loading && character != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    if (character.isMarvel) {
                        NexusBadge(label = "Marvel", color = NexusColors.Red)
                    } else if (character.publisherName != null) {
                        NexusBadge(label = character.publisherName, color = NexusColors.Info)
                    }
                }
                Text(
                    text = character.name.uppercase(),
                    fontFamily = BebasNeue,
                    fontSize = 46.sp,
                    letterSpacing = 1.5.sp,
                    color = NexusColors.TextPrimary,
                    lineHeight = 48.sp,
                )
                character.realName?.let {
                    Text(it, color = NexusColors.TextSecondary, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun Body(character: Character, onCharacterClick: (Int) -> Unit, onTeamClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
        // Stats
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md)) {
            Stat(
                value = character.appearances?.let { NumberFormat.getInstance(Locale("pt", "BR")).format(it) } ?: "—",
                label = "Aparições",
            )
            Stat(value = character.teams.size.takeIf { it > 0 }?.toString() ?: "—", label = "Times")
            Stat(value = character.firstYear ?: "—", label = "1ª Aparição")
        }

        HorizontalDivider(color = NexusColors.Border)

        // Sobre
        character.deck?.let { deck ->
            Section(title = "Sobre") {
                Text(deck, color = NexusColors.TextSecondary, fontSize = 15.sp, lineHeight = 24.sp)
            }
        }

        // Trilha do Herói
        Soundtracks.forCharacter(character.name)?.let { track ->
            Section(title = "Trilha do Herói") {
                SoundtrackCard(track)
            }
        }

        // DNA de Poderes
        Section(title = "DNA de Poderes") {
            PowerRadar(
                scores = computePowerScores(character.powers),
                modifier = Modifier.fillMaxWidth().height(280.dp),
            )
            if (character.powers.isNotEmpty()) {
                PowerBadges(character.powers)
            } else {
                Text(
                    "Sem poderes catalogados para este personagem.",
                    color = NexusColors.TextMuted,
                    fontSize = 13.sp,
                )
            }
        }

        // Conexões
        if (character.friends.isNotEmpty() || character.enemies.isNotEmpty()) {
            Section(title = "Conexões") {
                if (character.friends.isNotEmpty()) {
                    ConnectionRow("Aliados", character.friends, NexusColors.Info, onCharacterClick)
                }
                if (character.enemies.isNotEmpty()) {
                    ConnectionRow("Inimigos", character.enemies, NexusColors.Red, onCharacterClick)
                }
            }
        }

        // Times
        if (character.teams.isNotEmpty()) {
            Section(title = "Times") {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    character.teams.forEach { team ->
                        TeamChip(team, onClick = { onTeamClick(team.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(top = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(title.uppercase(), fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary)
        content()
    }
}

@Composable
private fun SoundtrackCard(track: Soundtrack) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(NexusColors.Gold.copy(alpha = 0.08f))
            .border(1.dp, NexusColors.Gold.copy(alpha = 0.25f), RoundedCornerShape(Radius.md))
            .padding(Spacing.md),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(Radius.md)).background(NexusColors.Gold.copy(alpha = 0.16f)),
        ) {
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = NexusColors.Gold, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(track.track, color = NexusColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${track.artist} • ${track.movie}", color = NexusColors.TextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PowerBadges(powers: List<NamedRef>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        powers.take(16).forEach { power ->
            NexusBadge(label = power.name, color = NexusColors.Gold)
        }
    }
}

@Composable
private fun ConnectionRow(label: String, refs: List<NamedRef>, accent: androidx.compose.ui.graphics.Color, onClick: (Int) -> Unit) {
    Text(label, color = NexusColors.TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        refs.take(20).forEach { ref ->
            ConnectionAvatar(ref, accent, onClick = { onClick(ref.id) })
        }
    }
}

@Composable
private fun ConnectionAvatar(ref: NamedRef, accent: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = Modifier.width(72.dp).clickable(onClick = onClick),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(NexusColors.SurfaceLight)
                .border(2.dp, accent.copy(alpha = 0.6f), CircleShape),
        ) {
            Text(initials(ref.name), color = accent, fontFamily = BebasNeue, fontSize = 22.sp)
        }
        Text(
            ref.name,
            color = NexusColors.TextSecondary,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TeamChip(team: NamedRef, onClick: () -> Unit) {
    val accent = teamColor(team.name)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(Radius.full))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent))
        Text(team.name, color = NexusColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Stat(value: String, label: String) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(value, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NexusColors.TextPrimary)
        Text(label.uppercase(), color = NexusColors.TextMuted, fontSize = 10.sp, letterSpacing = 0.5.sp, textAlign = TextAlign.Center)
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
