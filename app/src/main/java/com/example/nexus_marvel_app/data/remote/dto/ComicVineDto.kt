package com.example.nexus_marvel_app.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Standard Comic Vine image object. */
data class ImageDto(
    @SerializedName("icon_url") val iconUrl: String? = null,
    @SerializedName("medium_url") val mediumUrl: String? = null,
    @SerializedName("screen_url") val screenUrl: String? = null,
    @SerializedName("screen_large_url") val screenLargeUrl: String? = null,
    @SerializedName("small_url") val smallUrl: String? = null,
    @SerializedName("super_url") val superUrl: String? = null,
    @SerializedName("thumb_url") val thumbUrl: String? = null,
    @SerializedName("tiny_url") val tinyUrl: String? = null,
    @SerializedName("original_url") val originalUrl: String? = null,
)

/** Lightweight reference to another resource. */
data class RefDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("api_detail_url") val apiDetailUrl: String? = null,
)

data class IssueRefDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("issue_number") val issueNumber: String? = null,
)

data class CharacterDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("real_name") val realName: String? = null,
    @SerializedName("aliases") val aliases: String? = null,
    @SerializedName("deck") val deck: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("publisher") val publisher: RefDto? = null,
    @SerializedName("powers") val powers: List<RefDto>? = null,
    @SerializedName("teams") val teams: List<RefDto>? = null,
    @SerializedName("character_friends") val friends: List<RefDto>? = null,
    @SerializedName("character_enemies") val enemies: List<RefDto>? = null,
    @SerializedName("count_of_issue_appearances") val appearances: Int? = null,
    @SerializedName("first_appeared_in_issue") val firstAppearance: IssueRefDto? = null,
    @SerializedName("birth") val birth: String? = null,
)

data class TeamDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("deck") val deck: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("publisher") val publisher: RefDto? = null,
    @SerializedName("characters") val characters: List<RefDto>? = null,
    @SerializedName("count_of_issue_appearances") val appearances: Int? = null,
    @SerializedName("count_of_team_members") val memberCount: Int? = null,
    @SerializedName("first_appeared_in_issue") val firstAppearance: IssueRefDto? = null,
)

data class StoryArcDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("deck") val deck: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: ImageDto? = null,
    @SerializedName("publisher") val publisher: RefDto? = null,
    @SerializedName("count_of_issue_appearances") val appearances: Int? = null,
    @SerializedName("first_appeared_in_issue") val firstAppearance: IssueRefDto? = null,
    @SerializedName("characters") val characters: List<RefDto>? = null,
)

data class PowerDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("characters") val characters: List<RefDto>? = null,
)

/** List response envelope. */
data class ListResponse<T>(
    @SerializedName("error") val error: String? = null,
    @SerializedName("limit") val limit: Int = 0,
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("number_of_page_results") val pageResults: Int = 0,
    @SerializedName("number_of_total_results") val totalResults: Int = 0,
    @SerializedName("status_code") val statusCode: Int = 0,
    @SerializedName("results") val results: List<T>? = null,
)

/** Single-object response envelope. */
data class DetailResponse<T>(
    @SerializedName("error") val error: String? = null,
    @SerializedName("status_code") val statusCode: Int = 0,
    @SerializedName("results") val results: T? = null,
)
