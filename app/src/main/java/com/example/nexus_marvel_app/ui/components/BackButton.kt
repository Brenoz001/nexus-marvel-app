package com.example.nexus_marvel_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nexus_marvel_app.ui.theme.NexusColors

@Composable
fun CircleIconButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(NexusColors.Black.copy(alpha = 0.4f))
            .border(1.dp, NexusColors.Border, CircleShape)
            .clickable(onClick = onClick),
    ) { icon() }
}

@Composable
fun NexusBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(onClick = onClick, contentDescription = "Voltar", modifier = modifier) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = NexusColors.TextPrimary,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun FavoriteButton(favorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(onClick = onClick, contentDescription = "Favoritar", modifier = modifier) {
        Icon(
            imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Favoritar",
            tint = if (favorite) NexusColors.RedLight else NexusColors.White,
            modifier = Modifier.size(22.dp),
        )
    }
}
