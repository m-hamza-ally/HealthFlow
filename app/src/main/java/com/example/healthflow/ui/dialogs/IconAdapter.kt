package com.example.healthflow.ui.dialogs

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.R

/**
 * Adapter for icon selection
 */
class IconAdapter(
    private val icons: List<String>,
    private val onIconSelected: (String) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.bind(icons[position], position == selectedPosition)
    }

    override fun getItemCount() = icons.size

    fun selectIcon(icon: String) {
        val position = icons.indexOf(icon)
        if (position != -1) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardIcon)
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)

        fun bind(icon: String, isSelected: Boolean) {
            tvIcon.text = icon

            if (isSelected) {
                cardView.setCardBackgroundColor(Color.parseColor("#6BB6FF"))
                cardView.elevation = 8f
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                cardView.elevation = 2f
            }

            itemView.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onIconSelected(icon)
            }
        }
    }
}