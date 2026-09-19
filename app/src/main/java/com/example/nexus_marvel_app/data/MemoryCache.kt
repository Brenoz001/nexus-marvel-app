package com.example.nexus_marvel_app.data

import java.util.concurrent.ConcurrentHashMap

/** Thread-safe in-memory cache with per-entry TTL (default 5 minutes). */
class MemoryCache(private val defaultTtlMs: Long = 5 * 60 * 1000) {

    private data class Entry(val value: Any, val expiresAt: Long)

    private val store = ConcurrentHashMap<String, Entry>()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? {
        val entry = store[key] ?: return null
        if (System.currentTimeMillis() > entry.expiresAt) {
            store.remove(key)
            return null
        }
        return entry.value as? T
    }

    fun put(key: String, value: Any, ttlMs: Long = defaultTtlMs) {
        store[key] = Entry(value, System.currentTimeMillis() + ttlMs)
    }

    fun clear() = store.clear()
}
