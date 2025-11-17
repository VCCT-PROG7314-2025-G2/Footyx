package com.example.footyxapp.ui.player

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.manager.FavoritesManager
import com.example.footyxapp.data.model.PlayerData
import com.example.footyxapp.data.model.PlayerProfile
import com.example.footyxapp.data.model.Statistics
import com.example.footyxapp.databinding.FragmentPlayerBinding
import com.example.footyxapp.ui.common.SearchableFragment
import com.example.footyxapp.ui.player.adapter.PlayerSearchAdapter
import com.example.footyxapp.ui.player.adapter.TeamLeagueOption
import com.example.footyxapp.ui.player.adapter.TeamSelectionAdapter

class Player : Fragment(), SearchableFragment {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        fun newInstance() = Player()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val viewModel: PlayerViewModel by viewModels()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var searchAdapter: PlayerSearchAdapter
    private var currentPlayerData: PlayerData? = null
    private var selectedStatisticsIndex: Int = 0

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSeasonFunctionality()
        observeViewModel()
        
        // Try to load favorite player, otherwise load Mbappé as example
        loadDefaultOrExamplePlayer()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun loadDefaultOrExamplePlayer() {
        val favoritesManager = FavoritesManager.getInstance(requireContext())
        val favoritePlayer = favoritesManager.getFavoritePlayer()
        
        if (favoritePlayer != null) {
            // Load favorite player
            val playerId = favoritePlayer.playerData.player.id
            val season = favoritePlayer.season
            
            // Update season input
            binding.editSeason.setText(season.toString())
            
            // Load player statistics
            viewModel.loadPlayer(playerId, season)
        } else {
            // Load Mbappé's data as example (ID: 278)
            viewModel.loadPlayer(278, 2023)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupRecyclerView() {
        searchAdapter = PlayerSearchAdapter { playerProfile ->
            onPlayerSelected(playerProfile)
        }
        
        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupSeasonFunctionality() {
        binding.btnClearSearch.setOnClickListener {
            binding.editSeason.setText("2023")
            clearSearchResults()
            hidePlayerDetails()
        }
        
        // Add real-time season validation
        binding.editSeason.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val seasonText = s.toString().trim()
                val season = seasonText.toIntOrNull()
                
                // Show error if season is not in valid range
                if (seasonText.isNotEmpty() && (season == null || season !in 2021..2023)) {
                    binding.editSeason.error = "Valid seasons: 2021-2023"
                } else {
                    binding.editSeason.error = null
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun onPlayerSelected(playerProfile: PlayerProfile) {
        val playerId = playerProfile.player.id
        val seasonText = binding.editSeason.text.toString().trim()
        val inputSeason = seasonText.toIntOrNull()
        
        // Validate season is in 2021-2023 range, default to 2023 if invalid
        val season = if (inputSeason != null && inputSeason in 2021..2023) {
            inputSeason
        } else {
            2023
        }
        
        // Update season input if it was corrected
        if (season != inputSeason) {
            binding.editSeason.setText(season.toString())
        }
        
        // Hide search results and show player details
        hideSearchResults()
        binding.layoutPlayerDetails.visibility = View.VISIBLE
        
        // Load player statistics for the selected season
        viewModel.loadPlayer(playerId, season)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hideSearchResults() {
        binding.recyclerSearchResults.visibility = View.GONE
        binding.seasonSelectionCard.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hidePlayerDetails() {
        binding.layoutPlayerDetails.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Implementation of SearchableFragment interface
    override fun onSearch(query: String) {
        if (query.trim().length >= 2) {
            viewModel.searchPlayers(query.trim())
        } else {
            clearSearchResults()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun clearSearchResults() {
        viewModel.clearSearch()
        hideSearchResults()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // This method will be called from MainActivity when user searches (legacy support)
    fun handleSearch(query: String) {
        onSearch(query)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun observeViewModel() {
        viewModel.playerData.observe(viewLifecycleOwner) { playerData ->
            playerData?.let { 
                updateUI(it)
                binding.layoutPlayerDetails.visibility = View.VISIBLE
            }
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            searchAdapter.submitList(searchResults)
            if (searchResults.isNotEmpty()) {
                binding.recyclerSearchResults.visibility = View.VISIBLE
                binding.seasonSelectionCard.visibility = View.VISIBLE
                hidePlayerDetails()
            } else {
                hideSearchResults()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.searchProgressBar.visibility = if (isSearching) View.VISIBLE else View.GONE
        }

        var lastErrorMessage: String? = null
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Only show toast if it's a different error message to prevent spam
                if (it != lastErrorMessage) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    lastErrorMessage = it
                }
                // Clear the error after showing it
                viewModel.clearError()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateUI(playerData: PlayerData) {
        currentPlayerData = playerData
        val player = playerData.player

        // Basic player info
        binding.textPlayerName.text = player.name
        binding.textNationality.text = player.nationality
        binding.textAge.text = player.age.toString()
        binding.textHeight.text = "${player.height} cm"
        binding.textWeight.text = "${player.weight} kg"

        // Load player photo
        Glide.with(this)
            .load(player.photo)
            .placeholder(R.drawable.player_w)
            .error(R.drawable.player_w)
            .into(binding.playerPhoto)

        // Setup team selection if multiple statistics are available
        if (playerData.statistics.size > 1) {
            setupTeamSelection(playerData.statistics)
            binding.teamSelectionCard.visibility = View.VISIBLE
        } else {
            binding.teamSelectionCard.visibility = View.GONE
            selectedStatisticsIndex = 0
        }

        // Display statistics for the selected team/league
        displayStatistics()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupTeamSelection(statisticsList: List<Statistics>) {
        val options = statisticsList.mapIndexed { index, stats ->
            val displayName = "${stats.team.name} (${stats.league.name})"
            TeamLeagueOption(stats, displayName)
        }

        val adapter = TeamSelectionAdapter(requireContext(), options)
        binding.spinnerTeamSelection.adapter = adapter

        binding.spinnerTeamSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStatisticsIndex = position
                displayStatistics()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun displayStatistics() {
        val playerData = currentPlayerData ?: return
        val stats = if (playerData.statistics.isNotEmpty()) {
            playerData.statistics.getOrNull(selectedStatisticsIndex) ?: playerData.statistics[0]
        } else {
            null
        }

        stats?.let { statistics ->
            // Team info
            binding.textTeamName.text = statistics.team.name
            
            // Load team logo
            Glide.with(this)
                .load(statistics.team.logo)
                .placeholder(R.drawable.team_w)
                .error(R.drawable.team_w)
                .into(binding.teamLogo)

            // Position
            binding.textPlayerPosition.text = statistics.games.position ?: "Unknown"

            // Main statistics
            val appearances = statistics.games.appearances ?: 0
            val goals = statistics.goals.total ?: 0
            val assists = statistics.goals.assists ?: 0
            val rating = statistics.games.rating?.let { 
                "%.2f".format(it.toDoubleOrNull() ?: 0.0) 
            } ?: "N/A"

            binding.textAppearances.text = "Apps\n$appearances"
            binding.textGoals.text = "Goals\n$goals"
            binding.textAssists.text = "Assists\n$assists"
            binding.textRating.text = "Rating\n$rating"

            // Attacking stats
            binding.textShotsTotal.text = "${statistics.shots.total ?: 0}"
            binding.textShotsOn.text = "${statistics.shots.on ?: 0}"
            
            val dribblesSuccess = statistics.dribbles.success ?: 0
            val dribblesAttempts = statistics.dribbles.attempts ?: 0
            binding.textDribbles.text = "$dribblesSuccess / $dribblesAttempts"
            
            binding.textKeyPasses.text = "${statistics.passes.key ?: 0}"

            // Defense stats
            val duelsWon = statistics.duels.won ?: 0
            val duelsTotal = statistics.duels.total ?: 0
            binding.textDuelsWon.text = "$duelsWon / $duelsTotal"
            
            binding.textTackles.text = "${statistics.tackles.total ?: 0}"
            binding.textFoulsDrawn.text = "${statistics.fouls.drawn ?: 0}"
            binding.textYellowCards.text = "${statistics.cards.yellow ?: 0}"
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}