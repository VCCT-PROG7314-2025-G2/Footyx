package com.example.footyxapp.data.model

// API Response wrapper
data class OddsResponse(
    val response: List<OddsData>
)

// Main odds data
data class OddsData(
    val league: OddsLeague,
    val fixture: OddsFixture,
    val update: String,
    val bookmakers: List<Bookmaker>
)

// League info in odds response
data class OddsLeague(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String?,
    val season: Int
)

// Fixture info in odds response
data class OddsFixture(
    val id: Int,
    val timezone: String,
    val date: String,
    val timestamp: Long
)

// Bookmaker with their bets
data class Bookmaker(
    val id: Int,
    val name: String,
    val bets: List<Bet>
)

// Bet type (e.g., Match Winner, Goals Over/Under)
data class Bet(
    val id: Int,
    val name: String,
    val values: List<BetValue>
)

// Individual bet value (e.g., Home: 2.50)
data class BetValue(
    val value: String,
    val odd: String
)
