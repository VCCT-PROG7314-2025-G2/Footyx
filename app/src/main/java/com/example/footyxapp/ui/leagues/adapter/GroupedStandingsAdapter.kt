package com.example.footyxapp.ui.leagues.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.TeamStanding

sealed class StandingItem {
    data class GroupHeader(val groupName: String) : StandingItem()
    data class TeamItem(val standing: TeamStanding) : StandingItem()
}

class GroupedStandingsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<StandingItem>()

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_TEAM = 1
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    fun submitList(standings: List<TeamStanding>) {
        // Group standings by their group field
        val groupedStandings = standings.groupBy { it.group }
        
        // Create list with headers and teams
        val newItems = mutableListOf<StandingItem>()
        groupedStandings.forEach { (groupName, teams) ->
            newItems.add(StandingItem.GroupHeader(groupName))
            teams.forEach { team ->
                newItems.add(StandingItem.TeamItem(team))
            }
        }
        
        items = newItems
        notifyDataSetChanged()
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is StandingItem.GroupHeader -> VIEW_TYPE_HEADER
            is StandingItem.TeamItem -> VIEW_TYPE_TEAM
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_group_header, parent, false)
                GroupHeaderViewHolder(view)
            }
            VIEW_TYPE_TEAM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_standing_row, parent, false)
                TeamViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is StandingItem.GroupHeader -> (holder as GroupHeaderViewHolder).bind(item.groupName)
            is StandingItem.TeamItem -> (holder as TeamViewHolder).bind(item.standing)
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun getItemCount(): Int = items.size

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class GroupHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupNameText: TextView = itemView.findViewById(R.id.group_name_text)
        
        fun bind(groupName: String) {
            groupNameText.text = groupName
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

}
