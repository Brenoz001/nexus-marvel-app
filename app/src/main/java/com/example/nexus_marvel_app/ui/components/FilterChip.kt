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
    color: Color = NexusColors.Red,
) {
    val bg = if (selected) color.copy(alpha = 0.2f) else NexusColors.Surface
    val borderColor = if (selected) color.copy(alpha = 0.5f) else NexusColors.Border
    Text(
        text = label.uppercase(),
        color = if (selected) NexusColors.TextPrimary else NexusColors.TextSecondary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        modifier = modifier
            .clip(RoundedCornerShape(Radius.full))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(Radius.full))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    )
}
