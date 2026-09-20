package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.ui.navigation.Routes
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing

private data class LabFeature(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String,
)

@Composable
fun LabScreen(contentPadding: PaddingValues, onOpen: (String) -> Unit) {
    val features = listOf(
        LabFeature("Constelações", "O grafo de heróis conectados por time.", Icons.Filled.Hub, NexusColors.Red, Routes.LAB_CONSTELLATION),
        LabFeature("Confronto", "Poder vs poder num radar sobreposto.", Icons.Filled.Bolt, NexusColors.Red, Routes.LAB_CONFRONTO),
        LabFeature("Multiverso", "Encontre todas as variantes de um herói.", Icons.Filled.BlurOn, NexusColors.Info, Routes.LAB_MULTIVERSO),
        LabFeature("Sentido Aranha", "Chacoalhe para detectar uma ameaça.", Icons.Filled.Vibration, NexusColors.Gold, Routes.LAB_SPIDER),
        LabFeature("Efeito Thanos", "Estale os dedos. Metade se desfaz.", Icons.Filled.BackHand, NexusColors.Success, Routes.LAB_SNAP),
        LabFeature("Trilha Sonora", "As músicas mais marcantes dos filmes.", Icons.Filled.QueueMusic, NexusColors.Gold, Routes.LAB_JUKEBOX),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md),
    ) {
        Column(modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.md)) {
            Text("LABORATÓRIO", fontFamily = BebasNeue, fontSize = 32.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary)
            Text("Experimentos com o universo Marvel.", color = NexusColors.TextSecondary, fontSize = 14.sp)
        }

        features.forEach { f ->
            LabCard(f, onClick = { onOpen(f.route) })
            Box(modifier = Modifier.size(Spacing.md))
        }

        Box(modifier = Modifier.size(contentPadding.calculateBottomPadding() + Spacing.xl))
    }
}

@Composable
private fun LabCard(feature: LabFeature, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.Surface)
            .border(1.dp, feature.accent.copy(alpha = 0.35f), RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(Radius.md))
                .background(feature.accent.copy(alpha = 0.16f)),
        ) {
            Icon(feature.icon, contentDescription = null, tint = feature.accent, modifier = Modifier.size(28.dp))
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = Spacing.md)) {
            Text(feature.title.uppercase(), fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 0.5.sp, color = NexusColors.TextPrimary)
            Text(feature.description, color = NexusColors.TextSecondary, fontSize = 13.sp)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = NexusColors.TextMuted)
    }
}
