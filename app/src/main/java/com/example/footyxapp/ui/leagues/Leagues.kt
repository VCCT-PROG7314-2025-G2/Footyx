package com.example.footyxapp.ui.leagues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.LeagueData
import com.example.footyxapp.databinding.FragmentLeaguesBinding
import com.example.footyxapp.ui.common.SearchableFragment
import com.example.footyxapp.ui.leagues.adapter.LeagueSearchAdapter
import com.example.footyxapp.ui.leagues.adapter.GroupedStandingsAdapter

class Leagues : Fragment(), SearchableFragment {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        fun newInstance() = Leagues()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val viewModel: LeaguesViewModel by viewModels()
    private var _binding: FragmentLeaguesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var searchAdapter: LeagueSearchAdapter
    private lateinit var standingsAdapter: GroupedStandingsAdapter
    private lateinit var seasonAdapter: com.example.footyxapp.ui.leagues.adapter.SeasonAdapter
    private var currentLeagueData: LeagueData? = null
    private var selectedSeason: Int = 2023

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
        
        // Load default league (e.g., Premier League 2023)
        loadDefaultLeague()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onSearch(query: String) {
        if (query.isNotEmpty()) {
            viewModel.searchLeagues(query)
            showSearchResults()
        } else {
            clearSearchResults()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun clearSearchResults() {
        viewModel.clearSearchResults()
        hideSearchResults()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupClickListeners() {
        binding.btnToggleSeason.setOnClickListener {
            toggleSeasonSelection()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun toggleSeasonSelection() {
        if (binding.seasonSelectionCard.visibility == View.VISIBLE) {
            binding.seasonSelectionCard.visibility = View.GONE
        } else {
            binding.seasonSelectionCard.visibility = View.VISIBLE
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupRecyclerViews() {
        searchAdapter = LeagueSearchAdapter { leagueData ->
            onLeagueSearchResultSelected(leagueData)
        }
        
        standingsAdapter = GroupedStandingsAdapter()
        
        seasonAdapter = com.example.footyxapp.ui.leagues.adapter.SeasonAdapter { season ->
            onSeasonSelected(season)
        }
        
        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
        
        binding.recyclerSeasons.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = seasonAdapter
        }
        
        binding.standingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = standingsAdapter
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun onLeagueSearchResultSelected(leagueData: LeagueData) {
        currentLeagueData = leagueData
        
        // Hide search results
        hideSearchResults()
        
        // Get available seasons (filter to 2021-2023 for free API plan)
        val availableSeasons = leagueData.seasons
            .filter { it.year in 2021..2023 }
            .sortedByDescending { it.year }
            .map { it.year }
        
        if (availableSeasons.isNotEmpty()) {
            // Default to 2023 if available, otherwise the most recent
            selectedSeason = if (availableSeasons.contains(2023)) 2023 else availableSeasons.first()
            
            // Prepare season selection but keep it hidden initially
            seasonAdapter.submitList(availableSeasons)
            binding.seasonSelectionCard.visibility = View.GONE
            
            // Load standings for default season
            loadStandingsForCurrentLeague()
        } else {
            Toast.makeText(context, "No available seasons (2021-2023) for this league", Toast.LENGTH_SHORT).show()
        }
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private fun onSeasonSelected(season: Int) {
        selectedSeason = season
        loadStandingsForCurrentLeague()
    }
    
    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//
    
    private fun loadStandingsForCurrentLeague() {
        currentLeagueData?.let { leagueData ->
            // Load standings for the selected league and season
            viewModel.loadStandings(leagueData.league.id, leagueData.league.name, selectedSeason)
            
            // Show standings
            showStandings()
            
            // Update header
            updateLeagueHeader(leagueData, selectedSeason)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun loadDefaultLeague() {
        // Load Premier League 2023 as default
        viewModel.loadStandings(39, "Premier League", 2023)
        showStandings()
        
        // Create a simple league data for header display
        binding.leagueName.text = "Premier League - 2023"
        binding.leagueHeaderCard.visibility = View.VISIBLE
        binding.btnToggleSeason.visibility = View.GONE  // Hide button for default league
        
        // Load logo (you can set a placeholder or load from URL)
        Glide.with(this)
            .load("https://media.api-sports.io/football/leagues/39.png")
            .placeholder(R.drawable.ic_team_placeholder)
            .error(R.drawable.ic_team_placeholder)
            .into(binding.leagueLogo)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateLeagueHeader(leagueData: LeagueData, season: Int) {
        binding.leagueName.text = "${leagueData.league.name} - $season"
        binding.btnToggleSeason.visibility = View.VISIBLE  // Show button for searched leagues
        
        Glide.with(this)
            .load(leagueData.league.logo)
            .placeholder(R.drawable.ic_team_placeholder)
            .error(R.drawable.ic_team_placeholder)
            .into(binding.leagueLogo)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showSearchResults() {
        binding.recyclerSearchResults.visibility = View.VISIBLE
        binding.seasonSelectionCard.visibility = View.GONE
        hideStandings()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hideSearchResults() {
        binding.recyclerSearchResults.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showStandings() {
        binding.leagueHeaderCard.visibility = View.VISIBLE
        binding.standingsHeader.visibility = View.VISIBLE
        binding.standingsLegend.getRoot().visibility = View.VISIBLE
        binding.standingsRecyclerView.visibility = View.VISIBLE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hideStandings() {
        binding.leagueHeaderCard.visibility = View.GONE
        binding.standingsHeader.visibility = View.GONE
        binding.standingsLegend.getRoot().visibility = View.GONE
        binding.standingsRecyclerView.visibility = View.GONE
        binding.seasonSelectionCard.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { leagues ->
            searchAdapter.submitList(leagues)
        }
        
        viewModel.standings.observe(viewLifecycleOwner) { standings ->
            standingsAdapter.submitList(standings)
        }
        
        viewModel.currentLeague.observe(viewLifecycleOwner) { (leagueName, season) ->
            // League header is already updated in onLeagueSelected or loadDefaultLeague
        }
        
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.progressSearch.visibility = if (isSearching) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoadingStandings.observe(viewLifecycleOwner) { isLoading ->
            binding.progressStandings.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}