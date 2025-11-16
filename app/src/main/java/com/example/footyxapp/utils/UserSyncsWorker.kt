package com.example.footyxapp.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore

class UserSyncWorker(context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams){

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("local_user", Context.MODE_PRIVATE)
        val userPrefs = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        if(!prefs.getBoolean("hasPendingUpdate", false)){
            return Result.success()
        }
        val name =  prefs.getString("pending_name", "") ?: ""
        val email = prefs.getString("pending_email", "") ?: ""
        val password = prefs.getString("pending_password", "")?:""

        val firestore = FirebaseFirestore.getInstance()
        val userUid = userPrefs.getString("user_uid", "") ?:""

        val data = mutableMapOf<String, Any>()
        if (name.isNotEmpty()) data["name"] = name
        if (email.isNotEmpty()) data["email"] = email.lowercase()
        if (password.isNotEmpty()) data["password"] = password

        if (data.isEmpty()) {
            prefs.edit().putBoolean("hasPendingUpdate",false).apply()
            return Result.success()
        }
        firestore.collection("users")
            .document(userUid)
            .update(data)
            .addOnSuccessListener {
                prefs.edit().putBoolean("hasPendingUpdate",false).apply()
            }
        return Result.success()
    }

}