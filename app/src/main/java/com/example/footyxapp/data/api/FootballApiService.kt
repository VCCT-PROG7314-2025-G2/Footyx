package com.example.footyxapp.data.api

import com.example.footyxapp.data.model.PlayerResponse
import com.example.footyxapp.data.model.PlayerSearchResponse
import com.example.footyxapp.data.model.TeamSearchResponse
import com.example.footyxapp.data.model.TeamLeaguesResponse
import com.example.footyxapp.data.model.TeamStatisticsResponse
import com.example.footyxapp.data.model.LeagueSearchResponse
import com.example.footyxapp.data.model.StandingsResponse
import com.example.footyxapp.data.model.FixtureResponse
import com.example.footyxapp.data.model.FixtureStatisticsResponse
import com.example.footyxapp.data.model.FixtureEventsResponse
import com.example.footyxapp.data.model.OddsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FootballApiService {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("players")
    suspend fun getPlayer(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("id") playerId: Int,
        @Query("season") season: Int
    ): Response<PlayerResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("players/profiles")
    suspend fun searchPlayers(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("search") searchQuery: String
    ): Response<PlayerSearchResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("teams")
    suspend fun searchTeams(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("search") searchQuery: String
    ): Response<TeamSearchResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("leagues")
    suspend fun getTeamLeagues(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("season") season: Int,
        @Query("team") teamId: Int
    ): Response<TeamLeaguesResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("teams/statistics")
    suspend fun getTeamStatistics(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("league") leagueId: Int,
        @Query("season") season: Int,
        @Query("team") teamId: Int
    ): Response<TeamStatisticsResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("leagues")
    suspend fun searchLeagues(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("search") searchQuery: String
    ): Response<LeagueSearchResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("standings")
    suspend fun getStandings(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): Response<StandingsResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("fixtures")
    suspend fun getFixtures(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("live") live: String? = null,
        @Query("date") date: String? = null,
        @Query("league") league: Int? = null,
        @Query("season") season: Int? = null,
        @Query("team") team: Int? = null,
        @Query("last") last: Int? = null,
        @Query("next") next: Int? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null
    ): Response<FixtureResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("fixtures")
    suspend fun getFixtureById(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("id") fixtureId: Int
    ): Response<FixtureResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("fixtures/statistics")
    suspend fun getFixtureStatistics(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("fixture") fixtureId: Int
    ): Response<FixtureStatisticsResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("fixtures/events")
    suspend fun getFixtureEvents(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("fixture") fixtureId: Int
    ): Response<FixtureEventsResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("odds")
    suspend fun getFixtureOdds(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("fixture") fixtureId: Int,
        @Query("bookmaker") bookmakerId: Int? = null,
        @Query("bet") betId: Int? = null
    ): Response<OddsResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    @GET("fixtures/headtohead")
    suspend fun getHeadToHead(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "v3.football.api-sports.io",
        @Query("h2h") h2h: String,
        @Query("last") last: Int? = null,
        @Query("next") next: Int? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("status") status: String? = null
    ): Response<FixtureResponse>

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
