package com.example.footyxapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footyxapp.data.model.Transfer
import com.example.footyxapp.data.model.FixtureData
import com.example.footyxapp.data.repository.TransfersRepository
import com.example.footyxapp.data.repository.MatchRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val transfersRepository = TransfersRepository()
    private val matchRepository = MatchRepository()
    
    // LiveData for transfers with player info
    private val _transfers = MutableLiveData<List<TransferWithPlayer>>()
    val transfers: LiveData<List<TransferWithPlayer>> = _transfers
    
    // LiveData for loading state
    private val _isLoadingTransfers = MutableLiveData<Boolean>()
    val isLoadingTransfers: LiveData<Boolean> = _isLoadingTransfers
    
    // LiveData for errors
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // LiveData for live fixtures
    private val _liveFixtures = MutableLiveData<List<FixtureData>>()
    val liveFixtures: LiveData<List<FixtureData>> = _liveFixtures
    
    // LiveData for loading state of live fixtures
    private val _isLoadingLiveFixtures = MutableLiveData<Boolean>()
    val isLoadingLiveFixtures: LiveData<Boolean> = _isLoadingLiveFixtures

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadLiveFixtures() {
        viewModelScope.launch {
            _isLoadingLiveFixtures.value = true
            
            matchRepository.getLiveFixtures().fold(
                onSuccess = { response ->
                    _liveFixtures.value = response.response
                    _isLoadingLiveFixtures.value = false
                },
                onFailure = { exception ->
                    _error.value = mapError(exception)
                    _liveFixtures.value = emptyList()
                    _isLoadingLiveFixtures.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadPlayerTransfers(playerId: Int) {
        viewModelScope.launch {
            _isLoadingTransfers.value = true
            
            transfersRepository.getPlayerTransfers(playerId).fold(
                onSuccess = { response ->
                    val transferList = mutableListOf<TransferWithPlayer>()
                    response.response.forEach { transferData ->
                        transferData.transfers.forEach { transfer ->
                            transferList.add(
                                TransferWithPlayer(
                                    playerName = transferData.player.name,
                                    transfer = transfer
                                )
                            )
                        }
                    }
                    _transfers.value = transferList.take(10) // Show latest 10
                    _isLoadingTransfers.value = false
                },
                onFailure = { exception ->
                    _error.value = mapError(exception)
                    _transfers.value = emptyList()
                    _isLoadingTransfers.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun loadTeamTransfers(teamId: Int) {
        viewModelScope.launch {
            _isLoadingTransfers.value = true
            
            transfersRepository.getTeamTransfers(teamId).fold(
                onSuccess = { response ->
                    val transferList = mutableListOf<TransferWithPlayer>()
                    response.response.forEach { transferData ->
                        transferData.transfers.forEach { transfer ->
                            transferList.add(
                                TransferWithPlayer(
                                    playerName = transferData.player.name,
                                    transfer = transfer
                                )
                            )
                        }
                    }
                    // Sort by date descending and take latest 10
                    val sortedTransfers = transferList.sortedByDescending { it.transfer.date }.take(10)
                    _transfers.value = sortedTransfers
                    _isLoadingTransfers.value = false
                },
                onFailure = { exception ->
                    _error.value = mapError(exception)
                    _transfers.value = emptyList()
                    _isLoadingTransfers.value = false
                }
            )
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun clearError() {
        _error.value = null
    }

    private fun mapError(exception: Throwable): String {
        val msg = exception.message ?: ""
        return when {
            msg.contains("rate limit", true) -> "Too many requests. Please wait and try again."
            msg.contains("Expected BEGIN_ARRAY", true) -> "Data format error from server. Please try again later."
            msg.contains("timeout", true) || msg.contains("connection", true) -> "Network error. Check your connection."
            else -> "Unable to load data. Please try again."
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    // Data class to combine player name with transfer
    data class TransferWithPlayer(
        val playerName: String,
        val transfer: Transfer
    )

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}