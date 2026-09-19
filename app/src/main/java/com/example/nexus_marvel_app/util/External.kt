package com.example.nexus_marvel_app.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/** Builds a YouTube search URL for a track (so the user can actually hear it). */
fun youtubeSearchUrl(track: String, artist: String): String {
    val query = Uri.encode("$track $artist")
    return "https://www.youtube.com/results?search_query=$query"
}

/** Opens a URL in the device browser / relevant app. Safe no-op if nothing handles it. */
fun openUrl(context: Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
