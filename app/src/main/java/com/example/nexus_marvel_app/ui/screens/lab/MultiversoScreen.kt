package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.ui.components.EmptyState
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.NexusSearchBar
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiversoScreen(onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    val vm: MultiversoViewModel = viewModel(factory = MultiversoViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    LabScaffold(title = "Multiverso", onBack = onBack) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.md)) {
            NexusSearchBar(
                value = state.query,
                onValueChange = vm::setQuery,
                placeholder = "Nome base (ex: Spider, Hulk, Iron)",
            )

            Box(modifier = Modifier.fillMaxSize()) {
                // portal glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NexusColors.Info.copy(alpha = 0.18f), NexusColors.Background.copy(alpha = 0f)),
                            )
                        ),
                )

                when {
                    state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NexusColors.Info)
                    }
                    state.error != null -> ErrorState(message = state.error!!, onRetry = { vm.setQuery(state.query) })
                    !state.searched -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Digite um nome e abra o portal das variantes.",
                            color = NexusColors.TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(Spacing.xl),
                        )
                    }
                    state.variants.isEmpty() -> EmptyState(title = "Nenhuma variante", message = "Nada encontrado para \"${state.query}\".")
                    else -> FlowRow(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = Spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        state.variants.forEachIndexed { index, variant ->
                            VariantCard(variant, index, onClick = { onCharacterClick(variant.id) })
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(Spacing.xxl))
                    }
                }
            }
        }
    }
}

@Composable
private fun VariantCard(variant: Character, index: Int, onClick: () -> Unit) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(variant.id) {
        delay(index * 35L)
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    Column(
        modifier = Modifier
            .width(96.dp)
            .scale(scale.value)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        LabAvatar(url = variant.imageMedium, size = 88.dp, ringColor = NexusColors.Info, contentDescription = variant.name)
        Text(variant.name, color = NexusColors.TextPrimary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        variant.firstYear?.let { Text(it, color = NexusColors.TextMuted, fontSize = 10.sp) }
    }
}
