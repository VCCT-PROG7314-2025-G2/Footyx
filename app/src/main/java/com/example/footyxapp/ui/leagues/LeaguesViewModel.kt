package com.example.footyxapp.ui.leagues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footyxapp.data.model.LeagueData
import com.example.footyxapp.data.model.TeamStanding
import com.example.footyxapp.data.repository.LeagueRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LeaguesViewModel : ViewModel() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val repository = LeagueRepository()
    
    // Live data for league search results
    private val _searchResults = MutableLiveData<List<LeagueData>>()
    val searchResults: LiveData<List<LeagueData>> = _searchResults
    
    // Live data for standings
    private val _standings = MutableLiveData<List<TeamStanding>>()
    val standings: LiveData<List<TeamStanding>> = _standings
    
    // Live data for current league info
    private val _currentLeague = MutableLiveData<Pair<String, Int>>() // League name and season
    val currentLeague: LiveData<Pair<String, Int>> = _currentLeague
    
    // Live data for loading states
    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching
    
    private val _isLoadingStandings = MutableLiveData<Boolean>()
    val isLoadingStandings: LiveData<Boolean> = _isLoadingStandings
    
    // Live data for errors
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // Search job for cancellation
    private var searchJob: Job? = null

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun searchLeagues(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        // Cancel previous search
        searchJob?.cancel()
        
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            
            // Add delay for debouncing
            delay(300)
            
            repository.searchLeagues(query).fold(
                onSuccess = { response ->
                    _searchResults.value = response.response
                    _isSearching.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _searchResults.value = emptyList()
                    _isSearching.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadStandings(leagueId: Int, leagueName: String, season: Int) {
        viewModelScope.launch {
            _isLoadingStandings.value = true
            _currentLeague.value = Pair(leagueName, season)
            
            repository.getStandings(leagueId, season).fold(
                onSuccess = { response ->
                    // The API returns standings grouped (e.g., Champions League has multiple groups)
                    // Flatten all groups into a single list
                    val allStandings = response.response.firstOrNull()?.league?.standings?.flatten() ?: emptyList()
                    _standings.value = allStandings
                    _isLoadingStandings.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _standings.value = emptyList()
                    _isLoadingStandings.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearSearchResults() {
        searchJob?.cancel()
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearError() {
        _error.value = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}