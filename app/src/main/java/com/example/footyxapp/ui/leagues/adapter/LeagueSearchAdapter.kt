package com.example.footyxapp.ui.leagues.adapter

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
import com.example.footyxapp.data.model.LeagueData

class LeagueSearchAdapter(
    private val onLeagueClick: (LeagueData) -> Unit
) : ListAdapter<LeagueData, LeagueSearchAdapter.LeagueViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_league_search, parent, false)
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
        private val leagueCountry: TextView = itemView.findViewById(R.id.txtLeagueCountry)
        private val leagueSeasons: TextView = itemView.findViewById(R.id.txtLeagueSeasons)
        
        fun bind(leagueData: LeagueData) {
            leagueName.text = leagueData.league.name
            leagueCountry.text = leagueData.country.name
            
            // Display available seasons
            val availableSeasons = leagueData.seasons
                .sortedByDescending { it.year }
                .take(5)
                .joinToString(", ") { it.year.toString() }
            leagueSeasons.text = "Seasons: $availableSeasons"
            
            Glide.with(itemView.context)
                .load(leagueData.league.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(leagueLogo)
            
            itemView.setOnClickListener {
                onLeagueClick(leagueData)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LeagueData>() {
            override fun areItemsTheSame(oldItem: LeagueData, newItem: LeagueData): Boolean {
                return oldItem.league.id == newItem.league.id
            }
            
            override fun areContentsTheSame(oldItem: LeagueData, newItem: LeagueData): Boolean {
                return oldItem == newItem
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
