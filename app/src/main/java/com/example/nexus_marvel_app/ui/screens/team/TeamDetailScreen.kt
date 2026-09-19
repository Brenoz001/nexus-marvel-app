package com.example.nexus_marvel_app.ui.screens.team

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexus_marvel_app.ui.components.ComingSoon
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing

@Composable
fun TeamDetailScreen(teamId: Int, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        ComingSoon(
            title = "Time",
            description = "O detalhe do time (#$teamId) — membros e estatísticas — chega na etapa 6.",
            icon = Icons.Outlined.Groups,
            accent = NexusColors.Info,
            modifier = Modifier.fillMaxSize(),
        )
        NexusBackButton(
            onClick = onBack,
            modifier = Modifier.statusBarsPadding().padding(Spacing.md),
        )
    }
}
