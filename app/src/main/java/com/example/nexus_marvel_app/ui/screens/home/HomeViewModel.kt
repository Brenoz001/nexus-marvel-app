package com.example.nexus_marvel_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.data.repository.ComicVineRepository
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** A character positioned in the constellation graph. */
data class GraphNode(
    val character: Character,
    val x: Float,
    val y: Float,
    val team: String,
)

/** A link between two nodes (same team). */
data class GraphEdge(val from: Int, val to: Int, val team: String)

data class HomeGraph(
    val nodes: List<GraphNode> = emptyList(),
    val edges: List<GraphEdge> = emptyList(),
    val width: Float = SPACE,
    val height: Float = SPACE,
) {
    companion object {
        const val SPACE = 1100f
    }
}

data class HomeUiState(
    val loading: Boolean = true,
    val graph: HomeGraph = HomeGraph(),
    val error: String? = null,
)

class HomeViewModel(private val repo: ComicVineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                // Curated iconic Marvel characters (the default list is obscure).
                val featured = repo.getFeaturedCharacters()
                val chars = if (featured.isNotEmpty()) {
                    featured.take(30)
                } else {
                    val page = repo.getCharacters(query = "", offset = 0, limit = 40)
                    val marvel = page.items.filter { it.isMarvelOrUnknown }
                    (if (marvel.isEmpty()) page.items else marvel).take(30)
                }
                _uiState.update { it.copy(loading = false, graph = buildGraph(chars), error = null) }
            } catch (e: ComicVineException) {
                _uiState.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    /** Groups characters by their primary team into radial "constellations". */
    private fun buildGraph(chars: List<Character>): HomeGraph {
        if (chars.isEmpty()) return HomeGraph(nodes = emptyList(), edges = emptyList())

        val center = HomeGraph.SPACE / 2f
        val groups: Map<String, List<Character>> = chars.groupBy { c ->
            c.teams.firstOrNull()?.name ?: "Independentes"
        }

        val nodes = mutableListOf<GraphNode>()
        val edges = mutableListOf<GraphEdge>()

        val teamNames = groups.keys.toList()
        val teamCount = teamNames.size
        val clusterOrbit = HomeGraph.SPACE * 0.34f

        teamNames.forEachIndexed { teamIndex, team ->
            val members = groups.getValue(team)
            // Cluster center placed around a big circle (single team sits at the middle).
            val clusterAngle = if (teamCount <= 1) 0.0 else 2.0 * Math.PI * teamIndex / teamCount
            val clusterCx = if (teamCount <= 1) center else center + clusterOrbit * cos(clusterAngle).toFloat()
            val clusterCy = if (teamCount <= 1) center else center + clusterOrbit * sin(clusterAngle).toFloat()

            val startIndex = nodes.size
            val innerRadius = max(70f, 26f * members.size)
            members.forEachIndexed { j, character ->
                val a = 2.0 * Math.PI * j / max(1, members.size)
                val x = if (members.size == 1) clusterCx else clusterCx + innerRadius * cos(a).toFloat()
                val y = if (members.size == 1) clusterCy else clusterCy + innerRadius * sin(a).toFloat()
                nodes.add(GraphNode(character, x, y, team))
            }

            // Ring edges connecting members of the same team.
            val count = members.size
            if (count >= 2) {
                for (j in 0 until count) {
                    val from = startIndex + j
                    val to = startIndex + (j + 1) % count
                    if (from != to) edges.add(GraphEdge(from, to, team))
                }
            }
        }

        return HomeGraph(nodes = nodes, edges = edges)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { HomeViewModel(Graph.repository) }
        }
    }
}
