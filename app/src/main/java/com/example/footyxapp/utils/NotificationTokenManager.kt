package com.example.footyxapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

object NotificationTokenManager {
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private const val TAG = "NotificationTokenManager"
    private const val PREFS_NAME = "footyx_notifications"
    private const val KEY_FCM_TOKEN = "fcm_token"
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun getFCMToken(context: Context, onComplete: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                onComplete(null)
                return@OnCompleteListener
            }
            
            val token = task.result
            Log.d(TAG, "FCM Token: $token")
            
            // Save token locally
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
            
            onComplete(token)
        })
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun saveTokenToFirestore(userId: String, token: String, onComplete: (Boolean) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d(TAG, "FCM token saved to Firestore for user: $userId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving FCM token to Firestore", e)
                onComplete(false)
            }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun initializeTokenForUser(context: Context, userId: String) {
        getFCMToken(context) { token ->
            if (token != null && userId.isNotEmpty()) {
                saveTokenToFirestore(userId, token) { success ->
                    if (success) {
                        Log.d(TAG, "Token initialized for user: $userId")
                    }
                }
            }
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun getStoredToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_FCM_TOKEN).apply()
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
}

