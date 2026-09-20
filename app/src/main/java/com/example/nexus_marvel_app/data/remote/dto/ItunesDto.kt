package com.example.nexus_marvel_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/** iTunes Search API track (only the fields we use). */
data class ItunesTrack(
    @SerializedName("trackName") val trackName: String? = null,
    @SerializedName("artistName") val artistName: String? = null,
    @SerializedName("previewUrl") val previewUrl: String? = null,
    @SerializedName("artworkUrl100") val artworkUrl: String? = null,
)

data class ItunesResponse(
    @SerializedName("resultCount") val resultCount: Int = 0,
    @SerializedName("results") val results: List<ItunesTrack>? = null,
)
