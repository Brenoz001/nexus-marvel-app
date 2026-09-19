package com.example.nexus_marvel_app.ui.screens.lab

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.util.Soundtrack
import com.example.nexus_marvel_app.util.Soundtracks
import com.example.nexus_marvel_app.util.openUrl
import com.example.nexus_marvel_app.util.youtubeSearchUrl

@Composable
fun JukeboxScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    LabScaffold(title = "Trilha Sonora", onBack = onBack) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Equalizer()
                Text(
                    "As músicas mais marcantes de cada filme. Toque para ouvir.",
                    color = NexusColors.TextSecondary,
                    fontSize = 13.sp,
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(Soundtracks.all, key = { it.first }) { (hero, track) ->
                    TrackRow(hero, track, onPlay = { openUrl(context, youtubeSearchUrl(track.track, track.artist)) })
                }
            }
        }
    }
}

@Composable
private fun TrackRow(hero: String, track: Soundtrack, onPlay: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(NexusColors.Surface)
            .clickable(onClick = onPlay)
            .padding(Spacing.md),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(48.dp).clip(CircleShape).background(NexusColors.Gold.copy(alpha = 0.16f)),
        ) {
            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = NexusColors.Gold, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(track.track, color = NexusColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = NexusColors.TextSecondary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${hero} • ${track.movie}", color = NexusColors.TextMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp).clip(CircleShape).background(NexusColors.Gold),
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Ouvir", tint = NexusColors.Black, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun Equalizer(bars: Int = 5) {
    val transition = rememberInfiniteTransition(label = "eq")
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.height(28.dp)) {
        repeat(bars) { i ->
            val h by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(360 + i * 90), RepeatMode.Reverse),
                label = "bar$i",
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((6f + h * 20f).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NexusColors.Gold),
            )
        }
    }
}
