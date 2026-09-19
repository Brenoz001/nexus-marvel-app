package com.example.nexus_marvel_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing

@Composable
fun NexusFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NexusColors.Gold,
) {
    val bg = if (selected) color.copy(alpha = 0.18f) else NexusColors.Surface.copy(alpha = 0.6f)
    val borderColor = if (selected) color.copy(alpha = 0.55f) else NexusColors.Border
    Text(
        text = label,
        color = if (selected) NexusColors.TextPrimary else NexusColors.TextSecondary,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        fontSize = 13.sp,
        letterSpacing = 0.2.sp,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(Radius.full))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    )
}
