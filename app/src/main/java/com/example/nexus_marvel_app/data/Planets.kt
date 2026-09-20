package com.example.nexus_marvel_app.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.nexus_marvel_app.R

/** A Marvel realm/world and the iconic characters tied to it. */
data class Planet(
    val name: String,
    val realm: String,
    val color: Color,
    /** Real planet/moon photo rendered as the world's surface. */
    @DrawableRes val imageRes: Int,
    /** Character names (match the curated featured list). */
    val characters: List<String>,
)

/** The NEXUS cosmos: Marvel worlds you can travel between. */
val MARVEL_PLANETS: List<Planet> = listOf(
    Planet(
        name = "Terra",
        realm = "Sistema Sol • Sector 2814",
        color = Color(0xFF4A90D9),
        imageRes = R.drawable.planet_earth,
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
        imageRes = R.drawable.planet_saturn,
        characters = listOf("Thor", "Loki"),
    ),
    Planet(
        name = "Titã",
        realm = "Lua de Saturno",
        color = Color(0xFF9B59B6),
        imageRes = R.drawable.planet_mars,
        characters = listOf("Thanos"),
    ),
    Planet(
        name = "Cosmos",
        realm = "Xandar • Espaço profundo",
        color = Color(0xFF1ABC9C),
        imageRes = R.drawable.planet_neptune,
        characters = listOf("Star-Lord", "Captain Marvel", "Silver Surfer"),
    ),
    Planet(
        name = "Krakoa",
        realm = "Nação Mutante",
        color = Color(0xFFE67E22),
        imageRes = R.drawable.planet_jupiter,
        characters = listOf("Wolverine", "Magneto", "Storm"),
    ),
    Planet(
        name = "Klyntar",
        realm = "Mundo Simbionte",
        color = Color(0xFF95A5A6),
        imageRes = R.drawable.planet_moon,
        characters = listOf("Venom"),
    ),
)
