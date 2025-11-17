package com.example.footyxapp.ui.player.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.footyxapp.R
import com.example.footyxapp.data.model.Statistics

data class TeamLeagueOption(
    val statistics: Statistics,
    val displayName: String
)

class TeamSelectionAdapter(
    context: Context,
    private val options: List<TeamLeagueOption>
) : ArrayAdapter<TeamLeagueOption>(context, 0, options) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_team_selection, parent, false)

        val option = options[position]
        val statistics = option.statistics

        val teamLogo = view.findViewById<ImageView>(R.id.team_logo)
        val teamName = view.findViewById<TextView>(R.id.team_name)
        val leagueName = view.findViewById<TextView>(R.id.league_name)

        // Set team logo
        Glide.with(context)
            .load(statistics.team.logo)
            .placeholder(R.drawable.team_w)
            .error(R.drawable.team_w)
            .into(teamLogo)

        // Set team name
        teamName.text = statistics.team.name

        // Set league name
        leagueName.text = statistics.league.name

        return view
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
