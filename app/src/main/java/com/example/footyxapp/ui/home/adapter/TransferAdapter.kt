package com.example.footyxapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.databinding.ItemNewTransferBinding
import com.example.footyxapp.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

class TransferAdapter : ListAdapter<HomeViewModel.TransferWithPlayer, TransferAdapter.TransferViewHolder>(TransferDiffCallback()) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        val binding = ItemNewTransferBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransferViewHolder(binding)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    class TransferViewHolder(
        private val binding: ItemNewTransferBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transferWithPlayer: HomeViewModel.TransferWithPlayer) {
            val transfer = transferWithPlayer.transfer
            
            with(binding) {
                // Player name
                textPlayerNameTransfer.text = transferWithPlayer.playerName
                
                // Transfer type
                val typeText = transfer.type?.uppercase() ?: "N/A"
                textTransferType.text = typeText
                
                // From team
                textFromTeamName.text = transfer.teams.out.name
                Glide.with(root.context)
                    .load(transfer.teams.out.logo)
                    .placeholder(R.drawable.ic_team_placeholder)
                    .into(imageFromTeamLogo)
                
                // To team
                textToTeamName.text = transfer.teams.`in`.name
                Glide.with(root.context)
                    .load(transfer.teams.`in`.logo)
                    .placeholder(R.drawable.ic_team_placeholder)
                    .into(imageToTeamLogo)
                
                // Transfer date
                textTransferDate.text = formatDate(transfer.date)
                
                // Transfer fee - API doesn't provide fee, so show type
                textTransferFee.text = "Type: $typeText"
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    class TransferDiffCallback : DiffUtil.ItemCallback<HomeViewModel.TransferWithPlayer>() {
        override fun areItemsTheSame(
            oldItem: HomeViewModel.TransferWithPlayer,
            newItem: HomeViewModel.TransferWithPlayer
        ): Boolean {
            return oldItem.playerName == newItem.playerName && 
                   oldItem.transfer.date == newItem.transfer.date
        }

        override fun areContentsTheSame(
            oldItem: HomeViewModel.TransferWithPlayer,
            newItem: HomeViewModel.TransferWithPlayer
        ): Boolean {
            return oldItem == newItem
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
