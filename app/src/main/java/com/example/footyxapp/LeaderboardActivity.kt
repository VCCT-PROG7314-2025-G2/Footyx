package com.example.footyxapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.footyxapp.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLeaderboardBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide the default action bar since we have our own header
        supportActionBar?.hide()
        
        // Set up back button click listener
        binding.btnBack.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }
    }
}
