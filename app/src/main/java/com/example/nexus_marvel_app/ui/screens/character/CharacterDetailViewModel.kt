package com.example.nexus_marvel_app.ui.screens.character

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

data class CharacterDetailUiState(
    val loading: Boolean = true,
    val character: Character? = null,
    val error: String? = null,
)

class CharacterDetailViewModel(
    private val repo: ComicVineRepository,
    private val characterId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val character = repo.getCharacter(characterId)
                _uiState.update { it.copy(loading = false, character = character, error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    companion object {
        fun factory(characterId: Int) = viewModelFactory {
            initializer { CharacterDetailViewModel(Graph.repository, characterId) }
        }
    }
}
