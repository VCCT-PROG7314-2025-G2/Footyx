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
import com.example.footyxapp.data.model.TeamLeagueData

class TeamLeagueAdapter(
    private val onLeagueClick: (TeamLeagueData) -> Unit
) : ListAdapter<TeamLeagueData, TeamLeagueAdapter.LeagueViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team_league, parent, false)
        return LeagueViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class LeagueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val leagueLogo: ImageView = itemView.findViewById(R.id.imgLeagueLogo)
        private val leagueName: TextView = itemView.findViewById(R.id.txtLeagueName)
        private val leagueType: TextView = itemView.findViewById(R.id.txtLeagueType)
        private val leagueCountry: TextView = itemView.findViewById(R.id.txtLeagueCountry)
        
        fun bind(leagueData: TeamLeagueData) {
            leagueName.text = leagueData.league.name
            leagueType.text = leagueData.league.type
            leagueCountry.text = leagueData.country.name
            
            Glide.with(itemView.context)
                .load(leagueData.league.logo)
                .placeholder(R.drawable.ic_league_placeholder)
                .error(R.drawable.ic_league_placeholder)
                .into(leagueLogo)
            
            itemView.setOnClickListener {
                onLeagueClick(leagueData)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TeamLeagueData>() {
            override fun areItemsTheSame(oldItem: TeamLeagueData, newItem: TeamLeagueData): Boolean {
                return oldItem.league.id == newItem.league.id
            }
            
            override fun areContentsTheSame(oldItem: TeamLeagueData, newItem: TeamLeagueData): Boolean {
                return oldItem == newItem
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
