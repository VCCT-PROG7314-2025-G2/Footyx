package com.example.footyxapp.data.model

import com.google.gson.annotations.SerializedName

// Team Search Models
data class TeamSearchResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: TeamSearchParameters,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<TeamData>
)

data class TeamSearchParameters(
    @SerializedName("search")
    val search: String
)

data class TeamData(
    @SerializedName("team")
    val team: TeamInfo,
    @SerializedName("venue")
    val venue: Venue
)

data class TeamInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String?,
    @SerializedName("country")
    val country: String,
    @SerializedName("founded")
    val founded: Int?,
    @SerializedName("national")
    val national: Boolean,
    @SerializedName("logo")
    val logo: String
)

data class Venue(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("capacity")
    val capacity: Int?,
    @SerializedName("surface")
    val surface: String?,
    @SerializedName("image")
    val image: String?
)

// Team Leagues Response
data class TeamLeaguesResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: TeamLeaguesParameters,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: List<TeamLeagueData>
)

data class TeamLeaguesParameters(
    @SerializedName("season")
    val season: String,
    @SerializedName("team")
    val team: String
)

data class TeamLeagueData(
    @SerializedName("league")
    val league: TeamLeague,
    @SerializedName("country")
    val country: Country,
    @SerializedName("seasons")
    val seasons: List<Season>
)

data class TeamLeague(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("logo")
    val logo: String
)

data class Country(
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String?,
    @SerializedName("flag")
    val flag: String?
)

data class Season(
    @SerializedName("year")
    val year: Int,
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("current")
    val current: Boolean,
    @SerializedName("coverage")
    val coverage: Coverage
)

data class Coverage(
    @SerializedName("fixtures")
    val fixtures: FixtureCoverage,
    @SerializedName("standings")
    val standings: Boolean,
    @SerializedName("players")
    val players: Boolean,
    @SerializedName("top_scorers")
    val topScorers: Boolean,
    @SerializedName("top_assists")
    val topAssists: Boolean,
    @SerializedName("top_cards")
    val topCards: Boolean,
    @SerializedName("injuries")
    val injuries: Boolean,
    @SerializedName("predictions")
    val predictions: Boolean,
    @SerializedName("odds")
    val odds: Boolean
)

data class FixtureCoverage(
    @SerializedName("events")
    val events: Boolean,
    @SerializedName("lineups")
    val lineups: Boolean,
    @SerializedName("statistics_fixtures")
    val statisticsFixtures: Boolean,
    @SerializedName("statistics_players")
    val statisticsPlayers: Boolean
)

// Team Statistics Models
data class TeamStatisticsResponse(
    @SerializedName("get")
    val get: String,
    @SerializedName("parameters")
    val parameters: TeamStatisticsParameters,
    @SerializedName("errors")
    val errors: List<Any>,
    @SerializedName("results")
    val results: Int,
    @SerializedName("paging")
    val paging: Paging,
    @SerializedName("response")
    val response: TeamStatistics
)

data class TeamStatisticsParameters(
    @SerializedName("league")
    val league: String,
    @SerializedName("season")
    val season: String,
    @SerializedName("team")
    val team: String
)

data class TeamStatistics(
    @SerializedName("league")
    val league: TeamLeague,
    @SerializedName("team")
    val team: TeamInfo,
    @SerializedName("form")
    val form: String,
    @SerializedName("fixtures")
    val fixtures: TeamFixtures,
    @SerializedName("goals")
    val goals: TeamGoals,
    @SerializedName("biggest")
    val biggest: TeamBiggest,
    @SerializedName("clean_sheet")
    val cleanSheet: TeamCleanSheet,
    @SerializedName("failed_to_score")
    val failedToScore: TeamFailedToScore,
    @SerializedName("penalty")
    val penalty: TeamPenalty,
    @SerializedName("lineups")
    val lineups: List<TeamLineup>,
    @SerializedName("cards")
    val cards: TeamCards
)

data class TeamFixtures(
    @SerializedName("played")
    val played: TeamRecord,
    @SerializedName("wins")
    val wins: TeamRecord,
    @SerializedName("draws")
    val draws: TeamRecord,
    @SerializedName("loses")
    val loses: TeamRecord
)

data class TeamRecord(
    @SerializedName("home")
    val home: Int,
    @SerializedName("away")
    val away: Int,
    @SerializedName("total")
    val total: Int
)

