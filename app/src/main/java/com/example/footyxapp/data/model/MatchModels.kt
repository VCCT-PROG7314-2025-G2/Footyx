package com.example.footyxapp.data.model

import com.google.gson.annotations.SerializedName

// Fixture Response Models
data class FixtureResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: Map<String, Any>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<FixtureData>
)

data class FixtureData(
    @SerializedName("fixture")
    val fixture: Fixture,
    @SerializedName("league")
    val league: FixtureLeague,
    @SerializedName("teams")
    val teams: FixtureTeams,
    @SerializedName("goals")
    val goals: FixtureGoals,
    @SerializedName("score")
    val score: FixtureScore
)

data class Fixture(
    @SerializedName("id")
    val id: Int,
    @SerializedName("referee")
    val referee: String?,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("periods")
    val periods: FixturePeriods,
    @SerializedName("venue")
    val venue: FixtureVenue,
    @SerializedName("status")
    val status: FixtureStatus
)

data class FixturePeriods(
    @SerializedName("first")
    val first: Long?,
    @SerializedName("second")
    val second: Long?
)

data class FixtureVenue(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("city")
    val city: String?
)

data class FixtureStatus(
    @SerializedName("long")
    val long: String,
    @SerializedName("short")
    val short: String,
    @SerializedName("elapsed")
    val elapsed: Int?,
    @SerializedName("extra")
    val extra: Int?
)

data class FixtureLeague(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("flag")
    val flag: String?,
    @SerializedName("season")
    val season: Int,
    @SerializedName("round")
    val round: String?
)

data class FixtureTeams(
    @SerializedName("home")
    val home: FixtureTeam,
    @SerializedName("away")
    val away: FixtureTeam
)

data class FixtureTeam(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("winner")
    val winner: Boolean?
)

data class FixtureGoals(
    @SerializedName("home")
    val home: Int?,
    @SerializedName("away")
    val away: Int?
)

data class FixtureScore(
    @SerializedName("halftime")
    val halftime: FixtureScoreDetail,
    @SerializedName("fulltime")
    val fulltime: FixtureScoreDetail,
    @SerializedName("extratime")
    val extratime: FixtureScoreDetail,
    @SerializedName("penalty")
    val penalty: FixtureScoreDetail
)

data class FixtureScoreDetail(
    @SerializedName("home")
    val home: Int?,
    @SerializedName("away")
    val away: Int?
)

// Fixture Statistics Response Models
data class FixtureStatisticsResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: Map<String, Any>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<FixtureTeamStatistics>
)

data class FixtureTeamStatistics(
    @SerializedName("team")
    val team: StatTeam,
    @SerializedName("statistics")
    val statistics: List<Statistic>
)

data class StatTeam(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val logo: String
)

data class Statistic(
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: Any?
)

// Fixture Events Response Models
data class FixtureEventsResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: Map<String, Any>,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<FixtureEvent>
)

data class FixtureEvent(
    @SerializedName("time")
    val time: EventTime,
    @SerializedName("team")
    val team: EventTeam,
    @SerializedName("player")
    val player: EventPlayer,
    @SerializedName("assist")
    val assist: EventPlayer,
    @SerializedName("type")
    val type: String,
    @SerializedName("detail")
    val detail: String,
    @SerializedName("comments")
    val comments: String?
)

data class EventTime(
    @SerializedName("elapsed")
    val elapsed: Int,
    @SerializedName("extra")
    val extra: Int?
)

data class EventTeam(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val logo: String
)

data class EventPlayer(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)
