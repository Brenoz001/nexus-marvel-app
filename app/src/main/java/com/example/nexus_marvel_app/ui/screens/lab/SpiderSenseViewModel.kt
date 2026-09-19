package com.example.nexus_marvel_app.ui.screens.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.data.repository.ComicVineRepository
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SpiderSenseUiState(
    val loading: Boolean = true,
    val pool: List<Character> = emptyList(),
    val revealed: Character? = null,
    val error: String? = null,
)

class SpiderSenseViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SpiderSenseUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val featured = repo.getFeaturedCharacters()
                val pool = if (featured.isNotEmpty()) {
                    featured
                } else {
                    val page = repo.getCharacters(query = "", offset = 0, limit = 40)
                    val f = page.items.filter { it.isMarvelOrUnknown }
                    if (f.isEmpty()) page.items else f
                }
                _uiState.update { it.copy(loading = false, pool = pool, error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    /** Reveal a random character (different from the current one when possible). */
    fun detect() {
        val pool = _uiState.value.pool
        if (pool.isEmpty()) return
        val current = _uiState.value.revealed
        val candidates = if (pool.size > 1 && current != null) pool.filter { it.id != current.id } else pool
        _uiState.update { it.copy(revealed = candidates.random()) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { SpiderSenseViewModel(Graph.repository) }
        }
    }
}
