package com.example.footyxapp.data.classes

import com.google.firebase.Timestamp


data class User (
    var uid : String? = null,
    var email : String? = null,
    var name: String? = null,
    var password : String? = null,
    var photoUrl: String? = null,
    var createdAt: Timestamp? = null
)