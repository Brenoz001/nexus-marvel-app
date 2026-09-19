package com.example.nexus_marvel_app.domain.model

data class FavoriteItem(
    val id: Int,
    val name: String,
    val type: String, // "character" | "team" | "arc"
    val imageUrl: String? = null,
    val publisher: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
)
