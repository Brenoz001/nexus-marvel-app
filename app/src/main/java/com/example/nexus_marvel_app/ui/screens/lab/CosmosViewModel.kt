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

data class CosmosUiState(
    val loading: Boolean = true,
    val characters: List<Character> = emptyList(),
    val error: String? = null,
)

class CosmosViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CosmosUiState())
    val uiState = _uiState.asStateFlow()

    private var loadedKey: String? = null

    /** Load only the characters that belong to a given world (by name). */
    fun loadFor(names: List<String>) {
        val key = names.sorted().joinToString(",")
        if (key == loadedKey && _uiState.value.characters.isNotEmpty()) return
        loadedKey = key
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val chars = repo.getCharactersByNames(names)
                _uiState.update { it.copy(loading = false, characters = chars, error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    /** Characters that belong to a planet (matched by name). */
    fun charactersFor(names: List<String>): List<Character> {
        val chars = _uiState.value.characters
        return names.mapNotNull { wanted ->
            chars.firstOrNull { it.name.equals(wanted, ignoreCase = true) || it.name.contains(wanted, ignoreCase = true) }
        }.distinctBy { it.id }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { CosmosViewModel(Graph.repository) }
        }
    }
}
