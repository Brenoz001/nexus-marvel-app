package com.example.nexus_marvel_app.data.repository

import com.example.nexus_marvel_app.BuildConfig
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.data.MemoryCache
import com.example.nexus_marvel_app.data.mapper.toDomain
import com.example.nexus_marvel_app.data.remote.ComicVineApi
import com.example.nexus_marvel_app.data.remote.ComicVineClient
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.domain.model.Page
import com.example.nexus_marvel_app.domain.model.Power
import com.example.nexus_marvel_app.domain.model.StoryArc
import com.example.nexus_marvel_app.domain.model.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Single access point for Comic Vine data. Adds an in-memory cache (5 min TTL)
 * on top of the network client and normalizes errors to [ComicVineException].
 */
class ComicVineRepository(
    private val api: ComicVineApi = ComicVineClient.api,
    private val cache: MemoryCache = MemoryCache(),
) {

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.COMIC_VINE_API_KEY
        return key.isNotBlank() && key != "your_api_key_here"
    }

    suspend fun getCharacters(query: String, offset: Int, limit: Int = PAGE): Page<Character> =
        call("characters:$query:$offset:$limit") {
            val trimmed = query.trim()
            val response = if (trimmed.isNotEmpty()) {
                api.getCharacters(
                    limit = limit, offset = offset,
                    filter = "name:$trimmed", fieldList = CHARACTER_FIELDS,
                )
            } else {
                api.getCharacters(
                    limit = limit, offset = offset,
                    sort = "count_of_issue_appearances:desc", fieldList = CHARACTER_FIELDS,
                )
            }
            Page(response.results.orEmpty().map { it.toDomain() }, response.totalResults, offset)
        }

    suspend fun getCharacter(id: Int): Character =
        call("character:$id") {
            val response = api.getCharacter("${ComicVineApi.PREFIX_CHARACTER}-$id", CHARACTER_DETAIL_FIELDS)
            response.results?.toDomain()
                ?: throw ComicVineException("Personagem não encontrado.")
        }

    suspend fun getTeams(offset: Int, limit: Int = PAGE): Page<Team> =
        call("teams:$offset:$limit") {
            val response = api.getTeams(
                limit = limit, offset = offset,
                sort = "count_of_issue_appearances:desc", fieldList = TEAM_FIELDS,
            )
            Page(response.results.orEmpty().map { it.toDomain() }, response.totalResults, offset)
        }

    suspend fun getStoryArcs(offset: Int, limit: Int = PAGE): Page<StoryArc> =
        call("arcs:$offset:$limit") {
            val response = api.getStoryArcs(
                limit = limit, offset = offset,
                sort = "count_of_issue_appearances:desc", fieldList = ARC_FIELDS,
            )
            Page(response.results.orEmpty().map { it.toDomain() }, response.totalResults, offset)
        }

    suspend fun getPowers(offset: Int, limit: Int = 30): Page<Power> =
        call("powers:$offset:$limit") {
            val response = api.getPowers(limit = limit, offset = offset, fieldList = POWER_FIELDS)
            Page(response.results.orEmpty().map { it.toDomain() }, response.totalResults, offset)
        }

    /** Runs [block] off the main thread, with caching and normalized errors. */
    private suspend fun <T : Any> call(key: String, block: suspend () -> T): T =
        withContext(Dispatchers.IO) {
            if (!isApiKeyConfigured()) {
                throw ComicVineException(
                    "Chave da Comic Vine não configurada. Adicione COMIC_VINE_API_KEY ao local.properties."
                )
            }
            cache.get<T>(key)?.let { return@withContext it }
            try {
                val result = block()
                cache.put(key, result)
                result
            } catch (e: ComicVineException) {
                throw e
            } catch (e: HttpException) {
                throw when (e.code()) {
                    401, 403 -> ComicVineException("Acesso negado. Verifique sua API key.", e.code(), e)
                    420, 429 -> ComicVineException("Limite de requisições atingido. Tente em instantes.", e.code(), e)
                    else -> ComicVineException("Erro ${e.code()} ao consultar a Comic Vine.", e.code(), e)
                }
            } catch (e: IOException) {
                throw ComicVineException("Sem conexão com a Comic Vine. Verifique sua internet.", cause = e)
            } catch (e: Exception) {
                throw ComicVineException("Ocorreu um erro inesperado.", cause = e)
            }
        }

    companion object {
        const val PAGE = 20

        private const val CHARACTER_FIELDS =
            "id,name,real_name,deck,image,publisher,count_of_issue_appearances,powers,teams,character_friends,character_enemies,first_appeared_in_issue"
        private const val CHARACTER_DETAIL_FIELDS =
            "id,name,real_name,aliases,deck,image,publisher,powers,teams,character_friends,character_enemies,count_of_issue_appearances,first_appeared_in_issue,birth"
        private const val TEAM_FIELDS =
            "id,name,deck,image,publisher,count_of_issue_appearances,count_of_team_members,characters"
        private const val ARC_FIELDS =
            "id,name,deck,image,publisher,count_of_issue_appearances,first_appeared_in_issue"
        private const val POWER_FIELDS = "id,name,description,characters"
    }
}
