package com.example.footyxapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Set up the Get Started button click listener
        val getStartedButton = findViewById<Button>(R.id.splash_get_started_button)
        getStartedButton.setOnClickListener {
            // Navigate to LoginActivity when Get Started is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the splash screen so user can't go back to it
        }
    }
}
