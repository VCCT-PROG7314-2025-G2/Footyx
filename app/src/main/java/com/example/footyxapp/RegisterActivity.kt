package com.example.footyxapp

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
import com.example.footyxapp.data.classes.User
import com.example.footyxapp.data.model.UserViewModel
import com.example.footyxapp.databinding.ActivityLoginBinding
import com.example.footyxapp.databinding.ActivityMainBinding
import com.example.footyxapp.databinding.ActivityRegisterBinding
import java.util.UUID
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    // Register  Related Vars
    private lateinit var inputEmail : EditText
    private lateinit var inputName : EditText
    private lateinit var inputPassword : EditText
    private lateinit var inputConfirmPassword : EditText
    private lateinit var userViewModel: UserViewModel
    private lateinit var registerButton: Button
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //---------------------------------------------------------------------------------------------------------------------------------------//
        // Initialize Views
        //---------------------------------------------------------------------------------------------------------------------------------------//
        inputName = findViewById<EditText>(R.id.register_username)
        inputEmail = findViewById<EditText>(R.id.register_email)
        inputPassword = findViewById<EditText>(R.id.register_password)
        inputConfirmPassword = findViewById<EditText>(R.id.register_confirm_password)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        registerButton = findViewById(R.id.register_sign_up_btn)

        //---------------------------------------------------------------------------------------------------------------
        // Buttons Logic
        //-------------------------------------------------------------------------------------------------------------------

        registerButton.setOnClickListener {
            val name = inputName.text.toString().trim()
            val email = inputEmail.text.toString().trim().lowercase()
            val password = inputPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            if (validateInput(name, email, password, confirmPassword)) {
                val auth = Firebase.auth
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val uid = currentUser?.uid ?: UUID.randomUUID().toString()
                            val db = FirebaseFirestore.getInstance()
                            val userData = mapOf(
                                "uid" to uid,
                                "email" to email,
                                "name" to name,
                                "password" to password,
                                "photoUrl" to "",
                                "createdAt" to FieldValue.serverTimestamp()
                            )
                            db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "User registered succesfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, task.exception?.localizedMessage ?: "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }

    private fun validateInput(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        if (fullName.isEmpty()) {
            inputName.error = "Name cannot be empty"
            return false
        }

        if (email.isEmpty()) {
            inputEmail.error = "Email cannot be empty"
            return false
        }

        if (password.isEmpty()) {
            inputPassword.error = "Password cannot be empty"
            return false
        }

        if (confirmPassword.isEmpty()) {
            inputConfirmPassword.error = "Confirm Password cannot be empty"
            return false
        }
        if(confirmPassword != password){
            inputConfirmPassword.error = "Please confirm the password"
            return false
        }

        return true
    }

}