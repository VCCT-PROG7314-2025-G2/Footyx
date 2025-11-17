package com.example.footyxapp.ui.match.adapter

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
import com.example.footyxapp.data.model.FixtureData
import java.text.SimpleDateFormat
import java.util.*

class FixtureAdapter(
    private val onFixtureClick: (FixtureData) -> Unit
) : ListAdapter<FixtureData, FixtureAdapter.FixtureViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fixture, parent, false)
        return FixtureViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: FixtureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class FixtureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val homeTeamLogo: ImageView = itemView.findViewById(R.id.home_team_logo)
        private val homeTeamName: TextView = itemView.findViewById(R.id.home_team_name)
        private val awayTeamLogo: ImageView = itemView.findViewById(R.id.away_team_logo)
        private val awayTeamName: TextView = itemView.findViewById(R.id.away_team_name)
        private val score: TextView = itemView.findViewById(R.id.match_score)
        private val status: TextView = itemView.findViewById(R.id.match_status)
        private val date: TextView = itemView.findViewById(R.id.match_date)
        
        fun bind(fixture: FixtureData) {
            homeTeamName.text = fixture.teams.home.name
            awayTeamName.text = fixture.teams.away.name
            
            // Load team logos
            Glide.with(itemView.context)
                .load(fixture.teams.home.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(homeTeamLogo)
            
            Glide.with(itemView.context)
                .load(fixture.teams.away.logo)
                .placeholder(R.drawable.ic_team_placeholder)
                .error(R.drawable.ic_team_placeholder)
                .into(awayTeamLogo)
            
            // Display score
            val homeGoals = fixture.goals.home ?: "-"
            val awayGoals = fixture.goals.away ?: "-"
            score.text = "$homeGoals - $awayGoals"
            
            // Display status
            status.text = fixture.fixture.status.long
            
            // Format and display date
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault())
                val parsedDate = inputFormat.parse(fixture.fixture.date)
                date.text = if (parsedDate != null) outputFormat.format(parsedDate) else fixture.fixture.date
            } catch (e: Exception) {
                date.text = fixture.fixture.date
            }
            
            // Set click listener
            itemView.setOnClickListener {
                onFixtureClick(fixture)
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FixtureData>() {
            override fun areItemsTheSame(oldItem: FixtureData, newItem: FixtureData): Boolean {
                return oldItem.fixture.id == newItem.fixture.id
            }
            
            override fun areContentsTheSame(oldItem: FixtureData, newItem: FixtureData): Boolean {
                return oldItem == newItem
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
