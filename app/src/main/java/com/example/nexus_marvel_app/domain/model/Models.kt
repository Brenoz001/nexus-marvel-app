package com.example.nexus_marvel_app.domain.model

/** A named reference to another entity (team member, power, ally...). */
data class NamedRef(val id: Int, val name: String)

enum class Alignment { HERO, VILLAIN }

data class Character(
    val id: Int,
    val name: String,
    val realName: String?,
    val deck: String?,
    val imageMedium: String?,
    val imageOriginal: String?,
    val publisherName: String?,
    val appearances: Int?,
    val powers: List<NamedRef>,
    val teams: List<NamedRef>,
    val friends: List<NamedRef>,
    val enemies: List<NamedRef>,
    val firstAppearance: String?,
) {
    val isMarvel: Boolean get() = publisherName?.contains("marvel", ignoreCase = true) == true

    /** Year parsed from the first appearance issue name, if present. */
    val firstYear: String?
        get() = firstAppearance?.let { Regex("(19|20)\\d{2}").find(it)?.value }
}

data class Team(
    val id: Int,
    val name: String,
    val deck: String?,
    val imageMedium: String?,
    val imageOriginal: String?,
    val publisherName: String?,
    val memberCount: Int?,
    val appearances: Int?,
) {
    val isMarvel: Boolean get() = publisherName?.contains("marvel", ignoreCase = true) == true
}

data class StoryArc(
    val id: Int,
    val name: String,
    val deck: String?,
    val imageMedium: String?,
    val imageOriginal: String?,
    val publisherName: String?,
    val appearances: Int?,
    val firstAppearance: String?,
) {
    val isMarvel: Boolean get() = publisherName?.contains("marvel", ignoreCase = true) == true
}

data class Power(
    val id: Int,
    val name: String,
    val characterCount: Int?,
)

/** A page of results plus the total count (for pagination). */
data class Page<T>(
    val items: List<T>,
    val total: Int,
    val offset: Int,
)
