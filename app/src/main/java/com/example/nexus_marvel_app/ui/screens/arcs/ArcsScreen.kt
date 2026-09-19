package com.example.nexus_marvel_app.ui.screens.arcs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.StoryArc
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.EmptyState
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing

private val CARD_HEIGHT = 240.dp

@Composable
fun ArcsScreen(contentPadding: PaddingValues, onArcClick: (Int) -> Unit) {
    val vm: ArcsViewModel = viewModel(factory = ArcsViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.85f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = Spacing.md),
    ) {
        Column(modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)) {
            Text("ARCOS ÉPICOS", fontFamily = BebasNeue, fontSize = 32.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary)
            Text("A saga da Marvel, em linha do tempo.", color = NexusColors.TextSecondary, fontSize = 14.sp)
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.CenterStart) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NexusColors.Red)
                }
                state.error != null -> ErrorState(message = state.error!!, onRetry = vm::load)
                state.arcs.isEmpty() -> EmptyState(title = "Sem arcos", message = "Nenhum arco encontrado.")
                else -> Timeline(
                    arcs = state.arcs,
                    cardWidth = cardWidth,
                    loadingMore = state.loadingMore,
                    onEndReached = vm::loadMore,
                    onArcClick = onArcClick,
                )
            }
        }

        // bottom breathing room for the floating tab bar
        Box(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
    }
}

@Composable
private fun Timeline(
    arcs: List<StoryArc>,
    cardWidth: androidx.compose.ui.unit.Dp,
    loadingMore: Boolean,
    onEndReached: () -> Unit,
    onArcClick: (Int) -> Unit,
) {
    val listState = rememberLazyListState()
    val count = arcs.size
    val shouldLoadMore by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            count > 0 && last >= count - 2
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) onEndReached() }

    Box(modifier = Modifier.fillMaxWidth().height(CARD_HEIGHT)) {
        // Timeline line behind the cards
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(2.dp)
                .background(NexusColors.Red.copy(alpha = 0.5f)),
        )

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(arcs, key = { it.id }) { arc ->
                ArcCard(arc = arc, width = cardWidth, onClick = { onArcClick(arc.id) })
            }
            if (loadingMore) {
                item {
                    Box(modifier = Modifier.width(80.dp).height(CARD_HEIGHT), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NexusColors.Red)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArcCard(arc: StoryArc, width: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(width)
            .height(CARD_HEIGHT)
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.SurfaceLight)
            .clickable(onClick = onClick),
    ) {
        AsyncPoster(url = arc.imageMedium, modifier = Modifier.fillMaxSize(), contentDescription = arc.name)
        GradientScrim(modifier = Modifier.fillMaxSize(), intensity = 0.95f)

        Column(
            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                arc.appearances?.let { NexusBadge(label = "$it edições", color = NexusColors.Gold, small = true) }
                arcYear(arc.firstAppearance)?.let { year ->
                    Text(year, color = NexusColors.TextSecondary, fontFamily = JetBrainsMono, fontSize = 11.sp)
                }
            }
            Text(
                arc.name.uppercase(),
                fontFamily = BebasNeue,
                fontSize = 26.sp,
                letterSpacing = 0.5.sp,
                color = NexusColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 28.sp,
            )
            arc.deck?.let { deck ->
                Text(deck, color = NexusColors.TextSecondary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

private fun arcYear(issueName: String?): String? =
    issueName?.let { Regex("(19|20)\\d{2}").find(it)?.value }
