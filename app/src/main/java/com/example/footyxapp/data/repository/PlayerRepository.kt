package com.example.footyxapp.data.repository

import com.example.footyxapp.data.api.ApiClient
import com.example.footyxapp.data.model.PlayerResponse
import com.example.footyxapp.data.model.PlayerSearchResponse
import com.example.footyxapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlayerRepository {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val apiService = ApiClient.footballApiService

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getPlayer(playerId: Int, season: Int): Result<PlayerResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPlayer(
                    apiKey = Constants.API_KEY,
                    playerId = playerId,
                    season = season
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch player data: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun searchPlayers(query: String): Result<PlayerSearchResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchPlayers(
                    apiKey = Constants.API_KEY,
                    searchQuery = query
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to search players: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
