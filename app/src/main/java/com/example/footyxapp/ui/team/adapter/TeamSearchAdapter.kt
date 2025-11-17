package com.example.footyxapp.ui.team.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.TeamData

class TeamSearchAdapter(
    private val onTeamClick: (TeamData) -> Unit
) : ListAdapter<TeamData, TeamSearchAdapter.TeamViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team_search, parent, false)
        return TeamViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teamLogo: ImageView = itemView.findViewById(R.id.imgTeamLogo)
        private val teamName: TextView = itemView.findViewById(R.id.txtTeamName)
        private val teamCountry: TextView = itemView.findViewById(R.id.txtTeamCountry)
        private val teamFounded: TextView = itemView.findViewById(R.id.txtTeamFounded)
        
        fun bind(teamData: TeamData) {
            teamName.text = teamData.team.name
            teamCountry.text = teamData.team.country
            
            if (teamData.team.founded != null) {
                teamFounded.text = "Founded: ${teamData.team.founded}"
                teamFounded.visibility = View.VISIBLE
            } else {
                teamFounded.visibility = View.GONE
            }
            
            Glide.with(itemView.context)
                .load(teamData.team.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(teamLogo)
            
            itemView.setOnClickListener {
                onTeamClick(teamData)
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

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
