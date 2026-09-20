package com.example.nexus_marvel_app.data.repository

import com.example.nexus_marvel_app.data.MemoryCache
import com.example.nexus_marvel_app.data.remote.MusicApi
import com.example.nexus_marvel_app.data.remote.MusicClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Resolves a 30-second preview URL for a track via the iTunes Search API. */
class MusicRepository(
    private val api: MusicApi = MusicClient.api,
    private val cache: MemoryCache = MemoryCache(),
) {
    /** Returns a playable preview URL, or null if none was found. */
    suspend fun previewUrl(track: String, artist: String): String? = withContext(Dispatchers.IO) {
        val key = "preview:$track:$artist"
        cache.get<String>(key)?.let { return@withContext it }
        val result = runCatching {
            api.search(term = "$track $artist", entity = "song", limit = 1)
                .results?.firstOrNull()?.previewUrl
        }.getOrNull()
        if (result != null) cache.put(key, result, ttlMs = 60 * 60 * 1000)
        result
    }
}
