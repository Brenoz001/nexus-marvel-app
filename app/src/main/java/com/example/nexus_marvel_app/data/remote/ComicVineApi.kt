package com.example.nexus_marvel_app.data.remote

import com.example.nexus_marvel_app.data.remote.dto.CharacterDto
import com.example.nexus_marvel_app.data.remote.dto.DetailResponse
import com.example.nexus_marvel_app.data.remote.dto.ListResponse
import com.example.nexus_marvel_app.data.remote.dto.PowerDto
import com.example.nexus_marvel_app.data.remote.dto.StoryArcDto
import com.example.nexus_marvel_app.data.remote.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Comic Vine endpoints. `api_key`, `format=json` and the User-Agent header are
 * injected by an OkHttp interceptor (see ComicVineClient), so they are omitted here.
 *
 * Docs: https://comicvine.gamespot.com/api/documentation
 */
interface ComicVineApi {

    @GET("characters/")
    suspend fun getCharacters(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String? = null,
        @Query("filter") filter: String? = null,
        @Query("field_list") fieldList: String? = null,
    ): ListResponse<CharacterDto>

    @GET("character/{id}/")
    suspend fun getCharacter(
        @Path("id") id: String, // e.g. "4005-1443"
        @Query("field_list") fieldList: String? = null,
    ): DetailResponse<CharacterDto>

    @GET("teams/")
    suspend fun getTeams(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String? = null,
        @Query("filter") filter: String? = null,
        @Query("field_list") fieldList: String? = null,
    ): ListResponse<TeamDto>

    @GET("team/{id}/")
    suspend fun getTeam(
        @Path("id") id: String,
        @Query("field_list") fieldList: String? = null,
    ): DetailResponse<TeamDto>

    @GET("story_arcs/")
    suspend fun getStoryArcs(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("sort") sort: String? = null,
        @Query("filter") filter: String? = null,
        @Query("field_list") fieldList: String? = null,
    ): ListResponse<StoryArcDto>

    @GET("story_arc/{id}/")
    suspend fun getStoryArc(
        @Path("id") id: String,
        @Query("field_list") fieldList: String? = null,
    ): DetailResponse<StoryArcDto>

    @GET("powers/")
    suspend fun getPowers(
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Query("field_list") fieldList: String? = null,
    ): ListResponse<PowerDto>

    companion object {
        const val BASE_URL = "https://comicvine.gamespot.com/api/"

        // Resource id prefixes used to build detail URLs.
        const val PREFIX_CHARACTER = "4005"
        const val PREFIX_TEAM = "4060"
        const val PREFIX_STORY_ARC = "4045"
    }
}
