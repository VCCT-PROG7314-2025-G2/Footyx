package com.example.footyxapp.data.classes

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context){
    private val appContext = context.applicationContext

    companion object{
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        // Language Codes
        const val ENGLISH = "en"
        const val AFRIKAANS = "af"
    }
    val getLanguage : Flow<String?> = appContext.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY]
    }

    suspend fun  saveLanguage(languageCode: String){
        appContext.dataStore.edit{preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

}