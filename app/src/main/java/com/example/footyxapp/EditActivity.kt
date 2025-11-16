package com.example.footyxapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.footyxapp.data.classes.User
import com.example.footyxapp.data.model.UserViewModel
import com.example.footyxapp.databinding.ActivityEditBinding
import com.example.footyxapp.databinding.ActivityLoginBinding
import com.example.footyxapp.databinding.ActivityMainBinding
import com.example.footyxapp.databinding.ActivityRegisterBinding
import com.example.footyxapp.utils.UserSyncWorker
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding

    // Register  Related Vars
    private lateinit var inputEmail : EditText
    private lateinit var inputName : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputConfirmPassword : EditText
    private lateinit var userViewModel: UserViewModel
    private lateinit var editButton: Button
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //---------------------------------------------------------------------------------------------------------------------------------------//
        // Initialize Views
        //---------------------------------------------------------------------------------------------------------------------------------------//
        inputName = findViewById<EditText>(R.id.edit_username)
        inputEmail = findViewById<EditText>(R.id.edit_email)
        inputPassword = findViewById<EditText>(R.id.edit_password)
        inputConfirmPassword = findViewById<EditText>(R.id.edit_confirm_password)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        editButton = findViewById(R.id.edit_submit_btn)

        //---------------------------------------------------------------------------------------------------------------
        // Buttons Logic
        //-------------------------------------------------------------------------------------------------------------------

        editButton.setOnClickListener {
            val rawName = inputName.text.toString().trim()
            val rawEmail = inputEmail.text.toString().trim()
            val rawPassword = inputPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            val name = if (rawName.isNotEmpty()) rawName else null
            val email = if (rawEmail.isNotEmpty()) rawEmail.lowercase() else null
            val password = if (rawPassword.isNotEmpty()) rawPassword else null

            if (validateInput(name, email, password, confirmPassword)) {
                val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val uid = sharedPrefs.getString("user_uid", null)

                if (uid.isNullOrEmpty()) {
                    Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val authUser = FirebaseAuth.getInstance().currentUser
                if (!email.isNullOrBlank()) {
                    authUser?.updateEmail(email)
                }
                if (!password.isNullOrBlank()) {
                    authUser?.updatePassword(password)
                }

                userViewModel.updateUser(uid, name, email, password).observe(this) { success ->
                    if (success == true) {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        // Fallback: save offline and schedule sync
                        val user = User(uid = uid, email = email ?: "", password = password ?: "", name = name ?: "")
                        savePendingUpdate(user)
                        scheduleProfileSync()
                        Toast.makeText(this, "Saved locally. Will sync when online.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
    // Save fields locally so the app can sync them later
    private fun savePendingUpdate(user: User){
        val prefs = getSharedPreferences("local_user", Context.MODE_PRIVATE)
        prefs.edit().apply(){
            putString("pending_name", user.name)
            putString("pending_email", user.email)
            putString("pending_password", user.password)
            putBoolean("hasPendingUpdate", true)
            apply()
        }
    }
    // WorkManaget request to sync when internet is available
    private fun scheduleProfileSync(){
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<UserSyncWorker>()
            .setConstraints(constrains)
            .build()

        WorkManager.getInstance(this).enqueue(syncRequest)
    }

    private fun validateInput(fullName: String?, email: String?, password: String?, confirmPassword: String): Boolean {
        if (!password.isNullOrEmpty()) {
            if (confirmPassword.isEmpty()) {
                inputConfirmPassword.error = "Confirm Password cannot be empty"
                return false
            }
            if (confirmPassword != password) {
                inputConfirmPassword.error = "Please confirm the password"
                return false
            }
        }
        if (!email.isNullOrEmpty()) {
            val emailRegex = "^.+@.+\\..+$".toRegex()
            if (!emailRegex.matches(email)) {
                inputEmail.error = "Invalid email format"
                return false
            }
        }
        return true
    }

}