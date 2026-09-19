package com.example.nexus_marvel_app.ui.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexus_marvel_app.ui.components.ComingSoon
import com.example.nexus_marvel_app.ui.theme.NexusColors

@Composable
fun HomeScreen(contentPadding: PaddingValues) {
    ComingSoon(
        title = "Nexus",
        description = "O grafo de constelações — heróis conectados por seus times — chega na próxima etapa.",
        icon = Icons.Outlined.Hub,
        accent = NexusColors.Red,
        modifier = Modifier.fillMaxSize().padding(contentPadding),
    )
}
