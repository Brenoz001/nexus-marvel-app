package com.example.nexus_marvel_app.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.data.repository.ComicVineRepository
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.Alignment
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.util.classifyAlignment
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ExploreFilter(val label: String) {
    ALL("Todos"),
    HERO("Heróis"),
    VILLAIN("Vilões"),
    TEAMS("Times"),
    POWERS("Poderes");

    val mode: ExploreMode
        get() = when (this) {
            TEAMS -> ExploreMode.TEAMS
            POWERS -> ExploreMode.POWERS
            else -> ExploreMode.CHARACTERS
        }
}

enum class ExploreMode { CHARACTERS, TEAMS, POWERS }

data class ExploreUiState(
    val query: String = "",
    val filter: ExploreFilter = ExploreFilter.ALL,
    val mode: ExploreMode = ExploreMode.CHARACTERS,
    val items: List<Any> = emptyList(),
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    val refreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
)

@OptIn(FlowPreview::class)
class ExploreViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _filter = MutableStateFlow(ExploreFilter.ALL)

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var serverOffset = 0
    private var serverTotal = Int.MAX_VALUE
    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                _query.debounce { if (it.isEmpty()) 0L else 500L }.distinctUntilChanged(),
                _filter,
            ) { q, f -> q to f }
                .collect { (q, f) -> load(q, f, refreshing = false) }
        }
    }

    fun setQuery(q: String) {
        _query.value = q
        _uiState.update { it.copy(query = q) }
    }

    fun setFilter(f: ExploreFilter) {
        _filter.value = f
        _uiState.update { it.copy(filter = f, mode = f.mode) }
    }

    fun refresh() {
        load(_query.value, _filter.value, refreshing = true)
    }

    fun retry() {
        load(_query.value, _filter.value, refreshing = false)
    }

    private fun load(query: String, filter: ExploreFilter, refreshing: Boolean) {
        loadJob?.cancel()
        loadMoreJob?.cancel()
        serverOffset = 0
        serverTotal = Int.MAX_VALUE
        _uiState.update {
            it.copy(
                query = query, filter = filter, mode = filter.mode,
                loading = !refreshing, refreshing = refreshing, error = null,
                items = if (refreshing) it.items else emptyList(),
            )
        }
        loadJob = viewModelScope.launch {
            try {
                val (items, total, fetched) = fetchPage(query, filter, offset = 0)
                serverOffset = fetched
                serverTotal = total
                _uiState.update {
                    it.copy(
                        items = items,
                        loading = false, refreshing = false, error = null,
                        hasMore = serverOffset < serverTotal && fetched > 0,
                    )
                }
            } catch (e: ComicVineException) {
                _uiState.update {
                    it.copy(loading = false, refreshing = false, error = e.message, items = emptyList())
                }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasMore || state.loading || state.loadingMore || state.refreshing) return
        loadMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingMore = true) }
            try {
                val (items, total, fetched) = fetchPage(state.query, state.filter, offset = serverOffset)
                serverOffset += fetched
                serverTotal = total
                _uiState.update {
                    val merged = (it.items + items).distinctBy { item -> idOf(item) }
                    it.copy(
                        items = merged,
                        loadingMore = false,
                        hasMore = serverOffset < serverTotal && fetched > 0,
                    )
                }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loadingMore = false, error = e.message) }
            }
        }
    }

    /** Returns (filteredItems, serverTotal, fetchedCount). */
    private suspend fun fetchPage(
        query: String,
        filter: ExploreFilter,
        offset: Int,
    ): Triple<List<Any>, Int, Int> {
        return when (filter.mode) {
            ExploreMode.TEAMS -> {
                val page = repo.getTeams(offset)
                val filtered = page.items.filter { it.isMarvelOrUnknown }
                Triple(filtered, page.total, page.items.size)
            }
            ExploreMode.POWERS -> {
                val page = repo.getPowers(offset)
                Triple(page.items, page.total, page.items.size)
            }
            ExploreMode.CHARACTERS -> {
                val page = repo.getCharacters(query, offset)
                val filtered = page.items
                    .filter { it.isMarvelOrUnknown }
                    .filter { c -> matchesAlignment(c, filter) }
                Triple(filtered, page.total, page.items.size)
            }
        }
    }

    private fun matchesAlignment(c: Character, filter: ExploreFilter): Boolean = when (filter) {
        ExploreFilter.HERO -> classifyAlignment(c) == Alignment.HERO
        ExploreFilter.VILLAIN -> classifyAlignment(c) == Alignment.VILLAIN
        else -> true
    }

    private fun idOf(item: Any): Int = when (item) {
        is Character -> item.id
        is com.example.nexus_marvel_app.domain.model.Team -> item.id
        is com.example.nexus_marvel_app.domain.model.Power -> item.id
        else -> item.hashCode()
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ExploreViewModel(Graph.repository) }
        }
    }
}
