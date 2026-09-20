package com.example.nexus_marvel_app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Science
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val ARCS = "arcs"
    const val LAB = "lab"
    const val CHARACTER = "character/{id}"
    const val TEAM = "team/{id}"
    const val ARC = "arc/{id}"
    const val PLANET = "planet/{index}"

    // Laboratório
    const val LAB_CONFRONTO = "lab/confronto"
    const val LAB_MULTIVERSO = "lab/multiverso"
    const val LAB_SPIDER = "lab/spider"
    const val LAB_SNAP = "lab/snap"
    const val LAB_JUKEBOX = "lab/jukebox"
    const val LAB_CONSTELLATION = "lab/constellation"

    fun character(id: Int) = "character/$id"
    fun team(id: Int) = "team/$id"
    fun arc(id: Int) = "arc/$id"
    fun planet(index: Int) = "planet/$index"
}

data class TabItem(
    val route: String,
    val label: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
)

val TABS = listOf(
    TabItem(Routes.HOME, "Nexus", Icons.Outlined.Hub, Icons.Filled.Hub),
    TabItem(Routes.EXPLORE, "Explorar", Icons.Outlined.Explore, Icons.Filled.Explore),
    TabItem(Routes.ARCS, "Arcos", Icons.Outlined.Book, Icons.Filled.Book),
    TabItem(Routes.LAB, "Lab", Icons.Outlined.Science, Icons.Filled.Science),
)
