package com.example.footyxapp.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footyxapp.data.model.PlayerData
import com.example.footyxapp.data.model.PlayerProfile
import com.example.footyxapp.data.repository.PlayerRepository
import com.example.footyxapp.utils.Constants
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val repository = PlayerRepository()
    
    private val _playerData = MutableLiveData<PlayerData?>()
    val playerData: LiveData<PlayerData?> = _playerData
    
    private val _searchResults = MutableLiveData<List<PlayerProfile>>()
    val searchResults: LiveData<List<PlayerProfile>> = _searchResults
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Rate limiting
    private var lastSearchQuery = ""
    private var lastSearchTime = 0L
    private val MIN_SEARCH_INTERVAL = 1000L // 1 second between searches

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadPlayer(playerId: Int, season: Int = Constants.DEFAULT_SEASON) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getPlayer(playerId, season)
                .onSuccess { response ->
                    if (response.response.isNotEmpty()) {
                        _playerData.value = response.response[0]
                    } else {
                        _error.value = "No player data found for the selected season"
                    }
                }
                .onFailure { exception ->
                    val errorMessage = when {
                        exception.message?.contains("rate limit", ignoreCase = true) == true -> 
                            "Search limit reached. Please wait a moment before searching again."
                        exception.message?.contains("network", ignoreCase = true) == true -> 
                            "Network error. Please check your connection."
                        else -> "Unable to load player data. Please try again."
                    }
                    _error.value = errorMessage
                }
            
            _isLoading.value = false
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun searchPlayers(query: String) {
        // Rate limiting check
        val currentTime = System.currentTimeMillis()
        if (query == lastSearchQuery && (currentTime - lastSearchTime) < MIN_SEARCH_INTERVAL) {
            return // Skip this search due to rate limiting
        }
        
        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            return
        }
        
        lastSearchQuery = query
        lastSearchTime = currentTime
        
        viewModelScope.launch {
            _isSearching.value = true
            _error.value = null
            
            repository.searchPlayers(query.trim())
                .onSuccess { response ->
                    _searchResults.value = response.response
                }
                .onFailure { exception ->
                    val errorMessage = when {
                        exception.message?.contains("rate limit", ignoreCase = true) == true -> 
                            "Search limit reached. Please wait before searching again."
                        exception.message?.contains("network", ignoreCase = true) == true -> 
                            "Network error. Please check your connection."
                        else -> "Search failed. Please try again."
                    }
                    _error.value = errorMessage
                    _searchResults.value = emptyList()
                }
            
            _isSearching.value = false
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearSearch() {
        _searchResults.value = emptyList()
        _error.value = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearError() {
        _error.value = ""
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}