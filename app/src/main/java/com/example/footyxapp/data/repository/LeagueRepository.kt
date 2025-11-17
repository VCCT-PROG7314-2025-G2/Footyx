package com.example.footyxapp.data.repository

import com.example.footyxapp.data.api.ApiClient
import com.example.footyxapp.data.model.LeagueSearchResponse
import com.example.footyxapp.data.model.StandingsResponse
import com.example.footyxapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeagueRepository {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val apiService = ApiClient.footballApiService

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun searchLeagues(query: String): Result<LeagueSearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchLeagues(
                    apiKey = Constants.API_KEY,
                    searchQuery = query
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to search leagues: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getStandings(leagueId: Int, season: Int): Result<StandingsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStandings(
                    apiKey = Constants.API_KEY,
                    leagueId = leagueId,
                    season = season
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch standings: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
