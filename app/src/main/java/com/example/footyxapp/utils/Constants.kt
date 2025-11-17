package com.example.footyxapp.utils

import com.example.footyxapp.BuildConfig

object Constants {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Football API loaded from BuildConfig
    val API_KEY = BuildConfig.FOOTBALL_API_KEY
    
    // Football API base configuration
    const val BASE_URL = "https://v3.football.api-sports.io/"
    const val API_HOST = "v3.football.api-sports.io"
    
    // Default values
    const val DEFAULT_SEASON = 2023

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
