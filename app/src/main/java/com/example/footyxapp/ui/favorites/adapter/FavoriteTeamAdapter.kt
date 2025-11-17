package com.example.footyxapp.ui.favorites.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.TeamData

class FavoriteTeamAdapter(

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private val onTeamClick: (TeamData) -> Unit,
    private val onRemoveClick: (TeamData) -> Unit,
    private val onSetDefaultClick: (TeamData) -> Unit,
    private val isDefaultTeam: (Int) -> Boolean
) : ListAdapter<TeamData, FavoriteTeamAdapter.FavoriteTeamViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_team, parent, false)
        return FavoriteTeamViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: FavoriteTeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class FavoriteTeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teamLogo: ImageView = itemView.findViewById(R.id.imgTeamLogo)
        private val teamName: TextView = itemView.findViewById(R.id.txtTeamName)
        private val teamCountry: TextView = itemView.findViewById(R.id.txtTeamCountry)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
        private val btnSetDefault: ImageButton = itemView.findViewById(R.id.btnSetDefault)
        private val defaultBadge: TextView = itemView.findViewById(R.id.txtDefaultBadge)
        
        fun bind(teamData: TeamData) {
            teamName.text = teamData.team.name
            teamCountry.text = teamData.team.country
            
            val isDefault = isDefaultTeam(teamData.team.id)
            
            // Show/hide default badge
            defaultBadge.visibility = if (isDefault) View.VISIBLE else View.GONE
            
            // Show/hide set default button (hide if already default)
            btnSetDefault.visibility = if (isDefault) View.GONE else View.VISIBLE
            
            Glide.with(itemView.context)
                .load(teamData.team.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(teamLogo)
            
            itemView.setOnClickListener {
                onTeamClick(teamData)
            }
            
            btnRemove.setOnClickListener {
                onRemoveClick(teamData)
            }
            
            btnSetDefault.setOnClickListener {
                onSetDefaultClick(teamData)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TeamData>() {
            override fun areItemsTheSame(oldItem: TeamData, newItem: TeamData): Boolean {
                return oldItem.team.id == newItem.team.id
            }
            
            override fun areContentsTheSame(oldItem: TeamData, newItem: TeamData): Boolean {
                return oldItem == newItem
            }
        }
    }
}
