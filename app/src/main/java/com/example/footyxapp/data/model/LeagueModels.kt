package com.example.footyxapp.data.model

import com.google.gson.annotations.SerializedName

// League Search Models
data class LeagueSearchResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: LeagueSearchParameters,
    @SerializedName("errors")
    val errors: Any?,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<LeagueData>
)

data class LeagueSearchParameters(
    @SerializedName("search")
    val search: String
)

data class LeagueData(
    @SerializedName("league")
    val league: LeagueInfo,
    @SerializedName("country")
    val country: Country,
    @SerializedName("seasons")
    val seasons: List<Season>
)

data class LeagueInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("logo")
    val logo: String
)

// Standings Response Models
data class StandingsResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: StandingsParameters,
    @SerializedName("errors")
    val errors: Any?,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<LeagueStanding>
)

data class StandingsParameters(
    @SerializedName("league")
    val league: String,
    @SerializedName("season")
    val season: String
)

data class LeagueStanding(
    @SerializedName("league")
    val league: StandingLeague
)

data class StandingLeague(
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
    @SerializedName("standings")
    val standings: List<List<TeamStanding>>
)

data class TeamStanding(
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("team")
    val team: StandingTeam,
    @SerializedName("points")
    val points: Int,
    @SerializedName("goalsDiff")
    val goalsDiff: Int,
    @SerializedName("group")
    val group: String,
    @SerializedName("form")
    val form: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("all")
    val all: StandingRecord,
    @SerializedName("home")
    val home: StandingRecord,
    @SerializedName("away")
    val away: StandingRecord,
    @SerializedName("update")
    val update: String
)

data class StandingTeam(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val logo: String
)

data class StandingRecord(
    @SerializedName("played")
    val played: Int,
    @SerializedName("win")
    val win: Int,
    @SerializedName("draw")
    val draw: Int,
    @SerializedName("lose")
    val lose: Int,
    @SerializedName("goals")
    val goals: StandingGoals
)

data class StandingGoals(
    @SerializedName("for")
    val goalsFor: Int,
    @SerializedName("against")
    val goalsAgainst: Int
)
