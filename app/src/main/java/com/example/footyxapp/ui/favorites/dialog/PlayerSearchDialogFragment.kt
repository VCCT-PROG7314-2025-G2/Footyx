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
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.footyxapp.R
import com.example.footyxapp.data.model.PlayerData
import com.example.footyxapp.data.model.PlayerProfile
import com.example.footyxapp.databinding.DialogPlayerSearchBinding
import com.example.footyxapp.ui.player.PlayerViewModel
import com.example.footyxapp.ui.player.adapter.PlayerSearchAdapter

class PlayerSearchDialogFragment : DialogFragment() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        const val TAG = "PlayerSearchDialog"
        private const val SEARCH_DELAY_MS = 800L // Delay before executing search
        private const val MIN_SEARCH_LENGTH = 2 // Minimum characters before searching
        
        fun newInstance(
            onPlayerSelected: (PlayerData, Int) -> Unit
        ): PlayerSearchDialogFragment {
            return PlayerSearchDialogFragment().apply {
                this.onPlayerSelectedCallback = onPlayerSelected
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private var _binding: DialogPlayerSearchBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var searchAdapter: PlayerSearchAdapter
    
    private var onPlayerSelectedCallback: ((PlayerData, Int) -> Unit)? = null
    private var selectedSeason: Int = 2023
    
    // Rate limiting and debouncing
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastSearchQuery = ""

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPlayerSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDialog()
        setupRecyclerView()
        setupSeasonInput()
        setupSearchInput()
        setupClickListeners()
        observeViewModel()
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

    private fun setupDialog() {
        binding.txtDialogTitle.text = "Search for a Player"
    }

    private fun setupRecyclerView() {
        searchAdapter = PlayerSearchAdapter { playerProfile ->
            onPlayerSearchResultClicked(playerProfile)
        }
        
        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupSeasonInput() {
        binding.editSeason.setText(selectedSeason.toString())
        
        binding.editSeason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val seasonText = s.toString().trim()
                val season = seasonText.toIntOrNull()
                
                // Validate season is in 2021-2023 range
                if (season != null && season in 2021..2023) {
                    selectedSeason = season
                }
            }
        })
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupSearchInput() {
        binding.editPlayerSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                
                // Cancel any pending search
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                
                if (query.length < MIN_SEARCH_LENGTH) {
                    searchAdapter.submitList(emptyList())
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
                    viewModel.searchPlayers(query)
                }
                searchHandler.postDelayed(searchRunnable!!, SEARCH_DELAY_MS)
            }
        })
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun onPlayerSearchResultClicked(playerProfile: PlayerProfile) {
        val playerId = playerProfile.player.id
        
        // Validate season
        val inputSeason = binding.editSeason.text.toString().trim().toIntOrNull()
        val finalSeason = if (inputSeason != null && inputSeason in 2021..2023) {
            inputSeason
        } else {
            2023 // Default to 2023 if invalid
        }
        
        // Show loading
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerSearchResults.visibility = View.GONE
        
        // Load full player data with statistics for the selected season
        viewModel.loadPlayer(playerId, finalSeason)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchAdapter.submitList(results)
            binding.recyclerSearchResults.visibility = if (results.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.searchProgressBar.visibility = if (isSearching) View.VISIBLE else View.GONE
        }

        viewModel.playerData.observe(viewLifecycleOwner) { playerData ->
            playerData?.let {
                // Successfully loaded player data with statistics
                binding.progressBar.visibility = View.GONE
                
                // Return the player data with season to the callback
                onPlayerSelectedCallback?.invoke(it, selectedSeason)
                dismiss()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.progressBar.visibility = View.GONE
                binding.recyclerSearchResults.visibility = View.VISIBLE
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
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
