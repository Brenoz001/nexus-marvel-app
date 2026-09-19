package com.example.nexus_marvel_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.ui.theme.BebasNeue
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing

@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "Algo deu errado",
    onRetry: (() -> Unit)? = null,
) {
    CenteredState(modifier) {
        IconBubble(Icons.Default.ErrorOutline, NexusColors.RedLight, NexusColors.Red)
        StateTitle(title)
        StateMessage(message)
        if (onRetry != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier
                    .padding(top = Spacing.md)
                    .clip(RoundedCornerShape(Radius.full))
                    .background(NexusColors.Red)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.sm + 2.dp),
            ) {
                Icon(Icons.Default.Refresh, null, tint = NexusColors.White, modifier = Modifier.size(16.dp))
                Text("TENTAR NOVAMENTE", color = NexusColors.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String = "Nada por aqui",
    message: String = "Tente ajustar a busca ou os filtros.",
    icon: ImageVector = Icons.Outlined.Public,
    modifier: Modifier = Modifier,
) {
    CenteredState(modifier) {
        IconBubble(icon, NexusColors.TextSecondary, NexusColors.White)
        StateTitle(title)
        StateMessage(message)
    }
}

@Composable
fun ComingSoon(
    title: String,
    description: String,
    icon: ImageVector,
    accent: Color = NexusColors.Red,
    modifier: Modifier = Modifier,
) {
    CenteredState(modifier) {
        IconBubble(icon, accent, accent, big = true)
        Text(
            title.uppercase(),
            fontFamily = BebasNeue,
            fontSize = 40.sp,
            letterSpacing = 1.5.sp,
            color = NexusColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        StateMessage(description)
        Box(
            modifier = Modifier
                .padding(top = Spacing.md)
                .clip(RoundedCornerShape(Radius.full))
                .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(Radius.full))
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            Text("EM BREVE", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
        }
    }
}

@Composable
private fun CenteredState(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) { content() }
}

@Composable
private fun IconBubble(icon: ImageVector, tint: Color, ring: Color, big: Boolean = false) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(bottom = Spacing.md)
            .size(if (big) 104.dp else 88.dp)
            .clip(CircleShape)
            .background(ring.copy(alpha = 0.12f)),
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(if (big) 48.dp else 40.dp))
    }
}

@Composable
private fun StateTitle(title: String) {
    Text(
        title.uppercase(),
        fontFamily = BebasNeue,
        fontSize = 28.sp,
        letterSpacing = 1.sp,
        color = NexusColors.TextPrimary,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun StateMessage(message: String) {
    Text(
        message,
        color = NexusColors.TextSecondary,
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = Spacing.sm)
            .widthIn(max = 300.dp),
    )
}
