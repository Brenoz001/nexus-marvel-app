package com.example.nexus_marvel_app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * "Forje seu Herói" — a fully offline hero-identity builder.
 * Everything here is curated so it works with no network and never fails on stage.
 */

data class ForgeOrigin(val id: String, val label: String, val icon: ImageVector, val blurb: String, val bonus: Int)
data class ForgePower(val id: String, val label: String, val icon: ImageVector, val tier: Int)
data class ForgeWeakness(val id: String, val label: String, val icon: ImageVector, val blurb: String)
data class ForgeEmblem(val id: String, val icon: ImageVector)

val FORGE_ORIGINS: List<ForgeOrigin> = listOf(
    ForgeOrigin("mutant", "Mutante", Icons.Filled.Biotech, "nasceu com o gene-X pulsando nas veias", 10),
    ForgeOrigin("cosmic", "Cósmico", Icons.Filled.AutoAwesome, "foi banhado por energia cósmica pura", 16),
    ForgeOrigin("tech", "Tecnológico", Icons.Filled.Memory, "forjou o próprio poder com tecnologia de ponta", 8),
    ForgeOrigin("mystic", "Místico", Icons.Filled.AutoFixHigh, "domina as artes místicas mais antigas", 12),
    ForgeOrigin("experiment", "Experimento", Icons.Filled.Science, "sobreviveu a um experimento secreto", 10),
    ForgeOrigin("alien", "Alienígena", Icons.Filled.Rocket, "chegou à Terra de um mundo distante", 14),
    ForgeOrigin("cursed", "Amaldiçoado", Icons.Filled.DarkMode, "carrega uma maldição que virou poder", 12),
)

val FORGE_POWERS: List<ForgePower> = listOf(
    ForgePower("strength", "Super Força", Icons.Filled.FitnessCenter, 3),
    ForgePower("speed", "Super Velocidade", Icons.Filled.Speed, 2),
    ForgePower("flight", "Voo", Icons.Filled.Flight, 2),
    ForgePower("telepathy", "Telepatia", Icons.Filled.Psychology, 3),
    ForgePower("healing", "Fator de Cura", Icons.Filled.Healing, 2),
    ForgePower("energy", "Rajadas de Energia", Icons.Filled.FlashOn, 2),
    ForgePower("invisibility", "Invisibilidade", Icons.Filled.VisibilityOff, 1),
    ForgePower("time", "Controle do Tempo", Icons.Filled.Schedule, 3),
    ForgePower("magic", "Magia", Icons.Filled.AutoFixHigh, 3),
    ForgePower("fire", "Pirocinese", Icons.Filled.LocalFireDepartment, 2),
    ForgePower("ice", "Criocinese", Icons.Filled.AcUnit, 2),
    ForgePower("electric", "Eletrocinese", Icons.Filled.ElectricBolt, 2),
    ForgePower("weather", "Controle do Clima", Icons.Filled.Air, 3),
    ForgePower("durability", "Invulnerabilidade", Icons.Filled.Shield, 2),
    ForgePower("telekinesis", "Telecinese", Icons.Filled.Vibration, 3),
    ForgePower("cosmic", "Poder Cósmico", Icons.Filled.Star, 3),
)

val FORGE_WEAKNESSES: List<ForgeWeakness> = listOf(
    ForgeWeakness("arrogance", "Arrogância", Icons.Filled.Whatshot, "a arrogância pode ser sua ruína"),
    ForgeWeakness("past", "O passado", Icons.Filled.Schedule, "o passado ainda o assombra"),
    ForgeWeakness("substance", "Substância rara", Icons.Filled.Science, "uma substância rara anula seus poderes"),
    ForgeWeakness("love", "Proteger quem ama", Icons.Filled.Favorite, "faria de tudo para proteger quem ama"),
    ForgeWeakness("control", "Perder o controle", Icons.Filled.Bolt, "corre o risco de perder o controle"),
)

val FORGE_EMBLEMS: List<ForgeEmblem> = listOf(
    ForgeEmblem("star", Icons.Filled.Star),
    ForgeEmblem("bolt", Icons.Filled.Bolt),
    ForgeEmblem("shield", Icons.Filled.Shield),
    ForgeEmblem("fire", Icons.Filled.Whatshot),
    ForgeEmblem("ice", Icons.Filled.AcUnit),
    ForgeEmblem("flash", Icons.Filled.FlashOn),
    ForgeEmblem("heart", Icons.Filled.Favorite),
    ForgeEmblem("dark", Icons.Filled.DarkMode),
    ForgeEmblem("world", Icons.Filled.Public),
    ForgeEmblem("spark", Icons.Filled.AutoAwesome),
    ForgeEmblem("diamond", Icons.Filled.Diamond),
    ForgeEmblem("guard", Icons.Filled.GppGood),
)

val FORGE_ACCENTS: List<Color> = listOf(
    Color(0xFFE9B949), // gold
    Color(0xFFE23636), // red
    Color(0xFF5B8DEF), // blue
    Color(0xFF46A46B), // green
    Color(0xFF9B59B6), // purple
    Color(0xFF1ABC9C), // teal
    Color(0xFFE67E22), // orange
    Color(0xFFEC4899), // pink
    Color(0xFF4AB0D9), // cyan
)

val FORGE_TEAMS: List<String> = listOf(
    "Vingadores", "X-Men", "Guardiões da Galáxia",
    "Quarteto Fantástico", "Defensores", "Illuminati",
)

/** Random codename generator (stylized — agreement is intentionally loose). */
object HeroNameGen {
    private val first = listOf(
        "Sombra", "Tempestade", "Trovão", "Fúria", "Espectro", "Titã", "Aurora",
        "Vórtice", "Lâmina", "Fênix", "Corvo", "Quantum", "Nova", "Zênite",
        "Éter", "Íon", "Relâmpago", "Abismo", "Solaris", "Meia-Noite",
    )
    private val second = listOf(
        "Negro", "Escarlate", "Prateado", "Fantasma", "Imortal", "Selvagem",
        "Radiante", "Sombrio", "Cósmico", "Eterno", "Carmesim", "Ancestral",
        "de Ferro", "da Noite", "Sem Nome",
    )

    fun random(): String = "${first.random()} ${second.random()}"
}

/** Immutable snapshot of a forged hero. */
data class ForgedHero(
    val codename: String,
    val origin: ForgeOrigin,
    val powers: List<ForgePower>,
    val team: String,
    val weakness: ForgeWeakness,
    val emblem: ForgeEmblem,
    val accent: Color,
) {
    /** 0..100 power level from picked powers + origin bonus. */
    val powerLevel: Int
        get() = (powers.sumOf { it.tier } * 8 + origin.bonus).coerceIn(0, 100)

    val classification: String
        get() = when {
            powerLevel >= 85 -> "ÔMEGA"
            powerLevel >= 65 -> "ALFA"
            powerLevel >= 45 -> "BETA"
            else -> "GAMA"
        }

    /** One-line auto-generated origin story. */
    val story: String
        get() {
            val primary = powers.firstOrNull()?.label?.lowercase() ?: "um poder desconhecido"
            return "$codename ${origin.blurb}, despertando o poder de $primary. " +
                "Ao lado dos $team, tornou-se uma lenda — mas ${weakness.blurb}."
        }
}
