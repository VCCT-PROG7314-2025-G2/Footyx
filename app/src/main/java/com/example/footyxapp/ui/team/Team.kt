package com.example.footyxapp.ui.team

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.TeamData
import com.example.footyxapp.data.model.TeamLeagueData
import com.example.footyxapp.data.model.TeamStatistics
import com.example.footyxapp.databinding.FragmentTeamBinding
import com.example.footyxapp.ui.common.SearchableFragment
import com.example.footyxapp.ui.team.adapter.TeamSearchAdapter
import com.example.footyxapp.ui.team.adapter.TeamLeagueAdapter
import com.example.footyxapp.ui.team.adapter.FormationAdapter
import com.example.footyxapp.ui.team.adapter.FormationWithPercentage

class Team : Fragment(), SearchableFragment {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        fun newInstance() = Team()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val viewModel: TeamViewModel by viewModels()
    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var searchAdapter: TeamSearchAdapter
    private lateinit var leagueAdapter: TeamLeagueAdapter
    private lateinit var formationAdapter: FormationAdapter
    private var currentTeamData: TeamData? = null
    private var selectedSeason: Int = 2023

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupSeasonFunctionality()
        observeViewModel()
        
        // Load default favorite team if available, otherwise load example
        loadDefaultOrExampleTeam()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onSearch(query: String) {
        if (query.isNotEmpty()) {
            viewModel.searchTeams(query)
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

    private fun setupRecyclerViews() {
        searchAdapter = TeamSearchAdapter { teamData ->
            onTeamSelected(teamData)
        }
        
        leagueAdapter = TeamLeagueAdapter { leagueData ->
            onLeagueSelected(leagueData)
        }
        
        formationAdapter = FormationAdapter()
        
        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
        
        binding.recyclerTeamLeagues.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leagueAdapter
        }
        
        binding.recyclerFormations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = formationAdapter
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupSeasonFunctionality() {
        binding.btnClearSearch.setOnClickListener {
            binding.editSeason.setText("2023")
            selectedSeason = 2023
            clearSearchResults()
            hideTeamDetails()
        }
        
        // Add validation feedback for season input
        binding.editSeason.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val seasonText = s.toString().trim()
                val season = seasonText.toIntOrNull()
                
                // Visual feedback for invalid season
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

    private fun onTeamSelected(teamData: TeamData) {
        currentTeamData = teamData
        val seasonText = binding.editSeason.text.toString().trim()
        val inputSeason = seasonText.toIntOrNull()
        // Validate season: 2021-2023 only, default to 2023
        selectedSeason = if (inputSeason != null && inputSeason in 2021..2023) {
            inputSeason
        } else {
            2023
        }
        
        // Hide search results
        hideSearchResults()
        
        // Load team leagues for the selected season
        viewModel.loadTeamLeagues(teamData.team.id, selectedSeason)
        
        // Show league selection
        showLeagueSelection()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun onLeagueSelected(leagueData: TeamLeagueData) {
        currentTeamData?.let { teamData ->
            // Load team statistics for the selected league and season
            viewModel.loadTeamStatistics(teamData.team.id, leagueData.league.id, selectedSeason)
            
            // Hide league selection and show team details
            hideLeagueSelection()
            showTeamDetails()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun loadDefaultOrExampleTeam() {
        // Try to load default favorite team
        val favoritesManager = com.example.footyxapp.data.manager.FavoritesManager.getInstance(requireContext())
        val favorite = favoritesManager.getFavoriteTeam()
        
        if (favorite != null) {
            // Load the favorite team with the selected league
            currentTeamData = favorite.teamData
            selectedSeason = favorite.season
            
            // Load team statistics directly for the selected league
            viewModel.loadTeamStatistics(
                favorite.teamData.team.id,
                favorite.leagueId,
                favorite.season
            )
            
            // Show team details immediately
            showTeamDetails()
        } else {
            // Load PSG as example if no favorite team
            loadExampleTeam()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun loadExampleTeam() {
        // Load PSG data as example
        currentTeamData = TeamData(
            team = com.example.footyxapp.data.model.TeamInfo(
                id = 85,
                name = "Paris Saint Germain",
                code = "PAR",
                country = "France",
                founded = 1970,
                national = false,
                logo = "https://media.api-sports.io/football/teams/85.png"
            ),
            venue = com.example.footyxapp.data.model.Venue(
                id = 671,
                name = "Parc des Princes",
                address = "24, rue du Commandant Guilbaud",
                city = "Paris",
                capacity = 47929,
                surface = "grass",
                image = "https://media.api-sports.io/football/venues/671.png"
            )
        )
        selectedSeason = 2023
        viewModel.loadTeamStatistics(85, 61, 2023) // PSG in Ligue 1 2023
        showTeamDetails()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showSearchResults() {
        binding.recyclerSearchResults.visibility = View.VISIBLE
        binding.seasonSelectionCard.visibility = View.VISIBLE
        hideLeagueSelection()
        hideTeamDetails()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hideSearchResults() {
        binding.recyclerSearchResults.visibility = View.GONE
        binding.seasonSelectionCard.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showLeagueSelection() {
        binding.recyclerTeamLeagues.visibility = View.VISIBLE
        binding.leagueSelectionCard.visibility = View.VISIBLE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hideLeagueSelection() {
        binding.recyclerTeamLeagues.visibility = View.GONE
        binding.leagueSelectionCard.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showTeamDetails() {
        binding.layoutTeamDetails.visibility = View.VISIBLE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun hideTeamDetails() {
        binding.layoutTeamDetails.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            searchAdapter.submitList(teams)
        }
        
        viewModel.teamLeagues.observe(viewLifecycleOwner) { leagues ->
            leagueAdapter.submitList(leagues)
        }
        
        viewModel.teamStatistics.observe(viewLifecycleOwner) { statistics ->
            updateTeamStatistics(statistics)
        }
        
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.progressSearch.visibility = if (isSearching) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoadingLeagues.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLeagues.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoadingStatistics.observe(viewLifecycleOwner) { isLoading ->
            binding.progressStatistics.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateTeamStatistics(statistics: TeamStatistics) {
        currentTeamData?.let { teamData ->
            // Update team header
            binding.textTeamName.text = teamData.team.name
            binding.textLeagueName.text = "${statistics.league.name} - $selectedSeason"
            
            Glide.with(this)
                .load(teamData.team.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(binding.teamLogo)
            
            // Update form
            updateTeamForm(statistics.form)
            
            // Update season overview
            binding.textPlayed.text = "Played\n${statistics.fixtures.played.total}"
            binding.textWins.text = "Wins\n${statistics.fixtures.wins.total}"
            binding.textDraws.text = "Draws\n${statistics.fixtures.draws.total}"
            binding.textLoses.text = "Loses\n${statistics.fixtures.loses.total}"
            
            // Update goal statistics
            binding.textGoalsFor.text = statistics.goals.goalsFor.total.total.toString()
            binding.textGoalsForAvg.text = "${statistics.goals.goalsFor.average.total} per game"
            binding.textGoalsAgainst.text = statistics.goals.goalsAgainst.total.total.toString()
            binding.textGoalsAgainstAvg.text = "${statistics.goals.goalsAgainst.average.total} per game"
            
            // Update season records
            val biggestWin = statistics.biggest.wins.home ?: statistics.biggest.wins.away ?: "N/A"
            binding.textBiggestWin.text = biggestWin
            binding.textCleanSheets.text = statistics.cleanSheet.total.toString()
            binding.textWinStreak.text = statistics.biggest.streak.wins.toString()
            binding.textFailedToScore.text = statistics.failedToScore.total.toString()
            
            // Calculate total cards (approximate)
            val yellowCards = countTotalCards(statistics.cards.yellow)
            val redCards = countTotalCards(statistics.cards.red)
            binding.textYellowCards.text = yellowCards.toString()
            binding.textRedCards.text = redCards.toString()
            
            // Update formations
            updateFormations(statistics.lineups)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateTeamForm(form: String) {
        binding.formContainer.removeAllViews()
        
        form.takeLast(10).forEach { result ->
            val resultView = LayoutInflater.from(context).inflate(R.layout.item_form_result, binding.formContainer, false)
            val textResult = resultView.findViewById<TextView>(R.id.text_result)
            textResult.text = result.toString()
            
            val backgroundColor = when (result) {
                'W' -> R.color.green_500
                'D' -> R.color.yellow_500
                'L' -> R.color.red_500
                else -> R.color.gray_500
            }
            textResult.setBackgroundResource(backgroundColor)
            
            binding.formContainer.addView(resultView)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun countTotalCards(cardsMinute: com.example.footyxapp.data.model.TeamGoalsMinute): Int {
        return listOfNotNull(
            cardsMinute.min0To15.total,
            cardsMinute.min16To30.total,
            cardsMinute.min31To45.total,
            cardsMinute.min46To60.total,
            cardsMinute.min61To75.total,
            cardsMinute.min76To90.total,
            cardsMinute.min91To105.total,
            cardsMinute.min106To120.total
        ).sum()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun updateFormations(lineups: List<com.example.footyxapp.data.model.TeamLineup>) {
        val totalGames = lineups.sumOf { it.played }
        
        val formationsWithPercentage = lineups
            .sortedByDescending { it.played } // Sort by most used first
            .map { lineup ->
                val percentage = if (totalGames > 0) {
                    (lineup.played.toFloat() / totalGames.toFloat()) * 100f
                } else 0f
                
                FormationWithPercentage(
                    formation = lineup.formation,
                    played = lineup.played,
                    percentage = percentage
                )
            }
        
        formationAdapter.submitList(formationsWithPercentage)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}