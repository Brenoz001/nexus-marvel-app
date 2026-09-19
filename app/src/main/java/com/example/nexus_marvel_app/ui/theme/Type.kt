package com.example.nexus_marvel_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.nexus_marvel_app.R

/** Bebas Neue — large cinematic titles and section headers. */
val BebasNeue = FontFamily(Font(R.font.bebas_neue_regular, FontWeight.Normal))

/** JetBrains Mono — technical data, IDs, stats. */
val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold),
)

/** Body / headings use the clean system sans (weights via FontWeight). */
val BodyFont = FontFamily.Default

/**
 * NEXUS type scale. We map the design's named variants onto Material 3 slots so
 * components can also use MaterialTheme.typography where convenient.
 */
val NexusTypography = Typography(
    // Hero title (Bebas)
    displayLarge = TextStyle(
        fontFamily = BebasNeue, fontWeight = FontWeight.Normal,
        fontSize = 48.sp, lineHeight = 52.sp, letterSpacing = 2.sp,
    ),
    // Section title (Bebas)
    displayMedium = TextStyle(
        fontFamily = BebasNeue, fontWeight = FontWeight.Normal,
        fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = 1.5.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = BebasNeue, fontWeight = FontWeight.Normal,
        fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = 1.sp,
    ),
    // Card title
    titleLarge = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Bold,
        fontSize = 18.sp, lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    // Body
    bodyLarge = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 19.sp,
    ),
    // Caption / labels
    labelLarge = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 14.sp, letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Medium,
        fontSize = 10.sp, lineHeight = 12.sp, letterSpacing = 0.5.sp,
    ),
)

/** Convenience mono styles (not part of the Material scale). */
val MonoStat = TextStyle(
    fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold,
    fontSize = 18.sp, lineHeight = 22.sp,
)
val MonoCaption = TextStyle(
    fontFamily = JetBrainsMono, fontWeight = FontWeight.Normal,
    fontSize = 12.sp, lineHeight = 16.sp,
)
