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

data class ThanosUiState(
    val loading: Boolean = true,
    val pool: List<Character> = emptyList(),
    val snappedIds: Set<Int> = emptySet(),
    val snapped: Boolean = false,
    val error: String? = null,
)

class ThanosViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ThanosUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val featured = repo.getFeaturedCharacters()
                val pool = if (featured.isNotEmpty()) {
                    featured.take(20)
                } else {
                    val page = repo.getCharacters(query = "", offset = 0, limit = 30)
                    val f = page.items.filter { it.isMarvelOrUnknown }
                    (if (f.isEmpty()) page.items else f).take(20)
                }
                _uiState.update { it.copy(loading = false, pool = pool, snappedIds = emptySet(), snapped = false, error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun snap() {
        val pool = _uiState.value.pool
        if (pool.isEmpty()) return
        val victims = pool.shuffled().take(pool.size / 2).map { it.id }.toSet()
        _uiState.update { it.copy(snappedIds = victims, snapped = true) }
    }

    fun revert() = _uiState.update { it.copy(snappedIds = emptySet(), snapped = false) }

    companion object {
        val Factory = viewModelFactory {
            initializer { ThanosViewModel(Graph.repository) }
        }
    }
}
