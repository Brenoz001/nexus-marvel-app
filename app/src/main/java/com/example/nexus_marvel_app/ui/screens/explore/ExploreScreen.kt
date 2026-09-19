package com.example.nexus_marvel_app.ui.screens.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.domain.model.Power
import com.example.nexus_marvel_app.domain.model.Team
import com.example.nexus_marvel_app.ui.components.EmptyState
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.NexusFilterChip
import com.example.nexus_marvel_app.ui.components.NexusSearchBar
import com.example.nexus_marvel_app.ui.components.SkeletonBox
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    contentPadding: PaddingValues,
    onCharacterClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
) {
    val vm: ExploreViewModel = viewModel(factory = ExploreViewModel.Factory)
    val state by vm.uiState.collectAsStateWithLifecycle()
    val bottomPad = contentPadding.calculateBottomPadding() + Spacing.md

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = Spacing.md),
    ) {
        Column(modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.md)) {
            Text(
                "EXPLORAR",
                fontFamily = BebasNeue,
                fontSize = 32.sp,
                letterSpacing = 1.sp,
                color = NexusColors.TextPrimary,
            )
            Text(
                "O universo Marvel, conectado.",
                color = NexusColors.TextSecondary,
                fontSize = 14.sp,
            )
        }

        NexusSearchBar(value = state.query, onValueChange = vm::setQuery)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ExploreFilter.entries.forEach { f ->
                NexusFilterChip(
                    label = f.label,
                    selected = state.filter == f,
                    onClick = { vm.setFilter(f) },
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.loading -> LoadingContent(state.mode == ExploreMode.POWERS, bottomPad)
                state.error != null -> ErrorState(message = state.error!!, onRetry = vm::retry)
                state.items.isEmpty() -> EmptyState(
                    title = "Nenhum resultado",
                    message = if (state.query.isNotEmpty()) {
                        "Nada encontrado para \"${state.query}\"."
                    } else {
                        "Tente outro filtro para explorar."
                    },
                )
                else -> PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = vm::refresh) {
                    if (state.mode == ExploreMode.POWERS) {
                        PowersList(state, vm, bottomPad)
                    } else {
                        ItemsGrid(state, vm, bottomPad, onCharacterClick, onTeamClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemsGrid(
    state: ExploreUiState,
    vm: ExploreViewModel,
    bottomPad: Dp,
    onCharacterClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
) {
    val gridState = rememberLazyGridState()
    val itemCount = state.items.size
    val shouldLoadMore by remember(itemCount) {
        derivedStateOf {
            val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            itemCount > 0 && last >= itemCount - 4
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) vm.loadMore() }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        contentPadding = PaddingValues(bottom = bottomPad),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(state.items, key = { _, item -> itemKey(item) }) { _, item ->
            when (item) {
                is Character -> CharacterCard(character = item, onClick = onCharacterClick)
                is Team -> TeamCard(team = item, onClick = onTeamClick)
            }
        }
        if (state.loadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) { LoadingRow() }
        }
    }
}

@Composable
private fun PowersList(state: ExploreUiState, vm: ExploreViewModel, bottomPad: Dp) {
    val listState = rememberLazyListState()
    val itemCount = state.items.size
    val shouldLoadMore by remember(itemCount) {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            itemCount > 0 && last >= itemCount - 4
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) vm.loadMore() }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        contentPadding = PaddingValues(bottom = bottomPad),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(state.items, key = { itemKey(it) }) { item ->
            if (item is Power) PowerCard(power = item)
        }
        if (state.loadingMore) {
            item { LoadingRow() }
        }
    }
}

@Composable
private fun LoadingContent(isPowers: Boolean, bottomPad: Dp) {
    if (isPowers) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            contentPadding = PaddingValues(bottom = bottomPad),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(8) { SkeletonBox(modifier = Modifier.fillMaxWidth().height(72.dp), cornerRadius = 10.dp) }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            contentPadding = PaddingValues(bottom = bottomPad),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(8) { SkeletonBox(modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f)) }
        }
    }
}

@Composable
private fun LoadingRow() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = NexusColors.Gold)
    }
}

private fun itemKey(item: Any): Any = when (item) {
    is Character -> "c${item.id}"
    is Team -> "t${item.id}"
    is Power -> "p${item.id}"
    else -> item.hashCode()
}
