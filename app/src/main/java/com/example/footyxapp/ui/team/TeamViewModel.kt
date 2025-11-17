package com.example.footyxapp.ui.team

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footyxapp.data.model.*
import com.example.footyxapp.data.repository.TeamRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val repository = TeamRepository()
    
    // Live data for team search results
    private val _searchResults = MutableLiveData<List<TeamData>>()
    val searchResults: LiveData<List<TeamData>> = _searchResults
    
    // Live data for team leagues
    private val _teamLeagues = MutableLiveData<List<TeamLeagueData>>()
    val teamLeagues: LiveData<List<TeamLeagueData>> = _teamLeagues
    
    // Live data for team statistics
    private val _teamStatistics = MutableLiveData<TeamStatistics>()
    val teamStatistics: LiveData<TeamStatistics> = _teamStatistics
    
    // Live data for loading states
    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching
    
    private val _isLoadingLeagues = MutableLiveData<Boolean>()
    val isLoadingLeagues: LiveData<Boolean> = _isLoadingLeagues
    
    private val _isLoadingStatistics = MutableLiveData<Boolean>()
    val isLoadingStatistics: LiveData<Boolean> = _isLoadingStatistics
    
    // Live data for errors
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Search job for cancellation
    private var searchJob: Job? = null

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun searchTeams(query: String) {
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
            
            repository.searchTeams(query).fold(
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

    fun loadTeamLeagues(teamId: Int, season: Int) {
        viewModelScope.launch {
            _isLoadingLeagues.value = true
            
            repository.getTeamLeagues(teamId, season).fold(
                onSuccess = { response ->
                    _teamLeagues.value = response.response
                    _isLoadingLeagues.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoadingLeagues.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadTeamStatistics(teamId: Int, leagueId: Int, season: Int) {
        viewModelScope.launch {
            _isLoadingStatistics.value = true
            
            repository.getTeamStatistics(teamId, leagueId, season).fold(
                onSuccess = { response ->
                    _teamStatistics.value = response.response
                    _isLoadingStatistics.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _isLoadingStatistics.value = false
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
        _error.value = "error"
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}