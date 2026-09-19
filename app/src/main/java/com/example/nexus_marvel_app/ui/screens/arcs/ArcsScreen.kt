package com.example.nexus_marvel_app.ui.screens.arcs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexus_marvel_app.ui.components.ComingSoon
import com.example.nexus_marvel_app.ui.theme.NexusColors

@Composable
fun ArcsScreen(contentPadding: PaddingValues) {
    ComingSoon(
        title = "Arcos Épicos",
        description = "A linha do tempo horizontal dos grandes arcos da Marvel chega na próxima etapa.",
        icon = Icons.Outlined.Book,
        accent = NexusColors.Gold,
        modifier = Modifier.fillMaxSize().padding(contentPadding),
    )
}
