package com.example.nexus_marvel_app.util

import com.example.nexus_marvel_app.domain.model.Alignment
import com.example.nexus_marvel_app.domain.model.Character

private val KNOWN_VILLAINS = setOf(
    "thanos", "loki", "magneto", "venom", "green goblin", "doctor doom", "doctor octopus",
    "red skull", "ultron", "kingpin", "carnage", "kang", "galactus", "mystique",
    "sabretooth", "juggernaut", "apocalypse", "dormammu", "hela", "mysterio",
    "vulture", "rhino", "sandman",
)

/**
 * Rough hero/villain classification, combining a curated villain list with the
 * ratio of enemies to friends (the API can't be fully trusted).
 */
fun classifyAlignment(character: Character): Alignment {
    val name = character.name.lowercase()
    if (KNOWN_VILLAINS.any { name == it || name.contains(it) }) return Alignment.VILLAIN

    val friends = character.friends.size
    val enemies = character.enemies.size
    return if (enemies > 0 && enemies > friends * 1.5) Alignment.VILLAIN else Alignment.HERO
}
