package com.example.nexus_marvel_app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nexus_marvel_app.domain.model.FavoriteItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "nexus_favorites")

/**
 * Persists the user's favorites as a single JSON array in DataStore.
 * Exposes them as a Flow and offers toggle/remove operations.
 */
class FavoritesStore(private val context: Context) {

    private val gson = Gson()
    private val key = stringPreferencesKey("favorites_json")
    private val listType = object : TypeToken<List<FavoriteItem>>() {}.type

    val favorites: Flow<List<FavoriteItem>> = context.dataStore.data.map { prefs ->
        prefs[key]?.let { json ->
            runCatching { gson.fromJson<List<FavoriteItem>>(json, listType) }.getOrNull()
        } ?: emptyList()
    }

    suspend fun toggle(item: FavoriteItem) {
        context.dataStore.edit { prefs ->
            val current = readList(prefs[key])
            val updated = if (current.any { it.id == item.id }) {
                current.filterNot { it.id == item.id }
            } else {
                listOf(item.copy(addedAt = System.currentTimeMillis())) + current
            }
            prefs[key] = gson.toJson(updated)
        }
    }

    suspend fun remove(id: Int) {
        context.dataStore.edit { prefs ->
            val updated = readList(prefs[key]).filterNot { it.id == id }
            prefs[key] = gson.toJson(updated)
        }
    }

    private fun readList(json: String?): List<FavoriteItem> =
        json?.let { runCatching { gson.fromJson<List<FavoriteItem>>(it, listType) }.getOrNull() }
            ?: emptyList()
}
