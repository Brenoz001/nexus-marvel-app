package com.example.nexus_marvel_app.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nexus_marvel_app.ui.theme.nexusBackground
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nexus_marvel_app.ui.favorites.FavoritesViewModel
import com.example.nexus_marvel_app.ui.favorites.LocalFavorites
import com.example.nexus_marvel_app.ui.navigation.NexusBottomBar
import com.example.nexus_marvel_app.ui.navigation.Routes
import com.example.nexus_marvel_app.ui.navigation.TABS
import com.example.nexus_marvel_app.ui.screens.arc.ArcDetailScreen
import com.example.nexus_marvel_app.ui.screens.arcs.ArcsScreen
import com.example.nexus_marvel_app.ui.screens.character.CharacterDetailScreen
import com.example.nexus_marvel_app.ui.screens.explore.ExploreScreen
import com.example.nexus_marvel_app.ui.screens.home.HomeScreen
import com.example.nexus_marvel_app.ui.screens.lab.LabScreen
import com.example.nexus_marvel_app.ui.screens.lab.ConfrontoScreen
import com.example.nexus_marvel_app.ui.screens.lab.JukeboxScreen
import com.example.nexus_marvel_app.ui.screens.lab.ConstellationScreen
import com.example.nexus_marvel_app.ui.screens.lab.MultiversoScreen
import com.example.nexus_marvel_app.ui.screens.lab.SnapScreen
import com.example.nexus_marvel_app.ui.screens.lab.SpiderSenseScreen
import com.example.nexus_marvel_app.ui.screens.team.TeamDetailScreen
import com.example.nexus_marvel_app.ui.theme.NexusColors

private val TAB_ROUTES = TABS.map { it.route }.toSet()

@Composable
fun NexusApp() {
    val navController = rememberNavController()
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in TAB_ROUTES

    CompositionLocalProvider(LocalFavorites provides favoritesViewModel) {
        Scaffold(
            modifier = Modifier.fillMaxSize().nexusBackground(),
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    Box(modifier = Modifier.navigationBarsPadding().padding(bottom = 8.dp)) {
                        NexusBottomBar(
                            currentRoute = currentRoute,
                            onTabSelected = { route ->
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(250)) },
            ) {
                composable(Routes.HOME) {
                    HomeScreen(
                        contentPadding = innerPadding,
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                    )
                }
                composable(Routes.EXPLORE) {
                    ExploreScreen(
                        contentPadding = innerPadding,
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                        onTeamClick = { navController.navigate(Routes.team(it)) },
                    )
                }
                composable(Routes.ARCS) {
                    ArcsScreen(
                        contentPadding = innerPadding,
                        onArcClick = { navController.navigate(Routes.arc(it)) },
                    )
                }
                composable(Routes.LAB) {
                    LabScreen(contentPadding = innerPadding, onOpen = { navController.navigate(it) })
                }
                composable(Routes.LAB_CONFRONTO) {
                    ConfrontoScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.LAB_MULTIVERSO) {
                    MultiversoScreen(
                        onBack = { navController.popBackStack() },
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                    )
                }
                composable(Routes.LAB_SPIDER) {
                    SpiderSenseScreen(
                        onBack = { navController.popBackStack() },
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                    )
                }
                composable(Routes.LAB_SNAP) {
                    SnapScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.LAB_JUKEBOX) {
                    JukeboxScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.LAB_CONSTELLATION) {
                    ConstellationScreen(
                        onBack = { navController.popBackStack() },
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                    )
                }

                composable(
                    route = Routes.CHARACTER,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                    enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn() },
                    exitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut() },
                ) { entry ->
                    val id = entry.arguments?.getInt("id") ?: 0
                    CharacterDetailScreen(
                        characterId = id,
                        onBack = { navController.popBackStack() },
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                        onTeamClick = { navController.navigate(Routes.team(it)) },
                    )
                }
                composable(
                    route = Routes.TEAM,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    TeamDetailScreen(
                        teamId = entry.arguments?.getInt("id") ?: 0,
                        onBack = { navController.popBackStack() },
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                    )
                }
                composable(
                    route = Routes.ARC,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    ArcDetailScreen(
                        arcId = entry.arguments?.getInt("id") ?: 0,
                        onBack = { navController.popBackStack() },
                        onCharacterClick = { navController.navigate(Routes.character(it)) },
                    )
                }
            }
        }
    }
}
