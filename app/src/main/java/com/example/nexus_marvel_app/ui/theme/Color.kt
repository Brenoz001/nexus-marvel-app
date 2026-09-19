package com.example.nexus_marvel_app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * NEXUS palette — "Dossiê Estelar".
 * Deep star-map navy with warm gold as the primary light; red is reserved as
 * the Marvel signifier (badges), not UI chrome. Warm bone text reads editorial.
 */
object NexusColors {
    // Base — deep blue-ink, layered for depth
    val Background = Color(0xFF0A0E1A)
    val BackgroundDeep = Color(0xFF06090F)
    val Surface = Color(0xFF10192B)
    val SurfaceLight = Color(0xFF18233A)
    val SurfaceHover = Color(0xFF22304C)

    // Primary accent — starlight gold
    val Gold = Color(0xFFE9B949)
    val GoldDim = Color(0xFFB98B2E)
    val GoldSoft = Color(0xFFF3D488)

    // Marvel signifier — vivid red (badges / brand only)
    val Red = Color(0xFFE23636)
    val RedDark = Color(0xFFA11E1E)
    val RedLight = Color(0xFFFF5C64)

    // Text — warm bone
    val TextPrimary = Color(0xFFECE8DF)
    val TextSecondary = Color(0xFF8B94AC)
    val TextMuted = Color(0xFF565F79)

    // Semantic
    val Success = Color(0xFF46A46B)
    val Warning = Color(0xFFE9B949)
    val Danger = Color(0xFFE23636)
    val Info = Color(0xFF5B8DEF)

    // Utility
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)
    val Border = Color(0x14FFFFFF) // white @ ~8%
    val BorderStrong = Color(0x29FFFFFF) // white @ ~16%
    val GoldBorder = Color(0x33E9B949) // gold @ ~20%
}

/** Accent color per team, used across the app. */
fun teamColor(name: String?): Color {
    if (name == null) return NexusColors.Gold
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
        else -> NexusColors.GoldSoft
    }
}
