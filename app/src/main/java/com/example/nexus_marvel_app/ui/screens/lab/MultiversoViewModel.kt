package com.example.nexus_marvel_app.ui.screens.lab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.data.repository.ComicVineRepository
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.Character
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MultiversoUiState(
    val query: String = "",
    val loading: Boolean = false,
    val variants: List<Character> = emptyList(),
    val error: String? = null,
    val searched: Boolean = false,
)

@OptIn(FlowPreview::class)
class MultiversoViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(MultiversoUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _query.debounce { if (it.isBlank()) 0L else 450L }
                .distinctUntilChanged()
                .collect { q -> if (q.isNotBlank()) search(q) else reset() }
        }
    }

    fun setQuery(q: String) {
        _query.value = q
        _uiState.update { it.copy(query = q) }
    }

    private fun reset() {
        _uiState.update { it.copy(loading = false, variants = emptyList(), error = null, searched = false) }
    }

    private fun search(query: String) {
        _uiState.update { it.copy(loading = true, error = null, searched = true) }
        viewModelScope.launch {
            try {
                val page = repo.getCharacters(query = query, offset = 0, limit = 40)
                val variants = page.items.filter { it.isMarvelOrUnknown }
                _uiState.update { it.copy(loading = false, variants = variants, error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { MultiversoViewModel(Graph.repository) }
        }
    }
}
