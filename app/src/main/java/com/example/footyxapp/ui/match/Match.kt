package com.example.footyxapp.ui.match

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.ui.common.SearchableFragment
import com.example.footyxapp.ui.match.adapter.EventsAdapter
import com.example.footyxapp.ui.match.adapter.FixtureAdapter
import com.example.footyxapp.databinding.FragmentMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class Match : Fragment(), SearchableFragment {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var matchViewModel: MatchViewModel
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var fixtureAdapter: FixtureAdapter
    private lateinit var h2hAdapter: FixtureAdapter
    private var isSearchMode = false
    
    // Auto-refresh for live matches
    private val refreshHandler = Handler(Looper.getMainLooper())
    private var currentFixtureId: Int? = null
    private var isLiveMatch = false
    private val REFRESH_INTERVAL = 60000L // 1 minute in milliseconds
    
    private val refreshRunnable = object : Runnable {
        override fun run() {
            if (isLiveMatch && currentFixtureId != null) {
                matchViewModel.loadMatchDetails(currentFixtureId!!)
                refreshHandler.postDelayed(this, REFRESH_INTERVAL)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        matchViewModel = ViewModelProvider(this)[MatchViewModel::class.java]
        
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        setupRecyclerView()
        setupObservers()
        setupFixtureSearch()
        
        // Load a demo match (fixture ID 1035086 - example from API docs)
        matchViewModel.loadMatchDetails(1035086)
        
        return root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupRecyclerView() {
        // We'll initialize the adapter after we have the home team ID
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())
        
        // Setup head-to-head RecyclerView
        h2hAdapter = FixtureAdapter { fixture ->
            // Handle H2H fixture click - load match details
            matchViewModel.loadMatchDetails(fixture.fixture.id)
        }
        binding.recyclerViewH2h.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewH2h.adapter = h2hAdapter
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupFixtureSearch() {
        fixtureAdapter = FixtureAdapter { fixture ->
            // Handle fixture click - load match details
            matchViewModel.loadMatchDetails(fixture.fixture.id)
            clearSearchResults()
        }
        
        // Setup RecyclerView for fixture search results
        binding.fixtureSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.fixtureSearchResults.adapter = fixtureAdapter
        
        // Observe fixtures list for search results
        matchViewModel.fixtures.observe(viewLifecycleOwner) { fixtures ->
            if (isSearchMode && fixtures.isNotEmpty()) {
                fixtureAdapter.submitList(fixtures)
                // Show fixture list, hide match details
                binding.matchDetailsContainer.visibility = View.GONE
                binding.fixtureSearchResults.visibility = View.VISIBLE
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onSearch(query: String) {
        if (query.isBlank()) return
        
        isSearchMode = true
        
        // Check if searching for live fixtures
        if (query.equals("live", ignoreCase = true)) {
            matchViewModel.loadLiveMatches()
        } else {
            // Search by team name
            matchViewModel.searchFixtures(query)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun clearSearchResults() {
        isSearchMode = false
        fixtureAdapter.submitList(emptyList())
        binding.fixtureSearchResults.visibility = View.GONE
        binding.matchDetailsContainer.visibility = View.VISIBLE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupObservers() {
        // Observe current match details
        matchViewModel.currentMatch.observe(viewLifecycleOwner) { fixture ->
            if (fixture != null) {
                updateMatchHeader(fixture)
                // Initialize events adapter with home team ID
                if (!::eventsAdapter.isInitialized) {
                    eventsAdapter = EventsAdapter(fixture.teams.home.id)
                    binding.recyclerViewEvents.adapter = eventsAdapter
                } else {
                    // Update adapter with new home team ID for new fixture
                    eventsAdapter = EventsAdapter(fixture.teams.home.id)
                    binding.recyclerViewEvents.adapter = eventsAdapter
                }
                
                // Track current fixture and check if it's live
                currentFixtureId = fixture.fixture.id
                val status = fixture.fixture.status.short
                isLiveMatch = status in listOf("1H", "2H", "HT", "ET", "BT", "P", "LIVE")
                
                // Start or stop auto-refresh based on match status
                if (isLiveMatch) {
                    startAutoRefresh()
                } else {
                    stopAutoRefresh()
                }
            } else {
                // Clear UI when fixture is null
                clearMatchUI()
                stopAutoRefresh()
            }
        }
        
        // Observe statistics
        matchViewModel.statistics.observe(viewLifecycleOwner) { stats ->
            if (stats.isNotEmpty()) {
                updateStatistics(stats)
            } else {
                clearStatistics()
            }
        }
        
        // Observe events
        matchViewModel.events.observe(viewLifecycleOwner) { events ->
            if (::eventsAdapter.isInitialized) {
                eventsAdapter.submitList(events)
                
                binding.noEventsText.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
                binding.recyclerViewEvents.visibility = if (events.isEmpty()) View.GONE else View.VISIBLE
            }
        }
        
        // Observe odds
        matchViewModel.odds.observe(viewLifecycleOwner) { odds ->
            if (odds.isNotEmpty()) {
                updateOdds(odds)
            } else {
                clearOdds()
            }
        }
        
        // Observe head-to-head fixtures
        matchViewModel.headToHeadFixtures.observe(viewLifecycleOwner) { h2hFixtures ->
            if (h2hFixtures.isNotEmpty()) {
                h2hAdapter.submitList(h2hFixtures)
                binding.recyclerViewH2h.visibility = View.VISIBLE
                binding.noPreviousGamesText.visibility = View.GONE
            } else {
                h2hAdapter.submitList(emptyList())
                binding.recyclerViewH2h.visibility = View.GONE
                binding.noPreviousGamesText.visibility = View.VISIBLE
            }
        }
        
        // Observe errors
        matchViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                matchViewModel.clearError()
            }
        }
        
        // Observe loading states
        matchViewModel.isLoadingDetails.observe(viewLifecycleOwner) { isLoading ->
           
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateMatchHeader(fixture: com.example.footyxapp.data.model.FixtureData) {
        with(binding) {
            // Load team logos
            Glide.with(requireContext())
                .load(fixture.teams.home.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .into(homeTeamLogo)
                
            Glide.with(requireContext())
                .load(fixture.teams.away.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .into(awayTeamLogo)
                
            // Set team names
            homeTeamName.text = fixture.teams.home.name
            awayTeamName.text = fixture.teams.away.name
            
            // Set scores
            textScoreHome.text = fixture.goals.home?.toString() ?: "-"
            textScoreAway.text = fixture.goals.away?.toString() ?: "-"
            
            // Set match status
            textMatchStatus.text = fixture.fixture.status.long
            
            // Format and set date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val displayFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault())
            try {
                val date = dateFormat.parse(fixture.fixture.date)
                textMatchDate.text = date?.let { displayFormat.format(it) } ?: fixture.fixture.date
            } catch (e: Exception) {
                textMatchDate.text = fixture.fixture.date
            }
            
            // Set stadium and referee info
            textStadiumName.text = fixture.fixture.venue.name ?: "Unknown"
            textRefereeName.text = fixture.fixture.referee ?: "Unknown"
            
            // Load team logos for "View Team" section
            Glide.with(requireContext())
                .load(fixture.teams.home.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .into(viewHomeTeamLogo)
                
            Glide.with(requireContext())
                .load(fixture.teams.away.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .into(viewAwayTeamLogo)
                
            viewHomeTeamNameLabel.text = fixture.teams.home.name
            viewAwayTeamNameLabel.text = fixture.teams.away.name
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateStatistics(stats: List<com.example.footyxapp.data.model.FixtureTeamStatistics>) {
        if (stats.size < 2) return
        
        val homeStats = stats[0]
        val awayStats = stats[1]
        
        with(binding) {
            // Helper function to get stat value and remove .0 suffix
            fun getStatValue(team: com.example.footyxapp.data.model.FixtureTeamStatistics, type: String): String {
                val value = team.statistics.find { it.type == type }?.value?.toString() ?: "-"
                // Remove .0 from decimal numbers
                return if (value.endsWith(".0")) {
                    value.substring(0, value.length - 2)
                } else {
                    value
                }
            }
            
            // Update all statistics
            homeShotsOnGoal.text = getStatValue(homeStats, "Shots on Goal")
            awayShotsOnGoal.text = getStatValue(awayStats, "Shots on Goal")
            
            homeShotsOffGoal.text = getStatValue(homeStats, "Shots off Goal")
            awayShotsOffGoal.text = getStatValue(awayStats, "Shots off Goal")
            
            homeTotalShots.text = getStatValue(homeStats, "Total Shots")
            awayTotalShots.text = getStatValue(awayStats, "Total Shots")
            
            homeBlockedShots.text = getStatValue(homeStats, "Blocked Shots")
            awayBlockedShots.text = getStatValue(awayStats, "Blocked Shots")
            
            homeShotsInsidebox.text = getStatValue(homeStats, "Shots insidebox")
            awayShotsInsidebox.text = getStatValue(awayStats, "Shots insidebox")
            
            homeShotsOutsidebox.text = getStatValue(homeStats, "Shots outsidebox")
            awayShotsOutsidebox.text = getStatValue(awayStats, "Shots outsidebox")
            
            homeFouls.text = getStatValue(homeStats, "Fouls")
            awayFouls.text = getStatValue(awayStats, "Fouls")
            
            homeCornerKicks.text = getStatValue(homeStats, "Corner Kicks")
            awayCornerKicks.text = getStatValue(awayStats, "Corner Kicks")
            
            homeOffsides.text = getStatValue(homeStats, "Offsides")
            awayOffsides.text = getStatValue(awayStats, "Offsides")
            
            homeBallPossession.text = getStatValue(homeStats, "Ball Possession")
            awayBallPossession.text = getStatValue(awayStats, "Ball Possession")
            
            homeYellowCards.text = getStatValue(homeStats, "Yellow Cards")
            awayYellowCards.text = getStatValue(awayStats, "Yellow Cards")
            
            homeRedCards.text = getStatValue(homeStats, "Red Cards")
            awayRedCards.text = getStatValue(awayStats, "Red Cards")
            
            homeGoalkeeperSaves.text = getStatValue(homeStats, "Goalkeeper Saves")
            awayGoalkeeperSaves.text = getStatValue(awayStats, "Goalkeeper Saves")
            
            homeTotalPasses.text = getStatValue(homeStats, "Total passes")
            awayTotalPasses.text = getStatValue(awayStats, "Total passes")
            
            homePassesAccurate.text = getStatValue(homeStats, "Passes accurate")
            awayPassesAccurate.text = getStatValue(awayStats, "Passes accurate")
            
            homePassesPercentage.text = getStatValue(homeStats, "Passes %")
            awayPassesPercentage.text = getStatValue(awayStats, "Passes %")
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun clearMatchUI() {
        with(binding) {
            // Clear team logos
            homeTeamLogo.setImageResource(R.drawable.ic_team_placeholder)
            awayTeamLogo.setImageResource(R.drawable.ic_team_placeholder)
            viewHomeTeamLogo.setImageResource(R.drawable.ic_team_placeholder)
            viewAwayTeamLogo.setImageResource(R.drawable.ic_team_placeholder)
            
            // Clear team names
            homeTeamName.text = ""
            awayTeamName.text = ""
            viewHomeTeamNameLabel.text = ""
            viewAwayTeamNameLabel.text = ""
            
            // Clear scores
            textScoreHome.text = "-"
            textScoreAway.text = "-"
            
            // Clear match info
            textMatchStatus.text = ""
            textMatchDate.text = ""
            textStadiumName.text = ""
            textRefereeName.text = ""
            
            // Hide odds card
            winningOddsCard.visibility = View.GONE
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun clearStatistics() {
        with(binding) {
            // Clear all statistics
            homeShotsOnGoal.text = "-"
            awayShotsOnGoal.text = "-"
            
            homeShotsOffGoal.text = "-"
            awayShotsOffGoal.text = "-"
            
            homeTotalShots.text = "-"
            awayTotalShots.text = "-"
            
            homeBlockedShots.text = "-"
            awayBlockedShots.text = "-"
            
            homeShotsInsidebox.text = "-"
            awayShotsInsidebox.text = "-"
            
            homeShotsOutsidebox.text = "-"
            awayShotsOutsidebox.text = "-"
            
            homeFouls.text = "-"
            awayFouls.text = "-"
            
            homeCornerKicks.text = "-"
            awayCornerKicks.text = "-"
            
            homeOffsides.text = "-"
            awayOffsides.text = "-"
            
            homeBallPossession.text = "-"
            awayBallPossession.text = "-"
            
            homeYellowCards.text = "-"
            awayYellowCards.text = "-"
            
            homeRedCards.text = "-"
            awayRedCards.text = "-"
            
            homeGoalkeeperSaves.text = "-"
            awayGoalkeeperSaves.text = "-"
            
            homeTotalPasses.text = "-"
            awayTotalPasses.text = "-"
            
            homePassesAccurate.text = "-"
            awayPassesAccurate.text = "-"
            
            homePassesPercentage.text = "-"
            awayPassesPercentage.text = "-"
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateOdds(odds: List<com.example.footyxapp.data.model.BetValue>) {
        // Find Home, Draw, Away odds
        val homeOdd = odds.find { it.value.equals("Home", ignoreCase = true) }?.odd ?: "-"
        val drawOdd = odds.find { it.value.equals("Draw", ignoreCase = true) }?.odd ?: "-"
        val awayOdd = odds.find { it.value.equals("Away", ignoreCase = true) }?.odd ?: "-"
        
        with(binding) {
            // Show odds card and update values
            winningOddsCard.visibility = View.VISIBLE
            homeWinOddValue.text = homeOdd
            drawOddValue.text = drawOdd
            awayWinOddValue.text = awayOdd
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun clearOdds() {
        with(binding) {
            // Hide odds card when no odds available
            winningOddsCard.visibility = View.GONE
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Public method to load match details by fixture ID
    fun loadMatchDetails(fixtureId: Int) {
        matchViewModel.loadMatchDetails(fixtureId)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun startAutoRefresh() {
        stopAutoRefresh() // Stop any existing refresh first
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun stopAutoRefresh() {
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoRefresh()
        _binding = null
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    override fun onPause() {
        super.onPause()
        stopAutoRefresh()
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    override fun onResume() {
        super.onResume()
        if (isLiveMatch && currentFixtureId != null) {
            startAutoRefresh()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}