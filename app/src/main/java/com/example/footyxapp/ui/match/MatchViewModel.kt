package com.example.footyxapp.ui.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footyxapp.data.model.FixtureData
import com.example.footyxapp.data.model.FixtureEvent
import com.example.footyxapp.data.model.FixtureTeamStatistics
import com.example.footyxapp.data.model.BetValue
import com.example.footyxapp.data.repository.MatchRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MatchViewModel : ViewModel() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val repository = MatchRepository()
    
    // Live data for fixtures list
    private val _fixtures = MutableLiveData<List<FixtureData>>()
    val fixtures: LiveData<List<FixtureData>> = _fixtures
    
    // Live data for current match details
    private val _currentMatch = MutableLiveData<FixtureData?>()
    val currentMatch: LiveData<FixtureData?> = _currentMatch
    
    // Live data for match statistics
    private val _statistics = MutableLiveData<List<FixtureTeamStatistics>>()
    val statistics: LiveData<List<FixtureTeamStatistics>> = _statistics
    
    // Live data for match events
    private val _events = MutableLiveData<List<FixtureEvent>>()
    val events: LiveData<List<FixtureEvent>> = _events
    
    // Live data for match odds (Match Winner: Home/Draw/Away)
    private val _odds = MutableLiveData<List<BetValue>>()
    val odds: LiveData<List<BetValue>> = _odds
    
    // Live data for head-to-head fixtures
    private val _headToHeadFixtures = MutableLiveData<List<FixtureData>>()
    val headToHeadFixtures: LiveData<List<FixtureData>> = _headToHeadFixtures
    
    // Live data for loading states
    private val _isLoadingFixtures = MutableLiveData<Boolean>()
    val isLoadingFixtures: LiveData<Boolean> = _isLoadingFixtures
    
    private val _isLoadingDetails = MutableLiveData<Boolean>()
    val isLoadingDetails: LiveData<Boolean> = _isLoadingDetails
    
    // Live data for errors
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun searchFixtures(teamName: String) {
        viewModelScope.launch {
            _isLoadingFixtures.value = true
            
            // Get today's date for search (free plan doesn't support 'last' parameter)
            val today = getCurrentDate()
            
            repository.getFixturesByDate(today).fold(
                onSuccess = { response ->
                    val filtered = response.response.filter {
                        it.teams.home.name.contains(teamName, ignoreCase = true) ||
                        it.teams.away.name.contains(teamName, ignoreCase = true)
                    }
                    _fixtures.value = filtered
                    _isLoadingFixtures.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _fixtures.value = emptyList()
                    _isLoadingFixtures.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadLiveMatches() {
        viewModelScope.launch {
            _isLoadingFixtures.value = true
            
            repository.getLiveFixtures().fold(
                onSuccess = { response ->
                    _fixtures.value = response.response
                    _isLoadingFixtures.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _fixtures.value = emptyList()
                    _isLoadingFixtures.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadMatchesByDate(date: String = getCurrentDate()) {
        viewModelScope.launch {
            _isLoadingFixtures.value = true
            
            repository.getFixturesByDate(date).fold(
                onSuccess = { response ->
                    _fixtures.value = response.response
                    _isLoadingFixtures.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _fixtures.value = emptyList()
                    _isLoadingFixtures.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadMatchDetails(fixtureId: Int) {
        viewModelScope.launch {
            _isLoadingDetails.value = true
            
            // Clear previous data before loading new fixture
            clearMatchData()
            
            // Load fixture details
            repository.getFixtureById(fixtureId).fold(
                onSuccess = { response ->
                    val fixture = response.response.firstOrNull()
                    _currentMatch.value = fixture
                    
                    // Load head-to-head if fixture has teams
                    fixture?.let {
                        loadHeadToHead(it.teams.home.id, it.teams.away.id)
                    }
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
            
            // Load statistics
            repository.getFixtureStatistics(fixtureId).fold(
                onSuccess = { response ->
                    _statistics.value = response.response
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _statistics.value = emptyList()
                }
            )
            
            // Load events
            repository.getFixtureEvents(fixtureId).fold(
                onSuccess = { response ->
                    _events.value = response.response
                    _isLoadingDetails.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message
                    _events.value = emptyList()
                    _isLoadingDetails.value = false
                }
            )
            
            // Load odds (Match Winner)
            repository.getFixtureOdds(fixtureId).fold(
                onSuccess = { response ->
                    // Extract Match Winner odds (Bet ID = 1)
                    val matchWinnerOdds = response.response
                        .firstOrNull()
                        ?.bookmakers
                        ?.firstOrNull()
                        ?.bets
                        ?.firstOrNull { it.id == 1 }
                        ?.values
                    _odds.value = matchWinnerOdds ?: emptyList()
                },
                onFailure = { exception ->
                    // Odds not critical, just set empty
                    _odds.value = emptyList()
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearError() {
        _error.value = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearMatchData() {
        _currentMatch.value = null
        _statistics.value = emptyList()
        _events.value = emptyList()
        _odds.value = emptyList()
        _headToHeadFixtures.value = emptyList()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun loadHeadToHead(homeTeamId: Int, awayTeamId: Int) {
        viewModelScope.launch {
            repository.getHeadToHeadFixtures(homeTeamId, awayTeamId, last = 5).fold(
                onSuccess = { response ->
                    // Filter out the current fixture if it's in the list
                    val h2hFixtures = response.response.filter { 
                        it.fixture.id != _currentMatch.value?.fixture?.id 
                    }
                    _headToHeadFixtures.value = h2hFixtures
                },
                onFailure = { exception ->
                    // H2H not critical, just set empty
                    _headToHeadFixtures.value = emptyList()
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}