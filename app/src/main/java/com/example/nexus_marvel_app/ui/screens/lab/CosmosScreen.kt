package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.data.MARVEL_PLANETS
import com.example.nexus_marvel_app.data.Planet
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing

@Composable
fun CosmosScreen(onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    val vm: CosmosViewModel = viewModel(factory = CosmosViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf<String?>("Terra") }

    LabScaffold(title = "Cosmos", onBack = onBack) {
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NexusColors.Gold)
            }
            state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
            else -> LazyColumn(
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                items(MARVEL_PLANETS, key = { it.name }) { planet ->
                    PlanetCard(
                        planet = planet,
                        characters = vm.charactersFor(planet.characters),
                        expanded = expanded == planet.name,
                        onToggle = { expanded = if (expanded == planet.name) null else planet.name },
                        onCharacterClick = onCharacterClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanetCard(
    planet: Planet,
    characters: List<Character>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onCharacterClick: (Int) -> Unit,
) {
    val arrow by animateFloatAsState(if (expanded) 180f else 0f, label = "arrow")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.Surface)
            .border(1.dp, planet.color.copy(alpha = 0.35f), RoundedCornerShape(Radius.lg))
            .clickable(onClick = onToggle)
            .padding(Spacing.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(planet.color, planet.color.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.5f))
                        )
                    )
                    .border(1.dp, planet.color.copy(alpha = 0.6f), CircleShape),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(planet.name.uppercase(), fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 0.5.sp, color = NexusColors.TextPrimary)
                Text(planet.realm, color = NexusColors.TextSecondary, fontSize = 12.sp)
                Text("${characters.size} heróis", color = NexusColors.TextMuted, fontSize = 11.sp)
            }
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = planet.color,
                modifier = Modifier.rotate(arrow),
            )
        }

        AnimatedVisibility(visible = expanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                characters.forEach { c ->
                    Column(
                        modifier = Modifier.width(72.dp).clickable { onCharacterClick(c.id) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    ) {
                        LabAvatar(url = c.imageMedium, size = 60.dp, ringColor = planet.color, contentDescription = c.name)
                        Text(c.name, color = NexusColors.TextSecondary, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
    }
}
