package com.example.footyxapp.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class MatchSubscription(
    val matchId: String,
    val teamId: String,
    val teamName: String,
    val matchDate: Date,
    val reminderTime: Long = 30, // minutes before match
    val subscribedAt: Date = Date()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "matchId" to matchId,
            "teamId" to teamId,
            "teamName" to teamName,
            "matchDate" to Timestamp(matchDate),
            "reminderTime" to reminderTime,
            "subscribedAt" to Timestamp(subscribedAt)
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>?): MatchSubscription? {
            return if (map != null) {
                try {
                    MatchSubscription(
                        matchId = map["matchId"] as? String ?: "",
                        teamId = map["teamId"] as? String ?: "",
                        teamName = map["teamName"] as? String ?: "",
                        matchDate = (map["matchDate"] as? Timestamp)?.toDate() ?: Date(),
                        reminderTime = (map["reminderTime"] as? Long) ?: 30,
                        subscribedAt = (map["subscribedAt"] as? Timestamp)?.toDate() ?: Date()
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }
}

