package com.example.nexus_marvel_app.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.domain.model.Power
import com.example.nexus_marvel_app.ui.theme.JetBrainsMono
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing

@Composable
fun PowerCard(power: Power, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(NexusColors.Surface)
            .border(1.dp, NexusColors.Border, RoundedCornerShape(Radius.md))
            .padding(Spacing.md),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Radius.md))
                .background(NexusColors.Gold.copy(alpha = 0.14f)),
        ) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = NexusColors.Gold, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = power.name,
                color = NexusColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            power.characterCount?.let {
                Text(
                    text = "$it+ personagens",
                    color = NexusColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = JetBrainsMono,
                )
            }
        }
    }
}
