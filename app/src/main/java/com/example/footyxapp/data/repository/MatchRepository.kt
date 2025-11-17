package com.example.footyxapp.data.repository

import com.example.footyxapp.data.api.ApiClient
import com.example.footyxapp.data.model.FixtureResponse
import com.example.footyxapp.data.model.FixtureStatisticsResponse
import com.example.footyxapp.data.model.FixtureEventsResponse
import com.example.footyxapp.data.model.OddsResponse
import com.example.footyxapp.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MatchRepository {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val apiService = ApiClient.footballApiService

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getLiveFixtures(): Result<FixtureResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtures(
                apiKey = Constants.API_KEY,
                live = "all"
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch live fixtures: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getFixturesByDate(date: String): Result<FixtureResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtures(
                apiKey = Constants.API_KEY,
                date = date
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch fixtures by date: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getFixtureById(fixtureId: Int): Result<FixtureResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtureById(
                apiKey = Constants.API_KEY,
                fixtureId = fixtureId
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch fixture: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getFixtureStatistics(fixtureId: Int): Result<FixtureStatisticsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtureStatistics(
                apiKey = Constants.API_KEY,
                fixtureId = fixtureId
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch fixture statistics: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getFixtureEvents(fixtureId: Int): Result<FixtureEventsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtureEvents(
                apiKey = Constants.API_KEY,
                fixtureId = fixtureId
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch fixture events: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getRecentFixtures(last: Int = 10): Result<FixtureResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtures(
                apiKey = Constants.API_KEY,
                last = last
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch recent fixtures: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getFixtureOdds(fixtureId: Int, bookmakerId: Int? = null): Result<OddsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFixtureOdds(
                apiKey = Constants.API_KEY,
                fixtureId = fixtureId,
                bookmakerId = bookmakerId,
                betId = 1  // Bet ID 1 = "Match Winner" (Home/Draw/Away)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch fixture odds: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    suspend fun getHeadToHeadFixtures(homeTeamId: Int, awayTeamId: Int, last: Int = 10): Result<FixtureResponse> = withContext(Dispatchers.IO) {
        try {
            val h2h = "$homeTeamId-$awayTeamId"
            val response = apiService.getHeadToHead(
                apiKey = Constants.API_KEY,
                h2h = h2h,
                last = last
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch head-to-head fixtures: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
