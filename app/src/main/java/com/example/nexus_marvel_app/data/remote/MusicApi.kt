package com.example.nexus_marvel_app.data.remote

import com.example.nexus_marvel_app.data.remote.dto.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * iTunes Search API — free, no auth, returns 30-second song previews we can
 * stream in-app. Docs: https://performance-partners.apple.com/search-api
 */
interface MusicApi {
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 1,
    ): ItunesResponse

    companion object {
        const val BASE_URL = "https://itunes.apple.com/"
    }
}
