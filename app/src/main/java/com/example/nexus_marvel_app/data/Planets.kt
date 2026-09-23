package com.example.nexus_marvel_app.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.nexus_marvel_app.R

/** A Marvel galaxy (with its home world) and the iconic characters tied to it. */
data class Planet(
    val name: String,
    val realm: String,
    val color: Color,
    /** Real galaxy/nebula photo rendered as the galaxy's face. */
    @DrawableRes val imageRes: Int,
    /** Character names pulled from Comic Vine for this galaxy. */
    val characters: List<String>,
)

/** The NEXUS cosmos: Marvel galaxies you can travel between. */
val MARVEL_PLANETS: List<Planet> = listOf(
    Planet(
        name = "Via Láctea",
        realm = "Mundo natal: Terra",
        color = Color(0xFF4A90D9),
        imageRes = R.drawable.galaxy_milkyway,
        characters = listOf(
            "Spider-Man", "Iron Man", "Captain America", "Hulk", "Black Widow",
            "Doctor Strange", "Daredevil", "Vision", "Scarlet Witch", "Black Panther",
            "Hawkeye", "Falcon", "Ant-Man", "War Machine", "Winter Soldier",
            "Punisher", "Luke Cage", "Iron Fist", "Elektra", "Moon Knight",
            "She-Hulk", "Nick Fury",
        ),
    ),
    Planet(
        name = "Yggdrasil",
        realm = "Reino Eterno: Asgard",
        color = Color(0xFFE9B949),
        imageRes = R.drawable.galaxy_yggdrasil,
        characters = listOf(
            "Thor", "Loki", "Valkyrie", "Hela", "Odin", "Heimdall", "Sif",
            "Beta Ray Bill", "Enchantress", "Balder",
        ),
    ),
    Planet(
        name = "Sanctuário",
        realm = "Domínio de Thanos: Titã",
        color = Color(0xFF9B59B6),
        imageRes = R.drawable.galaxy_sanctuary,
        characters = listOf(
            "Thanos", "Gamora", "Nebula", "Drax", "Ebony Maw", "Starfox",
            "Corvus Glaive", "Proxima Midnight",
        ),
    ),
    Planet(
        name = "Andrômeda",
        realm = "Império Nova: Xandar",
        color = Color(0xFF1ABC9C),
        imageRes = R.drawable.galaxy_andromeda,
        characters = listOf(
            "Star-Lord", "Captain Marvel", "Silver Surfer", "Groot", "Rocket Raccoon",
            "Nova", "Adam Warlock", "Mantis", "Ronan", "Beta Ray Bill", "Gladiator",
        ),
    ),
    Planet(
        name = "Constelação Mutante",
        realm = "Ilha viva: Krakoa",
        color = Color(0xFFE67E22),
        imageRes = R.drawable.galaxy_krakoa,
        characters = listOf(
            "Wolverine", "Magneto", "Storm", "Cyclops", "Jean Grey", "Beast",
            "Nightcrawler", "Rogue", "Gambit", "Colossus", "Professor X",
            "Emma Frost", "Mystique", "Cable", "Iceman", "Jubilee",
        ),
    ),
    Planet(
        name = "Nebulosa Simbionte",
        realm = "Mundo natal: Klyntar",
        color = Color(0xFF95A5A6),
        imageRes = R.drawable.galaxy_klyntar,
        characters = listOf(
            "Venom", "Carnage", "Anti-Venom", "Toxin", "Riot", "Agent Venom",
            "Knull", "Scream",
        ),
    ),
)
