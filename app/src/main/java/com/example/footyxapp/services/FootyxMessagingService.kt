package com.example.footyxapp.services

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.footyxapp.R
import com.example.footyxapp.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FootyxMessagingService : FirebaseMessagingService() {
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private val TAG = "FootyxMessagingService"
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "From: ${remoteMessage.from}")
        
        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        
        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            handleNotificationMessage(it, remoteMessage.data)
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private fun handleDataMessage(data: Map<String, String>) {
        val notificationType = data["type"] ?: return
        val matchId = data["matchId"] ?: ""
        val title = data["title"] ?: ""
        val message = data["message"] ?: ""
        
        // Check if push notifications are enabled
        val prefs = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val pushNotificationsEnabled = prefs.getBoolean("push_notifications_enabled", false)
        
        if (!pushNotificationsEnabled) {
            Log.d(TAG, "Push notifications are disabled, ignoring message")
            return
        }
        
        // Check if user is subscribed to this match
        val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = userPrefs.getString("user_uid", null)
        
        if (userId != null && matchId.isNotEmpty()) {
            // Check subscription
            com.example.footyxapp.utils.MatchSubscriptionManager.isSubscribedToMatch(userId, matchId) { isSubscribed ->
                if (isSubscribed) {
                    when (notificationType) {
                        "goal_alert" -> {
                            showGoalAlert(title, message, matchId)
                        }
                        "final_score" -> {
                            showFinalScore(title, message, matchId)
                        }
                        else -> {
                            Log.d(TAG, "Unknown notification type: $notificationType")
                        }
                    }
                } else {
                    Log.d(TAG, "User not subscribed to match: $matchId")
                }
            }
        } else {
            // If no userId or matchId, show notification anyway (for testing)
            when (notificationType) {
                "goal_alert" -> {
                    showGoalAlert(title, message, matchId)
                }
                "final_score" -> {
                    showFinalScore(title, message, matchId)
                }
                else -> {
                    Log.d(TAG, "Unknown notification type: $notificationType")
                }
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private fun handleNotificationMessage(
        notification: com.google.firebase.messaging.RemoteMessage.Notification,
        data: Map<String, String>
    ) {
        val notificationType = data["type"] ?: "default"
        val matchId = data["matchId"] ?: ""
        val title = notification.title ?: getString(R.string.app_name)
        val message = notification.body ?: ""
        
        // Check if push notifications are enabled
        val prefs = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        val pushNotificationsEnabled = prefs.getBoolean("push_notifications_enabled", false)
        
        if (!pushNotificationsEnabled) {
            Log.d(TAG, "Push notifications are disabled, ignoring message")
            return
        }
        
        // Check if user is subscribed to this match
        val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = userPrefs.getString("user_uid", null)
        
        if (userId != null && matchId.isNotEmpty()) {
            // Check subscription
            com.example.footyxapp.utils.MatchSubscriptionManager.isSubscribedToMatch(userId, matchId) { isSubscribed ->
                if (isSubscribed) {
                    when (notificationType) {
                        "goal_alert" -> {
                            showGoalAlert(title, message, matchId)
                        }
                        "final_score" -> {
                            showFinalScore(title, message, matchId)
                        }
                        else -> {
                            // Default notification
                            NotificationHelper.showGoalAlertNotification(
                                context = this,
                                notificationId = matchId.hashCode(),
                                title = title,
                                message = message,
                                matchId = matchId
                            )
                        }
                    }
                } else {
                    Log.d(TAG, "User not subscribed to match: $matchId")
                }
            }
        } else {
            // If no userId or matchId, show notification anyway (for testing)
            when (notificationType) {
                "goal_alert" -> {
                    showGoalAlert(title, message, matchId)
                }
                "final_score" -> {
                    showFinalScore(title, message, matchId)
                }
                else -> {
                    // Default notification
                    NotificationHelper.showGoalAlertNotification(
                        context = this,
                        notificationId = matchId.hashCode(),
                        title = title,
                        message = message,
                        matchId = matchId
                    )
                }
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private fun showGoalAlert(title: String, message: String, matchId: String) {
        NotificationHelper.showGoalAlertNotification(
            context = this,
            notificationId = matchId.hashCode(),
            title = title.ifEmpty { getString(R.string.notification_goal_alert_title) },
            message = message,
            matchId = matchId
        )
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private fun showFinalScore(title: String, message: String, matchId: String) {
        NotificationHelper.showFinalScoreNotification(
            context = this,
            notificationId = matchId.hashCode(),
            title = title.ifEmpty { getString(R.string.notification_final_score_title) },
            message = message,
            matchId = matchId
        )
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Get user ID from shared preferences
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_uid", null)
        
        if (userId != null) {
            // Send token to Firestore
            com.example.footyxapp.utils.NotificationTokenManager.saveTokenToFirestore(userId, token) { success ->
                if (success) {
                    Log.d(TAG, "Token refreshed and saved to Firestore")
                } else {
                    Log.e(TAG, "Failed to save refreshed token to Firestore")
                }
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
}

