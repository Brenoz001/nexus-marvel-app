package com.example.nexus_marvel_app.data.mapper

import com.example.nexus_marvel_app.data.remote.dto.CharacterDto
import com.example.nexus_marvel_app.data.remote.dto.ImageDto
import com.example.nexus_marvel_app.data.remote.dto.PowerDto
import com.example.nexus_marvel_app.data.remote.dto.RefDto
import com.example.nexus_marvel_app.data.remote.dto.StoryArcDto
import com.example.nexus_marvel_app.data.remote.dto.TeamDto
import com.example.nexus_marvel_app.domain.model.Character
import com.example.nexus_marvel_app.domain.model.NamedRef
import com.example.nexus_marvel_app.domain.model.Power
import com.example.nexus_marvel_app.domain.model.StoryArc
import com.example.nexus_marvel_app.domain.model.Team

private fun ImageDto?.medium(): String? = this?.mediumUrl ?: this?.smallUrl ?: this?.thumbUrl
private fun ImageDto?.original(): String? = this?.originalUrl ?: this?.superUrl ?: this?.screenLargeUrl ?: medium()

private fun List<RefDto>?.toRefs(): List<NamedRef> =
    this.orEmpty().mapNotNull { r -> r.name?.let { NamedRef(r.id, it) } }

fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name ?: "Sem nome",
    realName = realName?.takeIf { it.isNotBlank() },
    deck = deck?.takeIf { it.isNotBlank() },
    imageMedium = image.medium(),
    imageOriginal = image.original(),
    publisherName = publisher?.name,
    appearances = appearances,
    powers = powers.toRefs(),
    teams = teams.toRefs(),
    friends = friends.toRefs(),
    enemies = enemies.toRefs(),
    firstAppearance = firstAppearance?.name,
)

fun TeamDto.toDomain(): Team = Team(
    id = id,
    name = name ?: "Sem nome",
    deck = deck?.takeIf { it.isNotBlank() },
    imageMedium = image.medium(),
    imageOriginal = image.original(),
    publisherName = publisher?.name,
    memberCount = memberCount ?: characters?.size,
    appearances = appearances,
)

fun StoryArcDto.toDomain(): StoryArc = StoryArc(
    id = id,
    name = name ?: "Sem nome",
    deck = deck?.takeIf { it.isNotBlank() },
    imageMedium = image.medium(),
    imageOriginal = image.original(),
    publisherName = publisher?.name,
    appearances = appearances,
    firstAppearance = firstAppearance?.name,
)

fun PowerDto.toDomain(): Power = Power(
    id = id,
    name = name ?: "Poder",
    characterCount = characters?.size,
)