data class TeamGoals(
    @SerializedName("for")
    val goalsFor: TeamGoalsFor,
    @SerializedName("against")
    val goalsAgainst: TeamGoalsAgainst
)

data class TeamGoalsFor(
    @SerializedName("total")
    val total: TeamRecord,
    @SerializedName("average")
    val average: TeamGoalsAverage,
    @SerializedName("minute")
    val minute: TeamGoalsMinute,
    @SerializedName("under_over")
    val underOver: TeamGoalsUnderOver
)

data class TeamGoalsAgainst(
    @SerializedName("total")
    val total: TeamRecord,
    @SerializedName("average")
    val average: TeamGoalsAverage,
    @SerializedName("minute")
    val minute: TeamGoalsMinute,
    @SerializedName("under_over")
    val underOver: TeamGoalsUnderOver
)

data class TeamGoalsAverage(
    @SerializedName("home")
    val home: String,
    @SerializedName("away")
    val away: String,
    @SerializedName("total")
    val total: String
)

data class TeamGoalsMinute(
    @SerializedName("0-15")
    val min0To15: MinuteStats,
    @SerializedName("16-30")
    val min16To30: MinuteStats,
    @SerializedName("31-45")
    val min31To45: MinuteStats,
    @SerializedName("46-60")
    val min46To60: MinuteStats,
    @SerializedName("61-75")
    val min61To75: MinuteStats,
    @SerializedName("76-90")
    val min76To90: MinuteStats,
    @SerializedName("91-105")
    val min91To105: MinuteStats,
    @SerializedName("106-120")
    val min106To120: MinuteStats
)

data class MinuteStats(
    @SerializedName("total")
    val total: Int?,
    @SerializedName("percentage")
    val percentage: String?
)

data class TeamGoalsUnderOver(
    @SerializedName("0.5")
    val underOver05: UnderOverStats,
    @SerializedName("1.5")
    val underOver15: UnderOverStats,
    @SerializedName("2.5")
    val underOver25: UnderOverStats,
    @SerializedName("3.5")
    val underOver35: UnderOverStats,
    @SerializedName("4.5")
    val underOver45: UnderOverStats
)

data class UnderOverStats(
    @SerializedName("over")
    val over: Int,
    @SerializedName("under")
    val under: Int
)

data class TeamBiggest(
    @SerializedName("streak")
    val streak: TeamStreak,
    @SerializedName("wins")
    val wins: TeamBiggestWins,
    @SerializedName("loses")
    val loses: TeamBiggestLoses,
    @SerializedName("goals")
    val goals: TeamBiggestGoals
)

data class TeamStreak(
    @SerializedName("wins")
    val wins: Int,
    @SerializedName("draws")
    val draws: Int,
    @SerializedName("loses")
    val loses: Int
)

data class TeamBiggestWins(
    @SerializedName("home")
    val home: String?,
    @SerializedName("away")
    val away: String?
)

data class TeamBiggestLoses(
    @SerializedName("home")
    val home: String?,
    @SerializedName("away")
    val away: String?
)

data class TeamBiggestGoals(
    @SerializedName("for")
    val goalsFor: TeamBiggestGoalsFor,
    @SerializedName("against")
    val goalsAgainst: TeamBiggestGoalsAgainst
)

data class TeamBiggestGoalsFor(
    @SerializedName("home")
    val home: Int,
    @SerializedName("away")
    val away: Int
)

data class TeamBiggestGoalsAgainst(
    @SerializedName("home")
    val home: Int,
    @SerializedName("away")
    val away: Int
)

data class TeamCleanSheet(
    @SerializedName("home")
    val home: Int,
    @SerializedName("away")
    val away: Int,
    @SerializedName("total")
    val total: Int
)

data class TeamFailedToScore(
    @SerializedName("home")
    val home: Int,
    @SerializedName("away")
    val away: Int,
    @SerializedName("total")
    val total: Int
)

data class TeamPenalty(
    @SerializedName("scored")
    val scored: PenaltyStats,
    @SerializedName("missed")
    val missed: PenaltyStats,
    @SerializedName("total")
    val total: Int
)

data class PenaltyStats(
    @SerializedName("total")
    val total: Int,
    @SerializedName("percentage")
    val percentage: String
)

data class TeamLineup(
    @SerializedName("formation")
    val formation: String,
    @SerializedName("played")
    val played: Int
)

data class TeamCards(
    @SerializedName("yellow")
    val yellow: TeamGoalsMinute,
    @SerializedName("red")
    val red: TeamGoalsMinute
)
