package com.example.nexus_marvel_app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * NEXUS palette — cinematic / editorial. Single source of truth for colors.
 * Think HBO Max meets a S.H.I.E.L.D. dashboard.
 */
object NexusColors {
    // Base
    val Background = Color(0xFF08080F) // near-black, blue tint
    val Surface = Color(0xFF12121F) // cards, containers
    val SurfaceLight = Color(0xFF1C1C2E) // elevation 2
    val SurfaceHover = Color(0xFF252540) // elevation 3 / hover

    // Brand
    val Red = Color(0xFFC0101A)
    val RedDark = Color(0xFF8C0C13)
    val RedLight = Color(0xFFFF4D5A)
    val Gold = Color(0xFFE6B800)
    val GoldDim = Color(0xFFB8930A)

    // Text
    val TextPrimary = Color(0xFFF0F0F5)
    val TextSecondary = Color(0xFF8888A0)
    val TextMuted = Color(0xFF555570)

    // Semantic
    val Success = Color(0xFF3D8B3D)
    val Warning = Color(0xFFE6B800)
    val Danger = Color(0xFFC0101A)
    val Info = Color(0xFF4A90D9)

    // Utility
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Border = Color(0x14FFFFFF) // white @ ~8%
    val BorderStrong = Color(0x29FFFFFF) // white @ ~16%
}

/** Accent color per team, used across the app. */
fun teamColor(name: String?): Color {
    if (name == null) return NexusColors.TextSecondary
    val n = name.lowercase()
    return when {
        n.contains("avengers") -> NexusColors.Red
        n.contains("x-men") || n.contains("x-force") -> NexusColors.Info
        n.contains("defenders") -> NexusColors.Success
        n.contains("guardians") -> NexusColors.Gold
        n.contains("fantastic") -> Color(0xFF4AB0D9)
        n.contains("illuminati") -> Color(0xFF9B59B6)
        n.contains("inhumans") -> Color(0xFF1ABC9C)
        n.contains("thunderbolts") -> Color(0xFFE67E22)
        else -> NexusColors.TextSecondary
    }
}
