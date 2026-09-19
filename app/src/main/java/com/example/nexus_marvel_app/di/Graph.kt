package com.example.nexus_marvel_app.di

import android.content.Context
import com.example.nexus_marvel_app.data.local.FavoritesStore
import com.example.nexus_marvel_app.data.repository.ComicVineRepository

/**
 * Minimal service locator (no Hilt). Initialized once from NexusApplication and
 * read by ViewModel factories.
 */
object Graph {
    lateinit var repository: ComicVineRepository
        private set
    lateinit var favoritesStore: FavoritesStore
        private set

    fun init(context: Context) {
        repository = ComicVineRepository()
        favoritesStore = FavoritesStore(context.applicationContext)
    }
}
