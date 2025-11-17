package com.example.footyxapp.ui.favorites.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.footyxapp.R
import com.example.footyxapp.data.model.TeamData
import com.example.footyxapp.data.model.TeamLeagueData
import com.example.footyxapp.databinding.DialogTeamSearchBinding
import com.example.footyxapp.ui.team.TeamViewModel
import com.example.footyxapp.ui.team.adapter.TeamSearchAdapter
import com.example.footyxapp.ui.team.adapter.TeamLeagueAdapter

class TeamSearchDialogFragment : DialogFragment() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private var _binding: DialogTeamSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TeamViewModel by viewModels()
    private lateinit var searchAdapter: TeamSearchAdapter
    private lateinit var leagueAdapter: TeamLeagueAdapter
    
    private var selectedTeam: TeamData? = null
    private var selectedSeason: Int = 2023 // Default to 2023
    
    private var onTeamAndLeagueSelected: ((TeamData, TeamLeagueData, Int) -> Unit)? = null
    
    // Rate limiting and debouncing
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastSearchQuery = ""

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        const val TAG = "TeamSearchDialog"
        private const val SEARCH_DELAY_MS = 800L // Delay before executing search
        private const val MIN_SEARCH_LENGTH = 2 // Minimum characters before searching
        
        fun newInstance(onTeamAndLeagueSelected: (TeamData, TeamLeagueData, Int) -> Unit): TeamSearchDialogFragment {
            return TeamSearchDialogFragment().apply {
                this.onTeamAndLeagueSelected = onTeamAndLeagueSelected
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogTeamSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupSearchBar()
        setupSeasonInput()
        setupCloseButton()
        observeViewModel()
        
        // Show team search initially
        showTeamSearch()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            // Remove default dialog margins
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupRecyclerViews() {
        // Team search adapter
        searchAdapter = TeamSearchAdapter { teamData ->
            onTeamSearchResultClicked(teamData)
        }
        
        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
        
        // League selection adapter
        leagueAdapter = TeamLeagueAdapter { leagueData ->
            onLeagueSelected(leagueData)
        }
        
        binding.recyclerLeagueResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leagueAdapter
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun onTeamSearchResultClicked(teamData: TeamData) {
        selectedTeam = teamData
        // Load leagues for this team
        viewModel.loadTeamLeagues(teamData.team.id, selectedSeason)
        showLeagueSelection()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun onLeagueSelected(leagueData: TeamLeagueData) {
        selectedTeam?.let { team ->
            onTeamAndLeagueSelected?.invoke(team, leagueData, selectedSeason)
            dismiss()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupSearchBar() {
        binding.editSearchTeam.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                
                // Cancel any pending search
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                
                if (query.length < MIN_SEARCH_LENGTH) {
                    viewModel.clearSearchResults()
                    lastSearchQuery = ""
                    return
                }
                
                // Skip if same query
                if (query == lastSearchQuery) {
                    return
                }
                
                // Debounce: wait before executing search
                searchRunnable = Runnable {
                    lastSearchQuery = query
                    viewModel.searchTeams(query)
                }
                searchHandler.postDelayed(searchRunnable!!, SEARCH_DELAY_MS)
            }
        })
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupSeasonInput() {
        // Set default season to 2023
        binding.editSeason.setText(selectedSeason.toString())
        
        // Update selected season when user changes it
        binding.editSeason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val seasonText = s.toString().trim()
                val season = seasonText.toIntOrNull()
                // Valid seasons: 2021-2023 only
                if (season != null && season in 2021..2023) {
                    selectedSeason = season
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupCloseButton() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showTeamSearch() {
        binding.txtDialogTitle.text = "Search for a Team"
        binding.layoutTeamSearch.visibility = View.VISIBLE
        binding.layoutLeagueSelection.visibility = View.GONE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showLeagueSelection() {
        binding.txtDialogTitle.text = "Select League"
        binding.layoutTeamSearch.visibility = View.GONE
        binding.layoutLeagueSelection.visibility = View.VISIBLE
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            searchAdapter.submitList(teams)
            
            // Show empty state if no results
            if (teams.isEmpty() && binding.editSearchTeam.text.toString().isNotEmpty()) {
                binding.txtEmptyState.visibility = View.VISIBLE
                binding.recyclerSearchResults.visibility = View.GONE
            } else {
                binding.txtEmptyState.visibility = View.GONE
                binding.recyclerSearchResults.visibility = View.VISIBLE
            }
        }
        
        viewModel.teamLeagues.observe(viewLifecycleOwner) { leagues ->
            leagueAdapter.submitList(leagues)
        }
        
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.progressSearch.visibility = if (isSearching) View.VISIBLE else View.GONE
        }
        
        viewModel.isLoadingLeagues.observe(viewLifecycleOwner) { isLoading ->
            binding.progressLeagues.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up search handler
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        _binding = null
    }
}
