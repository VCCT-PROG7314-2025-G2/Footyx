package com.example.footyxapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.*
import com.example.footyxapp.R
import com.example.footyxapp.data.model.MatchSubscription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

object MatchReminderScheduler {
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private const val TAG = "MatchReminderScheduler"
    private const val WORK_NAME_PERIODIC = "match_reminder_periodic"
    private const val WORK_NAME_MATCH_REMINDER = "match_reminder_"
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun schedulePeriodicWork(context: Context, userId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<MatchReminderWorker>(
            15, TimeUnit.MINUTES, // Check every 15 minutes
            5, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    "userId" to userId
                )
            )
            .addTag(WORK_NAME_PERIODIC)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        
        Log.d(TAG, "Periodic match reminder work scheduled")
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun scheduleMatchReminder(
        context: Context,
        subscription: MatchSubscription,
        userId: String
    ) {
        val matchDate = subscription.matchDate
        val reminderTime = subscription.reminderTime
        val reminderDate = Date(matchDate.time - (reminderTime * 60 * 1000)) // Convert minutes to milliseconds
        
        val now = Date()
        val delay = reminderDate.time - now.time
        
        if (delay <= 0) {
            Log.d(TAG, "Match reminder time has passed for match: ${subscription.matchId}")
            return
        }
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<MatchReminderNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    "userId" to userId,
                    "matchId" to subscription.matchId,
                    "teamName" to subscription.teamName,
                    "matchDate" to matchDate.time
                )
            )
            .addTag(WORK_NAME_MATCH_REMINDER + subscription.matchId)
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME_MATCH_REMINDER + subscription.matchId,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        Log.d(TAG, "Match reminder scheduled for match: ${subscription.matchId} at ${reminderDate}")
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun cancelMatchReminder(context: Context, matchId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_MATCH_REMINDER + matchId)
        Log.d(TAG, "Match reminder cancelled for match: $matchId")
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME_PERIODIC)
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_PERIODIC)
        Log.d(TAG, "All match reminders cancelled")
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    class MatchReminderWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {
        
        override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
            val userId = inputData.getString("userId") ?: return@withContext Result.failure()
            
            return@withContext try {
                // Get all subscribed matches synchronously
                val subscriptions = getSubscribedMatchesSync(userId)
                val now = Date()
                val futureDate = Date(now.time + (24 * 60 * 60 * 1000)) // 24 hours from now
                
                // Filter matches in the next 24 hours
                val upcomingMatches = subscriptions.filter { subscription ->
                    val matchDate = subscription.matchDate
                    matchDate.after(now) && matchDate.before(futureDate)
                }
                
                // Schedule reminders for upcoming matches
                upcomingMatches.forEach { subscription ->
                    scheduleMatchReminder(applicationContext, subscription, userId)
                }
                
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Error in MatchReminderWorker", e)
                Result.retry()
            }
        }
        
        private suspend fun getSubscribedMatchesSync(userId: String): List<MatchSubscription> {
            return suspendCancellableCoroutine { continuation ->
                MatchSubscriptionManager.getSubscribedMatches(userId) { subscriptions ->
                    continuation.resume(subscriptions)
                }
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    class MatchReminderNotificationWorker(
        context: Context,
        params: WorkerParameters
    ) : CoroutineWorker(context, params) {
        
        override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
            val userId = inputData.getString("userId") ?: return@withContext Result.failure()
            val matchId = inputData.getString("matchId") ?: return@withContext Result.failure()
            val teamName = inputData.getString("teamName") ?: return@withContext Result.failure()
            
            return@withContext try {
                // Check if push notifications are enabled
                val prefs = applicationContext.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
                val pushNotificationsEnabled = prefs.getBoolean("push_notifications_enabled", false)
                
                if (pushNotificationsEnabled) {
                    // Show match reminder notification
                    NotificationHelper.showMatchReminderNotification(
                        context = applicationContext,
                        notificationId = matchId.hashCode(),
                        title = applicationContext.getString(R.string.notification_match_reminder_title),
                        message = "$teamName match is starting soon!",
                        matchId = matchId
                    )
                }
                
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Error in MatchReminderNotificationWorker", e)
                Result.failure()
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
}

