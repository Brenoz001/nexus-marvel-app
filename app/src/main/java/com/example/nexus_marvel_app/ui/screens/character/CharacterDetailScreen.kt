package com.example.nexus_marvel_app.ui.screens.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.domain.model.FavoriteItem
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.FavoriteButton
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.components.SkeletonBox
import com.example.nexus_marvel_app.ui.favorites.LocalFavorites
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
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
                if (!state.loading && state.character != null) {
                    Body(state.character!!)
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
                    favorites.toggle(
                        FavoriteItem(c.id, c.name, "character", c.imageMedium, c.publisherName)
                    )
                },
                modifier = Modifier.statusBarsPadding().padding(Spacing.md).align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun Hero(loading: Boolean, character: Character?, heroHeight: androidx.compose.ui.unit.Dp) {
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
private fun Body(character: Character) {
    Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
        // Stats row
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md)) {
            Stat(
                value = character.appearances?.let {
                    NumberFormat.getInstance(Locale("pt", "BR")).format(it)
                } ?: "—",
                label = "Aparições",
            )
            Stat(value = character.teams.size.takeIf { it > 0 }?.toString() ?: "—", label = "Times")
            Stat(value = character.firstYear ?: "—", label = "1ª Aparição")
        }

        HorizontalDivider(color = NexusColors.Border)

        character.deck?.let { deck ->
            Column(modifier = Modifier.padding(top = Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text("SOBRE", fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary)
                Text(deck, color = NexusColors.TextSecondary, fontSize = 15.sp, lineHeight = 24.sp)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier
                .padding(top = Spacing.lg)
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.md))
                .background(NexusColors.Gold.copy(alpha = 0.08f))
                .padding(Spacing.md),
        ) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = NexusColors.Gold)
            Text(
                "DNA de Poderes, Trilha do Herói e Conexões chegam na etapa 3.",
                color = NexusColors.TextSecondary,
                fontSize = 13.sp,
            )
        }
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
