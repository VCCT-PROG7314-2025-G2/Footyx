package com.example.footyxapp.data.model

data class NotificationPreferences(
    val pushNotificationsEnabled: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "pushNotificationsEnabled" to pushNotificationsEnabled
        )
    }
    
    companion object {
        fun fromMap(map: Map<String, Any>?): NotificationPreferences {
            return if (map != null) {
                NotificationPreferences(
                    pushNotificationsEnabled = map["pushNotificationsEnabled"] as? Boolean ?: false
                )
            } else {
                NotificationPreferences()
            }
        }
    }
}

