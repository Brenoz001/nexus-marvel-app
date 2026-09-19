package com.example.nexus_marvel_app.util

import com.example.nexus_marvel_app.domain.model.NamedRef

/** The six radar categories used in Confronto / DNA de Poderes. */
enum class PowerCategory(val label: String, val short: String) {
    FORCA("Força", "Força"),
    VELOCIDADE("Velocidade", "Veloc."),
    INTELIGENCIA("Inteligência", "Intel."),
    ENERGIA("Energia", "Energia"),
    RESISTENCIA("Resistência", "Resist."),
    COMBATE("Habilidade de Combate", "Combate"),
}

const val MAX_POWER_SCORE = 10

private data class Mapping(val category: PowerCategory, val points: Int)

/** Lowercase power keyword -> category + point contribution. */
private val POWER_MAP: Map<String, Mapping> = mapOf(
    // Força
    "super strength" to Mapping(PowerCategory.FORCA, 6),
    "superhuman strength" to Mapping(PowerCategory.FORCA, 6),
    "enhanced strength" to Mapping(PowerCategory.FORCA, 5),
    "strength" to Mapping(PowerCategory.FORCA, 4),
    "size" to Mapping(PowerCategory.FORCA, 3),
    "density" to Mapping(PowerCategory.FORCA, 3),
    // Velocidade
    "super speed" to Mapping(PowerCategory.VELOCIDADE, 6),
    "enhanced speed" to Mapping(PowerCategory.VELOCIDADE, 5),
    "speed" to Mapping(PowerCategory.VELOCIDADE, 4),
    "flight" to Mapping(PowerCategory.VELOCIDADE, 5),
    "teleportation" to Mapping(PowerCategory.VELOCIDADE, 5),
    "reflexes" to Mapping(PowerCategory.VELOCIDADE, 3),
    // Inteligência
    "super intellect" to Mapping(PowerCategory.INTELIGENCIA, 6),
    "genius level intellect" to Mapping(PowerCategory.INTELIGENCIA, 6),
    "intellect" to Mapping(PowerCategory.INTELIGENCIA, 5),
    "telepathy" to Mapping(PowerCategory.INTELIGENCIA, 6),
    "telekinesis" to Mapping(PowerCategory.INTELIGENCIA, 5),
    "psychic" to Mapping(PowerCategory.INTELIGENCIA, 5),
    "mind control" to Mapping(PowerCategory.INTELIGENCIA, 5),
    // Energia
    "energy blast" to Mapping(PowerCategory.ENERGIA, 6),
    "energy absorption" to Mapping(PowerCategory.ENERGIA, 5),
    "energy manipulation" to Mapping(PowerCategory.ENERGIA, 6),
    "energy" to Mapping(PowerCategory.ENERGIA, 4),
    "magic" to Mapping(PowerCategory.ENERGIA, 6),
    "electricity" to Mapping(PowerCategory.ENERGIA, 5),
    "fire" to Mapping(PowerCategory.ENERGIA, 5),
    "ice" to Mapping(PowerCategory.ENERGIA, 5),
    "radiation" to Mapping(PowerCategory.ENERGIA, 5),
    "weather control" to Mapping(PowerCategory.ENERGIA, 5),
    // Resistência
    "invulnerability" to Mapping(PowerCategory.RESISTENCIA, 7),
    "healing factor" to Mapping(PowerCategory.RESISTENCIA, 6),
    "immortality" to Mapping(PowerCategory.RESISTENCIA, 6),
    "durability" to Mapping(PowerCategory.RESISTENCIA, 5),
    "stamina" to Mapping(PowerCategory.RESISTENCIA, 4),
    "regeneration" to Mapping(PowerCategory.RESISTENCIA, 5),
    // Habilidade de Combate
    "martial arts" to Mapping(PowerCategory.COMBATE, 6),
    "weapon master" to Mapping(PowerCategory.COMBATE, 6),
    "marksmanship" to Mapping(PowerCategory.COMBATE, 5),
    "agility" to Mapping(PowerCategory.COMBATE, 4),
    "acrobat" to Mapping(PowerCategory.COMBATE, 3),
    "stealth" to Mapping(PowerCategory.COMBATE, 3),
    "gadgets" to Mapping(PowerCategory.COMBATE, 4),
)

/**
 * Compute the six radar scores (0-10) from a character's powers.
 * Every category gets a baseline of 1 so the radar is never fully empty.
 */
fun computePowerScores(powers: List<NamedRef>): Map<PowerCategory, Int> {
    val scores = PowerCategory.entries.associateWith { 0 }.toMutableMap()
    for (power in powers) {
        val name = power.name.lowercase()
        for ((keyword, mapping) in POWER_MAP) {
            if (name.contains(keyword)) {
                val current = scores[mapping.category] ?: 0
                scores[mapping.category] = minOf(MAX_POWER_SCORE, current + mapping.points)
            }
        }
    }
    for (cat in PowerCategory.entries) {
        if ((scores[cat] ?: 0) == 0) scores[cat] = 1
    }
    return scores
}

/** Overall power rating (average of the six categories, 0-10). */
fun overallRating(scores: Map<PowerCategory, Int>): Double {
    if (scores.isEmpty()) return 0.0
    val avg = scores.values.sum().toDouble() / scores.size
    return (avg * 10).toInt() / 10.0
}
