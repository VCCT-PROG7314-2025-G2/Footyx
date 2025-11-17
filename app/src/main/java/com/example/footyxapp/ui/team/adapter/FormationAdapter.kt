package com.example.footyxapp.ui.team.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.footyxapp.R
import com.example.footyxapp.data.model.TeamLineup

data class FormationWithPercentage(
    val formation: String,
    val played: Int,
    val percentage: Float
)

class FormationAdapter : ListAdapter<FormationWithPercentage, FormationAdapter.FormationViewHolder>(DiffCallback) {

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_formation, parent, false)
        return FormationViewHolder(view)
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    override fun onBindViewHolder(holder: FormationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    inner class FormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtFormation: TextView = itemView.findViewById(R.id.txtFormation)
        private val txtGamesPlayed: TextView = itemView.findViewById(R.id.txtGamesPlayed)
        private val txtPercentage: TextView = itemView.findViewById(R.id.txtPercentage)
        private val progressBar: View = itemView.findViewById(R.id.progressBar)
        
        fun bind(formationData: FormationWithPercentage) {
            txtFormation.text = formationData.formation
            txtGamesPlayed.text = "${formationData.played} games"
            txtPercentage.text = "${formationData.percentage.toInt()}%"
            
            // Set progress bar width based on percentage
            val layoutParams = progressBar.layoutParams
            val maxWidth = 100 * itemView.resources.displayMetrics.density.toInt() // 100dp in pixels
            layoutParams.width = (maxWidth * (formationData.percentage / 100f)).toInt()
            progressBar.layoutParams = layoutParams
            
            // Create gradient background for progress bar
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.cornerRadius = 2 * itemView.resources.displayMetrics.density // 2dp
            
            // Color based on usage percentage
            val color = when {
                formationData.percentage >= 50f -> ContextCompat.getColor(itemView.context, R.color.primary_green)
                formationData.percentage >= 25f -> ContextCompat.getColor(itemView.context, R.color.yellow_500)
                else -> ContextCompat.getColor(itemView.context, R.color.red_500)
            }
            
            drawable.setColor(color)
            progressBar.background = drawable
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FormationWithPercentage>() {
            override fun areItemsTheSame(oldItem: FormationWithPercentage, newItem: FormationWithPercentage): Boolean {
                return oldItem.formation == newItem.formation
            }
            
            override fun areContentsTheSame(oldItem: FormationWithPercentage, newItem: FormationWithPercentage): Boolean {
                return oldItem == newItem
            }
        }
    }

    //°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°//

}
