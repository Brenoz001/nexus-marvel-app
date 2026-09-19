package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nexus_marvel_app.ui.components.ComingSoon
import com.example.nexus_marvel_app.ui.theme.NexusColors

@Composable
fun LabScreen(contentPadding: PaddingValues) {
    ComingSoon(
        title = "Laboratório",
        description = "Confronto, Multiverso, Sentido Aranha e o Efeito Thanos chegam nas próximas etapas.",
        icon = Icons.Outlined.Science,
        accent = NexusColors.Info,
        modifier = Modifier.fillMaxSize().padding(contentPadding),
    )
}
