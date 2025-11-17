package com.example.footyxapp.ui.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.PlayerProfile
import com.example.footyxapp.databinding.ItemPlayerSearchBinding

class PlayerSearchAdapter(
    private val onPlayerClick: (PlayerProfile) -> Unit
) : ListAdapter<PlayerProfile, PlayerSearchAdapter.PlayerSearchViewHolder>(PlayerDiffCallback()) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerSearchViewHolder {
        val binding = ItemPlayerSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlayerSearchViewHolder(binding, onPlayerClick)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: PlayerSearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    class PlayerSearchViewHolder(
        private val binding: ItemPlayerSearchBinding,
        private val onPlayerClick: (PlayerProfile) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playerProfile: PlayerProfile) {
            val player = playerProfile.player
            
            binding.apply {
                textPlayerName.text = player.name
                textPlayerPosition.text = player.position ?: "Unknown Position"
                textPlayerNationality.text = player.nationality ?: "Unknown"
                textPlayerAge.text = if (player.age != null) "${player.age} years" else "Age unknown"
                
                // Load player photo
                Glide.with(itemView.context)
                    .load(player.photo)
                    .placeholder(R.drawable.player_w)
                    .error(R.drawable.player_w)
                    .into(imagePlayerPhoto)
                
                root.setOnClickListener {
                    onPlayerClick(playerProfile)
                }
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private class PlayerDiffCallback : DiffUtil.ItemCallback<PlayerProfile>() {
        override fun areItemsTheSame(oldItem: PlayerProfile, newItem: PlayerProfile): Boolean {
            return oldItem.player.id == newItem.player.id
        }

        override fun areContentsTheSame(oldItem: PlayerProfile, newItem: PlayerProfile): Boolean {
            return oldItem == newItem
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
