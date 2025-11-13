package com.example.footyxapp.data.api

import com.example.footyxapp.data.model.PlayerResponse
import com.example.footyxapp.data.model.PlayerSearchResponse
import com.example.footyxapp.data.model.TeamSearchResponse
import com.example.footyxapp.data.model.TeamLeaguesResponse
import com.example.footyxapp.data.model.TeamStatisticsResponse
import com.example.footyxapp.data.model.LeagueSearchResponse
import com.example.footyxapp.data.model.StandingsResponse
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

}
