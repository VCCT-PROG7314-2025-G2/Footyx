package com.example.footyxapp.ui.match.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footyxapp.R
import com.example.footyxapp.data.model.FixtureEvent

class EventsAdapter(private val homeTeamId: Int) : ListAdapter<FixtureEvent, EventsAdapter.EventViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match_event, parent, false)
        return EventViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventMinute: TextView = itemView.findViewById(R.id.item_event_minute)
        private val homeEventContainer: LinearLayout = itemView.findViewById(R.id.home_event_container)
        private val awayEventContainer: LinearLayout = itemView.findViewById(R.id.away_event_container)
        private val homeEventPlayer: TextView = itemView.findViewById(R.id.home_event_player)
        private val awayEventPlayer: TextView = itemView.findViewById(R.id.away_event_player)
        private val homeEventIcon: ImageView = itemView.findViewById(R.id.home_event_icon)
        private val awayEventIcon: ImageView = itemView.findViewById(R.id.away_event_icon)
        private val homeEventPlayerOut: TextView = itemView.findViewById(R.id.home_event_player_out)
        private val homeEventPlayerIn: TextView = itemView.findViewById(R.id.home_event_player_in)
        private val awayEventPlayerOut: TextView = itemView.findViewById(R.id.away_event_player_out)
        private val awayEventPlayerIn: TextView = itemView.findViewById(R.id.away_event_player_in)
        
        fun bind(event: FixtureEvent) {
            // Display the minute with extra time if present
            val timeText = if (event.time.extra != null) {
                "${event.time.elapsed}+${event.time.extra}'"
            } else {
                "${event.time.elapsed}'"
            }
            eventMinute.text = timeText
            
            // Determine if this is a home or away team event
            val isHomeTeam = event.team.id == homeTeamId
            val isSubstitution = event.type.equals("subst", ignoreCase = true)
            
            // Show appropriate container (inverted: away team shows on left, home team on right)
            if (!isHomeTeam) {
                homeEventContainer.visibility = View.VISIBLE
                awayEventContainer.visibility = View.GONE
                
                if (isSubstitution) {
                    // For substitution: show player out (left), icon, player in (right)
                    homeEventPlayer.visibility = View.GONE
                    homeEventPlayerOut.visibility = View.VISIBLE
                    homeEventPlayerIn.visibility = View.VISIBLE
                    homeEventPlayerOut.text = event.player.name ?: "Unknown"
                    homeEventPlayerIn.text = event.assist.name ?: "Unknown"
                } else {
                    // For other events: show player name normally
                    homeEventPlayer.visibility = View.VISIBLE
                    homeEventPlayerOut.visibility = View.GONE
                    homeEventPlayerIn.visibility = View.GONE
                    homeEventPlayer.text = event.player.name ?: "Unknown"
                }
                setEventIcon(homeEventIcon, event)
            } else {
                homeEventContainer.visibility = View.GONE
                awayEventContainer.visibility = View.VISIBLE
                
                if (isSubstitution) {
                    // For substitution: show player out (left), icon, player in (right)
                    awayEventPlayer.visibility = View.GONE
                    awayEventPlayerOut.visibility = View.VISIBLE
                    awayEventPlayerIn.visibility = View.VISIBLE
                    awayEventPlayerOut.text = event.player.name ?: "Unknown"
                    awayEventPlayerIn.text = event.assist.name ?: "Unknown"
                } else {
                    // For other events: show player name normally
                    awayEventPlayer.visibility = View.VISIBLE
                    awayEventPlayerOut.visibility = View.GONE
                    awayEventPlayerIn.visibility = View.GONE
                    awayEventPlayer.text = event.player.name ?: "Unknown"
                }
                setEventIcon(awayEventIcon, event)
            }
        }
        
        private fun setEventIcon(imageView: ImageView, event: FixtureEvent) {
            val iconRes = when (event.type.lowercase()) {
                "goal" -> R.drawable.goal // Use goal.png for all goals
                "card" -> {
                    if (event.detail.contains("Yellow", ignoreCase = true)) {
                        R.drawable.ic_yellow_card
                    } else {
                        R.drawable.ic_red_card
                    }
                }
                "subst" -> R.drawable.substitute // Use substitute.png
                else -> R.drawable.goal
            }
            imageView.setImageResource(iconRes)
            
            // Flip the icon for own goals
            if (event.type.lowercase() == "goal" && event.detail.contains("Own Goal", ignoreCase = true)) {
                imageView.scaleX = -1f
            } else {
                imageView.scaleX = 1f
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FixtureEvent>() {
            override fun areItemsTheSame(oldItem: FixtureEvent, newItem: FixtureEvent): Boolean {
                return oldItem.time.elapsed == newItem.time.elapsed && 
                       oldItem.player.id == newItem.player.id &&
                       oldItem.type == newItem.type
            }
            
            override fun areContentsTheSame(oldItem: FixtureEvent, newItem: FixtureEvent): Boolean {
                return oldItem == newItem
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}

