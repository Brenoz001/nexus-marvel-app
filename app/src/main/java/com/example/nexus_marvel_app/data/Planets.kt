package com.example.nexus_marvel_app.data

import androidx.compose.ui.graphics.Color

/** A Marvel realm/world and the iconic characters tied to it. */
data class Planet(
    val name: String,
    val realm: String,
    val color: Color,
    /** Character names (match the curated featured list). */
    val characters: List<String>,
)

/** The NEXUS cosmos: Marvel worlds you can travel between. */
val MARVEL_PLANETS: List<Planet> = listOf(
    Planet(
        name = "Terra",
        realm = "Sistema Sol • Sector 2814",
        color = Color(0xFF4A90D9),
        characters = listOf(
            "Spider-Man", "Iron Man", "Captain America", "Hulk", "Black Widow",
            "Doctor Strange", "Deadpool", "Daredevil", "Ghost Rider", "Vision",
            "Scarlet Witch", "Black Panther",
        ),
    ),
    Planet(
        name = "Asgard",
        realm = "Reino Eterno • Yggdrasil",
        color = Color(0xFFE9B949),
        characters = listOf("Thor", "Loki"),
    ),
    Planet(
        name = "Titã",
        realm = "Lua de Saturno",
        color = Color(0xFF9B59B6),
        characters = listOf("Thanos"),
    ),
    Planet(
        name = "Cosmos",
        realm = "Xandar • Espaço profundo",
        color = Color(0xFF1ABC9C),
        characters = listOf("Star-Lord", "Captain Marvel", "Silver Surfer"),
    ),
    Planet(
        name = "Krakoa",
        realm = "Nação Mutante",
        color = Color(0xFFE67E22),
        characters = listOf("Wolverine", "Magneto", "Storm"),
    ),
    Planet(
        name = "Klyntar",
        realm = "Mundo Simbionte",
        color = Color(0xFF95A5A6),
        characters = listOf("Venom"),
    ),
)
