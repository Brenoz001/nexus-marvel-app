package com.example.nexus_marvel_app.ui.screens.explore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.domain.model.FavoriteItem
import com.example.nexus_marvel_app.ui.components.AsyncPoster
import com.example.nexus_marvel_app.ui.components.GradientScrim
import com.example.nexus_marvel_app.ui.components.NexusBadge
import com.example.nexus_marvel_app.ui.favorites.LocalFavorites
import com.example.nexus_marvel_app.ui.theme.NexusColors
import com.example.nexus_marvel_app.ui.theme.Radius
import com.example.nexus_marvel_app.ui.theme.Spacing
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CharacterCard(
    character: Character,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val favorites = LocalFavorites.current
    val favoriteIds by favorites.favoriteIds.collectAsStateWithLifecycle()
    val favorite = character.id in favoriteIds

    Box(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(Radius.lg))
            .background(NexusColors.SurfaceLight)
            .border(1.dp, NexusColors.Border, RoundedCornerShape(Radius.lg))
            .clickable { onClick(character.id) },
    ) {
        AsyncPoster(url = character.imageMedium, modifier = Modifier.fillMaxSize(), contentDescription = character.name)
        GradientScrim(modifier = Modifier.fillMaxSize(), intensity = 0.9f)

        HeartButton(
            favorite = favorite,
            onClick = {
                favorites.toggle(
                    FavoriteItem(
                        id = character.id,
                        name = character.name,
                        type = "character",
                        imageUrl = character.imageMedium,
                        publisher = character.publisherName,
                    )
                )
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(Spacing.sm),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (character.isMarvel) NexusBadge(label = "Marvel", small = true)
            Text(
                text = character.name,
                color = NexusColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            character.appearances?.let {
                Text(
                    text = "${NumberFormat.getInstance(Locale("pt", "BR")).format(it)} aparições",
                    color = NexusColors.TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = com.example.nexus_marvel_app.ui.theme.JetBrainsMono,
                )
            }
        }
    }
}

@Composable
private fun HeartButton(favorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(favorite) {
        if (favorite) {
            scale.animateTo(1.4f, tween(120))
            scale.animateTo(1f, tween(120))
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(NexusColors.Black.copy(alpha = 0.4f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Icon(
            imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Favoritar",
            tint = if (favorite) NexusColors.RedLight else NexusColors.White,
            modifier = Modifier.size(20.dp).scale(scale.value),
        )
    }
}
