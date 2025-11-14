package com.example.footyxapp

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import com.example.footyxapp.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.media3.common.util.UnstableApi
import androidx.credentials.exceptions.ClearCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import com.example.footyxapp.data.classes.User
import com.example.footyxapp.data.model.UserViewModel
import com.example.footyxapp.databinding.SettingsActivityBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    // Login Related Vars
    private lateinit var inputEmail : EditText
    private lateinit var inputPassword : EditText
    private lateinit var registerButton : Button
    private lateinit var userViewModel: UserViewModel
    private lateinit var user : User


    //Google Sign-In Option & Request
    private val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId("538104946492-rnfeh3iac5pl60mioig7va4h2l01osp9.apps.googleusercontent.com")
        .setFilterByAuthorizedAccounts(false)
        .build()

    // Create the credential manager request
    private val credentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()


    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Checks if user is logged in
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val uid = sharedPreferences.getString("user_uid",null)
        if(uid != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
        //Binding Initialization
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //---------------------------------------------------------------------------------------------------------------------------------------//
        // Initialize Views
        //---------------------------------------------------------------------------------------------------------------------------------------//
        inputEmail = findViewById<EditText>(R.id.login_email)
        inputPassword = findViewById<EditText>(R.id.login_password)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Initiate Firebase Variables
        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)

        // Sign In With Google
        binding.loginGoogleSignInBtn.setOnClickListener{
            startCredentialSignIn()

        }
        binding.loginRegisterBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginSignInBttn.setOnClickListener {
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            // Check input validation
            if (validateInput(email, password)) {

                // Validate Login
                userViewModel.validateLogin(email, password).observe(this) { user ->
                    if (user != null) {
                        Toast.makeText(this, "Login successful! Welcome ${user.name}", Toast.LENGTH_SHORT).show()

                        // Ensure user ID is saved
                        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putBoolean("is_logged_in", true)
                            putString("user_uid", user.uid.toString())
                            apply()
                        }

                        // Initialize FCM token for notifications
                        com.example.footyxapp.utils.NotificationTokenManager.initializeTokenForUser(
                            this@LoginActivity,
                            user.uid
                        )

                        // Start MainActivity after login is successful
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        // Close login activity
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        // Biometric Login button
        binding.loginBiometric.setOnClickListener {
            showBiometricPrompt()
        }

    }
    //________________________________________________________ Google Sign in Related____________________________________
    @OptIn(UnstableApi::class)
    private fun startCredentialSignIn(){
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = credentialRequest,
                    context = this@LoginActivity
                )
                handleSignIn(result.credential)
            }catch (e: GetCredentialCancellationException){
                Log.w(TAG,"User cancelled sign-in")

            }catch (e: GetCredentialInterruptedException){
                Log.w(TAG,"Sign-in interrupted")

            }catch (e: GetCredentialException){
                Log.w(TAG,"getCredential failed: ${e.localizedMessage}")

            }catch (e: Exception){
                Log.e(TAG, "Unexpected error during credential request")
            }
        }
    }
    // Handle the SignIn
    @androidx.annotation.OptIn(UnstableApi::class)
    private fun handleSignIn(credential: Credential){
        // Check if credential is of type Google ID
        if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        }else{
            Log.w(TAG, "Credential is not of type Google")
        }
    }
    // Authenticate with Firebase using the Firebase credential
    @androidx.annotation.OptIn(UnstableApi::class)
    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful){
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    user?.let {
                        val db = FirebaseFirestore.getInstance()
                        val userRef = db.collection("users").document(user.uid)

                        val userData = mapOf(
                            "uid" to user.uid,
                            "name" to (user.displayName ?: ""),
                            "email" to (user.email ?: ""),
                            "photoUrl" to (user.photoUrl?.toString()?: ""),
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                        // Create doc if it does not exist
                        userRef.get().addOnSuccessListener { doc ->
                            if(!doc.exists()){
                                userRef.set(userData)
                            }
                        }
                    }
                    val prefs = this.getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit().apply{
                        if (user != null) {
                            putString("userName", user.displayName)
                            putString("userEmail", user.email)
                            putString("user_uid", user.uid.toString())
                        }
                       apply()
                    }
                    
                    // Initialize FCM token for notifications
                    if (user != null) {
                        com.example.footyxapp.utils.NotificationTokenManager.initializeTokenForUser(
                            this@LoginActivity,
                            user.uid
                        )
                    }
                    
                    updateUI(user)
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun navigateToHome(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    // Update the UI based on User status
    @androidx.annotation.OptIn(UnstableApi::class)
    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            Toast.makeText(this,"Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }else{
            Toast.makeText(this,"Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    // Validate Login input
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            inputEmail.error = "Email cannot be empty"
            return false
        }

        if (password.isEmpty()) {
            inputPassword.error = "Password cannot be empty"
            return false
        }

        return true
    }
//________________________________________________________ Google Sign in Related END____________________________________
    //-------------------------------------------------------------------------------------------------------------------------------
    // Biometric Related
    //-------------------------------------------------------------------------------------------------------------------------------

    // Biometric Authentication Setup
    private fun showBiometricPrompt(){
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    handleBiometricLogin()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@LoginActivity, "Biometric error: $errString", Toast.LENGTH_SHORT).show()
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Authentication to Sign In to Footyx")
            .setNegativeButtonText("Cancel")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    // HandleBiometric Login
    private fun handleBiometricLogin(){
        auth.signInAnonymously()
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                val uid = user?.uid ?: return@addOnSuccessListener

                val db = FirebaseFirestore.getInstance()
                val userDoc = hashMapOf(
                    "uid" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "email" to null,
                    "name" to null,
                    "password" to null,
                    "photoUrl" to null
                )
                // Create user document if it does not exist
                db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        if(!snapshot.exists()){
                            db.collection("users").document(uid).set(userDoc)
                        }
                    }
                // Save locally
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit().apply{
                    putBoolean("is_logged_in", true)
                    putString("user_uid",uid)
                    apply()
                }

                Toast.makeText(this, "Logged in with Biometrics", Toast.LENGTH_SHORT).show()
                navigateToHome()

            }
            .addOnFailureListener { Toast.makeText(this, "Biometric Login failed", Toast.LENGTH_SHORT).show() }
    }

}
