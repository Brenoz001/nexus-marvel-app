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

data class ConfrontoUiState(
    val loading: Boolean = true,
    val pool: List<Character> = emptyList(),
    val error: String? = null,
    val fighterA: Character? = null,
    val fighterB: Character? = null,
    val activeSlot: Int = 0, // 0 = A, 1 = B
)

class ConfrontoViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfrontoUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val page = repo.getCharacters(query = "", offset = 0, limit = 40)
                val f = page.items.filter { it.isMarvelOrUnknown }
                val pool = (if (f.isEmpty()) page.items else f).take(24)
                _uiState.update { it.copy(loading = false, pool = pool, error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun setActiveSlot(slot: Int) = _uiState.update { it.copy(activeSlot = slot) }

    fun pick(character: Character) {
        _uiState.update { state ->
            if (state.activeSlot == 0) {
                state.copy(fighterA = character, activeSlot = 1)
            } else {
                state.copy(fighterB = character, activeSlot = 0)
            }
        }
    }

    fun clear() = _uiState.update { it.copy(fighterA = null, fighterB = null, activeSlot = 0) }

    companion object {
        val Factory = viewModelFactory {
            initializer { ConfrontoViewModel(Graph.repository) }
        }
    }
}
