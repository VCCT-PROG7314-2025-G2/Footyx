package com.example.footyxapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.footyxapp.FavoritesActivity
import com.example.footyxapp.R
import com.example.footyxapp.data.manager.FavoritesManager
import com.example.footyxapp.databinding.FragmentHomeBinding
import com.example.footyxapp.ui.home.adapter.TransferAdapter
import com.example.footyxapp.ui.match.adapter.FixtureAdapter

class Home : Fragment() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var transferAdapter: TransferAdapter
    private lateinit var liveMatchesAdapter: FixtureAdapter
    private lateinit var favoritesManager: FavoritesManager

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        favoritesManager = FavoritesManager.getInstance(requireContext())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        setupTransfersRecyclerView()
        setupLiveMatchesRecyclerView()
        setupObservers()
        loadTransfers()
        homeViewModel.loadLiveFixtures()
        
        return root
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onResume() {
        super.onResume()
        // Reload transfers when returning to home (in case favorites changed)
        loadTransfers()
        // Reload live fixtures to get the latest data
        homeViewModel.loadLiveFixtures()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupTransfersRecyclerView() {
        transferAdapter = TransferAdapter()
        binding.recyclerViewNewTransfers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNewTransfers.adapter = transferAdapter
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupLiveMatchesRecyclerView() {
        liveMatchesAdapter = FixtureAdapter { fixtureData ->
            // Navigate to Match fragment using NavController
            val bundle = bundleOf("fixtureId" to fixtureData.fixture.id)
            findNavController().navigate(R.id.action_home2_to_match, bundle)
        }
        binding.recyclerViewLiveMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLiveMatches.adapter = liveMatchesAdapter
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun setupObservers() {
        // Observe transfers
        homeViewModel.transfers.observe(viewLifecycleOwner) { transfers ->
            if (transfers.isNotEmpty()) {
                transferAdapter.submitList(transfers)
                binding.recyclerViewNewTransfers.visibility = View.VISIBLE
                binding.noNewTransfersText.visibility = View.GONE
            } else {
                // Check if user has favorites
                val hasFavoriteTeam = favoritesManager.getFavoriteTeam() != null
                val hasFavoritePlayer = favoritesManager.getFavoritePlayer() != null
                
                if (!hasFavoriteTeam && !hasFavoritePlayer) {
                    // Show prompt to add favorites
                    showAddFavoritesPrompt()
                } else {
                    // Show generic "no transfers" message
                    binding.recyclerViewNewTransfers.visibility = View.GONE
                    binding.noNewTransfersText.visibility = View.VISIBLE
                }
            }
        }
        
        // Observe live fixtures
        homeViewModel.liveFixtures.observe(viewLifecycleOwner) { fixtures ->
            if (fixtures.isNotEmpty()) {
                liveMatchesAdapter.submitList(fixtures)
                binding.recyclerViewLiveMatches.visibility = View.VISIBLE
                binding.noLiveMatchesText.visibility = View.GONE
            } else {
                binding.recyclerViewLiveMatches.visibility = View.GONE
                binding.noLiveMatchesText.visibility = View.VISIBLE
            }
        }
        
        // Observe errors
        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                homeViewModel.clearError()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun loadTransfers() {
        val favoriteTeam = favoritesManager.getFavoriteTeam()
        val favoritePlayer = favoritesManager.getFavoritePlayer()
        
        when {
            favoriteTeam != null -> {
                // Load transfers for favorite team
                homeViewModel.loadTeamTransfers(favoriteTeam.teamData.team.id)
            }
            favoritePlayer != null -> {
                // Load transfers for favorite player
                homeViewModel.loadPlayerTransfers(favoritePlayer.playerData.player.id)
            }
            else -> {
                // No favorites - show prompt
                showAddFavoritesPrompt()
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun showAddFavoritesPrompt() {
        binding.recyclerViewNewTransfers.visibility = View.GONE
        binding.noNewTransfersText.visibility = View.VISIBLE
        binding.noNewTransfersText.text = "Add a favorite team or player to see recent transfers"
        
        // Make the text clickable to open favorites activity
        binding.noNewTransfersText.setOnClickListener {
            val intent = Intent(requireContext(), FavoritesActivity::class.java)
            startActivity(intent)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}