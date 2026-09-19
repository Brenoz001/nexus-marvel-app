package com.example.nexus_marvel_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius

@Composable
fun NexusBadge(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = NexusColors.Red,
    small: Boolean = false,
) {
    Text(
        text = label.uppercase(),
        color = color,
        fontSize = if (small) 9.sp else 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(Radius.full))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(Radius.full))
            .padding(horizontal = if (small) 8.dp else 10.dp, vertical = if (small) 2.dp else 4.dp),
    )
}
