package com.example.nexus_marvel_app.ui.favorites

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.local.FavoritesStore
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.FavoriteItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared, app-wide favorites state backed by DataStore. */
class FavoritesViewModel(private val store: FavoritesStore) : ViewModel() {

    val favorites: StateFlow<List<FavoriteItem>> = store.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteIds: StateFlow<Set<Int>> = store.favorites
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggle(item: FavoriteItem) {
        viewModelScope.launch { store.toggle(item) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { FavoritesViewModel(Graph.favoritesStore) }
        }
    }
}

/** Access the shared FavoritesViewModel from any composable. */
val LocalFavorites = staticCompositionLocalOf<FavoritesViewModel> {
    error("FavoritesViewModel not provided")
}
