package com.example.nexus_marvel_app.ui.screens.arcs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.data.repository.ComicVineRepository
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.StoryArc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArcsUiState(
    val loading: Boolean = true,
    val arcs: List<StoryArc> = emptyList(),
    val loadingMore: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
)

class ArcsViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ArcsUiState())
    val uiState = _uiState.asStateFlow()

    private var serverOffset = 0
    private var serverTotal = Int.MAX_VALUE

    init { load() }

    fun load() {
        serverOffset = 0
        serverTotal = Int.MAX_VALUE
        _uiState.update { it.copy(loading = true, error = null, arcs = emptyList()) }
        viewModelScope.launch {
            try {
                val page = repo.getStoryArcs(offset = 0)
                serverOffset = page.items.size
                serverTotal = page.total
                val arcs = page.items.filter { it.isMarvel }
                _uiState.update {
                    it.copy(
                        loading = false,
                        arcs = arcs,
                        error = null,
                        hasMore = serverOffset < serverTotal && page.items.isNotEmpty(),
                    )
                }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasMore || state.loading || state.loadingMore) return
        _uiState.update { it.copy(loadingMore = true) }
        viewModelScope.launch {
            try {
                val page = repo.getStoryArcs(offset = serverOffset)
                serverOffset += page.items.size
                serverTotal = page.total
                val more = page.items.filter { it.isMarvel }
                _uiState.update {
                    it.copy(
                        arcs = (it.arcs + more).distinctBy { arc -> arc.id },
                        loadingMore = false,
                        hasMore = serverOffset < serverTotal && page.items.isNotEmpty(),
                    )
                }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loadingMore = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ArcsViewModel(Graph.repository) }
        }
    }
}
