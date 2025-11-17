package com.example.footyxapp.data.model

// API Response wrapper
data class TransferResponse(
    val response: List<TransferData>
)

// Main transfer data for a player
data class TransferData(
    val player: TransferPlayer,
    val update: String,
    val transfers: List<Transfer>
)

// Player info in transfer response
data class TransferPlayer(
    val id: Int,
    val name: String
)

// Individual transfer record
data class Transfer(
    val date: String,
    val type: String?,
    val teams: TransferTeams
)

// Teams involved in transfer
data class TransferTeams(
    val `in`: TransferTeamInfo,
    val out: TransferTeamInfo
)

// Team information
data class TransferTeamInfo(
    val id: Int,
    val name: String,
    val logo: String
)
