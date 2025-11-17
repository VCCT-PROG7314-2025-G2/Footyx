package com.example.footyxapp.data.repository

import com.example.footyxapp.data.api.ApiClient
import com.example.footyxapp.data.model.TeamSearchResponse
import com.example.footyxapp.data.model.TeamLeaguesResponse
import com.example.footyxapp.data.model.TeamStatisticsResponse
import com.example.footyxapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeamRepository {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val apiService = ApiClient.footballApiService

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun searchTeams(query: String): Result<TeamSearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchTeams(
                    apiKey = Constants.API_KEY,
                    searchQuery = query
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to search teams: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getTeamLeagues(teamId: Int, season: Int): Result<TeamLeaguesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTeamLeagues(
                    apiKey = Constants.API_KEY,
                    season = season,
                    teamId = teamId
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch team leagues: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getTeamStatistics(teamId: Int, leagueId: Int, season: Int): Result<TeamStatisticsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTeamStatistics(
                    apiKey = Constants.API_KEY,
                    leagueId = leagueId,
                    season = season,
                    teamId = teamId
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch team statistics: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
