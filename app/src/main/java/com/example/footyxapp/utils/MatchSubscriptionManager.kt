package com.example.footyxapp.utils

import android.content.Context
import android.util.Log
import com.example.footyxapp.data.model.MatchSubscription
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

object MatchSubscriptionManager {
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private const val TAG = "MatchSubscriptionManager"
    private val firestore = FirebaseFirestore.getInstance()
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun subscribeToMatch(
        userId: String,
        matchId: String,
        teamId: String,
        teamName: String,
        matchDate: Date,
        reminderTime: Long = 30,
        onComplete: (Boolean) -> Unit
    ) {
        val subscription = MatchSubscription(
            matchId = matchId,
            teamId = teamId,
            teamName = teamName,
            matchDate = matchDate,
            reminderTime = reminderTime
        )
        
        firestore.collection("users")
            .document(userId)
            .collection("subscribedMatches")
            .document(matchId)
            .set(subscription.toMap(), SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Match subscribed: $matchId for user: $userId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error subscribing to match", e)
                onComplete(false)
            }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun unsubscribeFromMatch(
        userId: String,
        matchId: String,
        onComplete: (Boolean) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("subscribedMatches")
            .document(matchId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Match unsubscribed: $matchId for user: $userId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error unsubscribing from match", e)
                onComplete(false)
            }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun getSubscribedMatches(
        userId: String,
        onComplete: (List<MatchSubscription>) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("subscribedMatches")
            .get()
            .addOnSuccessListener { documents ->
                val subscriptions = documents.mapNotNull { doc ->
                    MatchSubscription.fromMap(doc.data)
                }
                Log.d(TAG, "Retrieved ${subscriptions.size} subscribed matches for user: $userId")
                onComplete(subscriptions)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting subscribed matches", e)
                onComplete(emptyList())
            }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun subscribeToTeamMatches(
        userId: String,
        teamId: String,
        teamName: String,
        matches: List<Pair<String, Date>>, // List of (matchId, matchDate) pairs
        reminderTime: Long = 30,
        onComplete: (Int) -> Unit
    ) {
        var successCount = 0
        var completedCount = 0
        
        if (matches.isEmpty()) {
            onComplete(0)
            return
        }
        
        matches.forEach { (matchId, matchDate) ->
            subscribeToMatch(userId, matchId, teamId, teamName, matchDate, reminderTime) { success ->
                if (success) {
                    successCount++
                }
                completedCount++
                
                if (completedCount == matches.size) {
                    Log.d(TAG, "Subscribed to $successCount/${matches.size} matches for team: $teamName")
                    onComplete(successCount)
                }
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun unsubscribeFromTeamMatches(
        userId: String,
        teamId: String,
        onComplete: (Int) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("subscribedMatches")
            .whereEqualTo("teamId", teamId)
            .get()
            .addOnSuccessListener { documents ->
                var successCount = 0
                var completedCount = 0
                val totalCount = documents.size()
                
                if (totalCount == 0) {
                    onComplete(0)
                    return@addOnSuccessListener
                }
                
                documents.forEach { doc ->
                    unsubscribeFromMatch(userId, doc.id) { success ->
                        if (success) {
                            successCount++
                        }
                        completedCount++
                        
                        if (completedCount == totalCount) {
                            Log.d(TAG, "Unsubscribed from $successCount/$totalCount matches for team: $teamId")
                            onComplete(successCount)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error unsubscribing from team matches", e)
                onComplete(0)
            }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun isSubscribedToMatch(
        userId: String,
        matchId: String,
        onComplete: (Boolean) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("subscribedMatches")
            .document(matchId)
            .get()
            .addOnSuccessListener { doc ->
                onComplete(doc.exists())
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking subscription", e)
                onComplete(false)
            }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
}

