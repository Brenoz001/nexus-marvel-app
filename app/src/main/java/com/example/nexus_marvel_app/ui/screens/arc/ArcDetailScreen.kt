package com.example.nexus_marvel_app.ui.screens.arc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexus_marvel_app.ui.components.ComingSoon
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing

@Composable
fun ArcDetailScreen(arcId: Int, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        ComingSoon(
            title = "Arco",
            description = "O detalhe do arco (#$arcId) — personagens e edições — chega na etapa 5.",
            icon = Icons.Outlined.Book,
            accent = NexusColors.Gold,
            modifier = Modifier.fillMaxSize(),
        )
        NexusBackButton(
            onClick = onBack,
            modifier = Modifier.statusBarsPadding().padding(Spacing.md),
        )
    }
}
