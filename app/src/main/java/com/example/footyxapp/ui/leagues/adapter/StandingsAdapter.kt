package com.example.footyxapp.ui.leagues.adapter

import android.graphics.Color
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
import com.example.footyxapp.data.model.TeamStanding

class StandingsAdapter : ListAdapter<TeamStanding, StandingsAdapter.StandingViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_standing_row, parent, false)
        return StandingViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: StandingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class StandingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rankIndicator: View = itemView.findViewById(R.id.rank_indicator)
        private val textRank: TextView = itemView.findViewById(R.id.text_rank)
        private val teamLogo: ImageView = itemView.findViewById(R.id.team_logo_row)
        private val teamName: TextView = itemView.findViewById(R.id.team_name_row)
        private val textPlayed: TextView = itemView.findViewById(R.id.text_played_row)
        private val textGoalsDiff: TextView = itemView.findViewById(R.id.text_goals_diff_row)
        private val textPoints: TextView = itemView.findViewById(R.id.text_points_row)
        
        fun bind(standing: TeamStanding) {
            textRank.text = standing.rank.toString()
            teamName.text = standing.team.name
            textPlayed.text = standing.all.played.toString()
            
            // Format goal difference with + or - sign
            val goalDiffText = if (standing.goalsDiff > 0) {
                "+${standing.goalsDiff}"
            } else {
                standing.goalsDiff.toString()
            }
            textGoalsDiff.text = goalDiffText
            
            textPoints.text = standing.points.toString()
            
            // Set rank indicator color based on description
            val indicatorColor = when {
                standing.description?.contains("Champions League", ignoreCase = true) == true -> "#4CAF50" // Green
                standing.description?.contains("Europa League", ignoreCase = true) == true -> "#FF9800" // Orange
                standing.description?.contains("Conference", ignoreCase = true) == true -> "#2196F3" // Blue
                standing.description?.contains("Relegation", ignoreCase = true) == true -> "#F44336" // Red
                else -> "#757575" // Gray
            }
            rankIndicator.setBackgroundColor(Color.parseColor(indicatorColor))
            
            Glide.with(itemView.context)
                .load(standing.team.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(teamLogo)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<TeamStanding>() {
            override fun areItemsTheSame(oldItem: TeamStanding, newItem: TeamStanding): Boolean {
                return oldItem.team.id == newItem.team.id
            }
            
            override fun areContentsTheSame(oldItem: TeamStanding, newItem: TeamStanding): Boolean {
                return oldItem == newItem
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
