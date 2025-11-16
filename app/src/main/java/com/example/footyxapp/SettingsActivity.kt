package com.example.footyxapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.footyxapp.LoginActivity
import com.example.footyxapp.databinding.SettingsActivityBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlin.math.sign
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private lateinit var binding: SettingsActivityBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var logoutButton: Button
    private lateinit var editButton : Button

    // Add Shared Preference
    private val prefs by lazy  {
        getSharedPreferences("settings_prefs", MODE_PRIVATE)
    }

    //Google Sign-In Option & Request
    private val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId("538104946492-rnfeh3iac5pl60mioig7va4h2l01osp9.apps.googleusercontent.com")
        .setFilterByAuthorizedAccounts(false)
        .build()

    // Create the credential manager request
    private val credentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //---------------------------------------------------------------------------------------------------------------------------------------//
        // Initialize Views
        //---------------------------------------------------------------------------------------------------------------------------------------//
        logoutButton = findViewById(R.id.btn_logout)
        editButton = findViewById(R.id.edit_user_btn)


        // Initiate Firebase Variables
        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)

        // Load saved settings
        loadSettings()
        setupSwitchListener()
        setupLanguageListener()

        // Hide the default action bar since we have our own header
        supportActionBar?.hide()

        // Set up back button click listener
        binding.btnBack.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }

        // Set up navigation to favorites activity
        setupFavoriteTeamNavigation()

        //
        logoutButton.setOnClickListener {
            performLogout()
        }

        //Redirect to Edit User Details
        editButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupFavoriteTeamNavigation() {
        // Find the favorite team LinearLayout and set click listener
        val favoriteTeamLayout = binding.root.findViewById<android.widget.LinearLayout>(
            R.id.layout_favorite_team
        )

        favoriteTeamLayout?.setOnClickListener {
            // Navigate to FavoritesActivity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Perform logout and then navigate to login
    private fun performLogout(){
        lifecycleScope.launch {
            val logoutSuccessful = signOut()

            if(logoutSuccessful){
                Toast.makeText(this@SettingsActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Navigate to Login
                startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                finish()
            }else{
                Toast.makeText(this@SettingsActivity, "Error during logout", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // SignOut
    @androidx.annotation.OptIn(UnstableApi::class)
    private suspend fun signOut(): Boolean {

        // When a user signs out, clear the current user credential state from all credential credential providers
        return try {
            // Firebase sign out
            auth.signOut()

            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)

            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            sharedPreferences.edit().remove("user_uid").apply()

            true
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            false
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Methods to Manage Settings manipulated by User
    // Load all settings options
    private fun loadSettings() {
        // Switched
        binding.switchMatchReminders.isChecked = prefs.getBoolean("match_reminders", true)
        binding.switchGoalsAlerts.isChecked = prefs.getBoolean("goals_alerts", true)
        binding.switchFinalScoreAlerts.isChecked = prefs.getBoolean("final_score_alerts", true)
        binding.switchBiometricAuth.isChecked = prefs.getBoolean("biometric_enabled",false)

        // Language Radio Button
        val savedLanguage = prefs.getString("language","english")
        when(savedLanguage){
            "english" -> binding.radioEnglish.isChecked = true
            "afrikaans" -> binding.radioAfrikaans.isChecked = true
        }
    }
    // Edit All settings options
    private fun setupSwitchListener(){
        binding.switchBiometricAuth.setOnCheckedChangeListener { _, isChecked -> prefs.edit() {
            putBoolean(
                "biometric_enabled",
                isChecked
            ) }
        }
        binding.switchGoalsAlerts.setOnCheckedChangeListener { _, isChecked -> prefs.edit() {
            putBoolean(
                "goals_alerts",
                isChecked
            ) }
        }
        binding.switchFinalScoreAlerts.setOnCheckedChangeListener { _, isChecked -> prefs.edit() {
            putBoolean(
                "final_score_alerts",
                isChecked
            ) }
        }
        binding.switchMatchReminders.setOnCheckedChangeListener { _, isChecked -> prefs.edit() {
            putBoolean(
                "match_reminders",
                isChecked
            ) }
        }
    }
    // Save Selected Language
    private fun setupLanguageListener(){
        binding.radioEnglish.setOnCheckedChangeListener { _, isChecked -> prefs.edit() {
            if(isChecked){
                putString(
                    "language",
                    "english"
                ).apply() }
            }

        }
        binding.radioAfrikaans.setOnCheckedChangeListener { _, isChecked -> prefs.edit() {
            if(isChecked){
                putString(
                    "language",
                    "afrikaans"
                ).apply() }
            }

        }
    }

}