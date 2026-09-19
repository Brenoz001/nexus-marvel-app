package com.example.nexus_marvel_app.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.domain.model.Team
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import com.example.nexus_marvel_app.ui.theme.teamColor

@Composable
fun TeamCard(
    team: Team,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = teamColor(team.name)
    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.SurfaceLight)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(Radius.lg))
            .clickable { onClick(team.id) },
    ) {
        AsyncPoster(url = team.imageMedium, modifier = Modifier.fillMaxSize(), contentDescription = team.name)
        GradientScrim(modifier = Modifier.fillMaxSize(), intensity = 0.92f)

        // Team accent bar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(3.dp)
                .background(accent),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            NexusBadge(label = "Time", color = accent, small = true)
            Text(
                text = team.name,
                color = NexusColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            team.memberCount?.takeIf { it > 0 }?.let {
                Text(
                    text = "$it membros",
                    color = NexusColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMono,
                )
            }
        }
    }
}
