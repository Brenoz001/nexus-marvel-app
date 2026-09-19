package com.example.nexus_marvel_app.util

data class Soundtrack(val track: String, val artist: String, val movie: String)

/** Maps ~30 popular Marvel characters to an iconic movie/trailer soundtrack. */
object Soundtracks {
    private val map: Map<String, Soundtrack> = mapOf(
        "Spider-Man" to Soundtrack("Sunflower", "Post Malone & Swae Lee", "Into the Spider-Verse"),
        "Iron Man" to Soundtrack("Back in Black", "AC/DC", "Iron Man"),
        "Thor" to Soundtrack("Immigrant Song", "Led Zeppelin", "Thor: Ragnarok"),
        "Black Panther" to Soundtrack("All the Stars", "Kendrick Lamar & SZA", "Black Panther"),
        "Captain America" to Soundtrack("Star Spangled Man", "Alan Menken", "The First Avenger"),
        "Guardians of the Galaxy" to Soundtrack("Hooked on a Feeling", "Blue Swede", "Guardians of the Galaxy"),
        "Scarlet Witch" to Soundtrack("Chaos Magic", "Christophe Beck", "WandaVision"),
        "Wolverine" to Soundtrack("Hurt", "Johnny Cash", "Logan"),
        "Doctor Strange" to Soundtrack("Interstellar Overdrive", "Pink Floyd", "Doctor Strange"),
        "Hulk" to Soundtrack("The Lonely Man", "Joe Harnell", "The Incredible Hulk TV"),
        "Black Widow" to Soundtrack("Smells Like Teen Spirit", "Malia J", "Black Widow"),
        "Ant-Man" to Soundtrack("It's a Small World", "Theme", "Ant-Man"),
        "Captain Marvel" to Soundtrack("Just a Girl", "No Doubt", "Captain Marvel"),
        "Deadpool" to Soundtrack("Angel of the Morning", "Juice Newton", "Deadpool"),
        "Thanos" to Soundtrack("Forge", "Alan Silvestri", "Infinity War"),
        "Loki" to Soundtrack("Green Theme", "Natalie Holt", "Loki"),
        "Vision" to Soundtrack("WandaVision Theme", "Kristen Anderson-Lopez", "WandaVision"),
        "Hawkeye" to Soundtrack("Save the City", "Rogers: The Musical", "Hawkeye"),
        "Falcon" to Soundtrack("Louisiana Hero", "Henry Jackman", "The Falcon and the Winter Soldier"),
        "Moon Knight" to Soundtrack("Day N Nite", "Kid Cudi", "Moon Knight"),
        "Daredevil" to Soundtrack("Daredevil Main Theme", "John Paesano", "Daredevil"),
        "Punisher" to Soundtrack("One", "Metallica", "The Punisher"),
        "Storm" to Soundtrack("Storm Theme", "Henry Jackman", "X-Men: Days of Future Past"),
        "Magneto" to Soundtrack("Frankenstein", "Henry Jackman", "X-Men: First Class"),
        "Professor X" to Soundtrack("Cerebro Theme", "John Ottman", "X-Men"),
        "Venom" to Soundtrack("Venom", "Eminem", "Venom"),
        "Silver Surfer" to Soundtrack("Fantastic Four Rise Theme", "John Ottman", "Rise of the Silver Surfer"),
        "Ghost Rider" to Soundtrack("Ghost Riders in the Sky", "Spiderbait", "Ghost Rider"),
        "Blade" to Soundtrack("Confusion", "New Order", "Blade"),
        "Groot" to Soundtrack("Mr. Blue Sky", "Electric Light Orchestra", "Guardians of the Galaxy Vol. 2"),
    )

    /** All tracks as (character, soundtrack) pairs, for the Jukebox. */
    val all: List<Pair<String, Soundtrack>> = map.entries.map { it.key to it.value }

    /** Exact match first, then a loose contains-match. */
    fun forCharacter(name: String?): Soundtrack? {
        if (name.isNullOrBlank()) return null
        map[name]?.let { return it }
        val lower = name.lowercase()
        val key = map.keys.firstOrNull { k ->
            lower.contains(k.lowercase()) || k.lowercase().contains(lower)
        }
        return key?.let { map[it] }
    }
}
