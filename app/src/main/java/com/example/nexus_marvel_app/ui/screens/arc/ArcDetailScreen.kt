package com.example.nexus_marvel_app.ui.screens.arc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.data.ComicVineException
import com.example.nexus_marvel_app.di.Graph
import com.example.nexus_marvel_app.domain.model.NamedRef
import com.example.nexus_marvel_app.domain.model.StoryArc
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.ErrorState
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBackButton
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.components.SkeletonBox
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.nexusBackground

@Composable
fun ArcDetailScreen(arcId: Int, onBack: () -> Unit, onCharacterClick: (Int) -> Unit) {
    var arc by remember { mutableStateOf<StoryArc?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var reload by remember { mutableStateOf(0) }

    LaunchedEffect(arcId, reload) {
        loading = true
        error = null
        try {
            arc = Graph.repository.getStoryArc(arcId)
        } catch (e: ComicVineException) {
            error = e.message
        }
        loading = false
    }

    val heroHeight = LocalConfiguration.current.screenWidthDp.dp * 0.9f

    Box(modifier = Modifier.fillMaxSize().nexusBackground()) {
        if (error != null) {
            ErrorState(message = error!!, onRetry = { reload++ })
        } else {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().height(heroHeight)) {
                    if (loading) {
                        SkeletonBox(modifier = Modifier.fillMaxSize(), cornerRadius = 0.dp)
                    } else {
                        AsyncPoster(
                            url = arc?.imageOriginal ?: arc?.imageMedium,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            contentDescription = arc?.name,
                        )
                    }
                    GradientScrim(modifier = Modifier.fillMaxSize(), intensity = 0.98f)
                    arc?.let { a ->
                        Column(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                                a.appearances?.let { NexusBadge(label = "$it edições", color = NexusColors.Gold, small = true) }
                                a.firstYear?.let { Text(it, color = NexusColors.TextSecondary, fontFamily = JetBrainsMono, fontSize = 12.sp) }
                            }
                            Text(a.name.uppercase(), fontFamily = BebasNeue, fontSize = 40.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, lineHeight = 42.sp)
                        }
                    }
                }

                arc?.let { a -> Body(a, onCharacterClick) }
            }
        }

        NexusBackButton(onClick = onBack, modifier = Modifier.statusBarsPadding().padding(Spacing.md).align(Alignment.TopStart))
    }
}

@Composable
private fun Body(arc: StoryArc, onCharacterClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(Spacing.md)) {
        arc.deck?.let {
            Text("SOBRE", fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary)
            Text(it, color = NexusColors.TextSecondary, fontSize = 15.sp, lineHeight = 24.sp, modifier = Modifier.padding(top = Spacing.sm, bottom = Spacing.lg))
        }

        if (arc.characters.isNotEmpty()) {
            HorizontalDivider(color = NexusColors.Border)
            Text("PERSONAGENS NO ARCO", fontFamily = BebasNeue, fontSize = 24.sp, letterSpacing = 1.sp, color = NexusColors.TextPrimary, modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.sm))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                arc.characters.take(24).forEach { ref -> RefAvatar(ref, onClick = { onCharacterClick(ref.id) }) }
            }
        }
    }
}

@Composable
private fun RefAvatar(ref: NamedRef, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(72.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(60.dp).clip(CircleShape).background(NexusColors.SurfaceLight),
        ) {
            Text(initials(ref.name), color = NexusColors.Gold, fontFamily = BebasNeue, fontSize = 22.sp)
        }
        Text(ref.name, color = NexusColors.TextSecondary, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}
