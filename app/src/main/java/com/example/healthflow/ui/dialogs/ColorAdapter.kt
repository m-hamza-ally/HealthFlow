package com.example.healthflow.ui.dialogs

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.R

/**
 * Adapter for color selection with visual selection marker
 */
class ColorAdapter(
    private val colors: List<String>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position == selectedPosition)
    }

    override fun getItemCount() = colors.size

    /**
     * Select a color programmatically (useful when editing existing habit)
     */
    fun selectColor(color: String) {
        val position = colors.indexOf(color)
        if (position != -1 && position != selectedPosition) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    /**
     * Get currently selected color
     */
    fun getSelectedColor(): String = colors[selectedPosition]

    /**
     * Set selected position by index
     */
    fun setSelectedPosition(position: Int) {
        if (position in colors.indices && position != selectedPosition) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardColor: CardView = itemView.findViewById(R.id.cardColor)
        private val viewCheckmark: ImageView = itemView.findViewById(R.id.viewCheckmark)
        private val viewSelectionRing: View = itemView.findViewById(R.id.viewSelectionRing)

        fun bind(color: String, isSelected: Boolean) {
            // Set card background color
            try {
                val parsedColor = Color.parseColor(color)
                cardColor.setCardBackgroundColor(parsedColor)

                // Adjust checkmark color based on background brightness
                adjustCheckmarkColor(parsedColor)
            } catch (e: Exception) {
                // Fallback to default color if parsing fails
                cardColor.setCardBackgroundColor(Color.parseColor("#6BB6FF"))
            }

            // Show/hide selection indicators
            viewCheckmark.visibility = if (isSelected) View.VISIBLE else View.GONE
            viewSelectionRing.visibility = if (isSelected) View.VISIBLE else View.GONE

            // Handle click
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition

                    // Update both items
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)

                    // Notify listener
                    onColorSelected(color)
                }
            }
        }

        /**
         * Adjust checkmark color to ensure visibility on different backgrounds
         */
        private fun adjustCheckmarkColor(backgroundColor: Int) {
            val red = Color.red(backgroundColor)
            val green = Color.green(backgroundColor)
            val blue = Color.blue(backgroundColor)

            // Calculate perceived brightness (0-255)
            val brightness = (0.299 * red + 0.587 * green + 0.114 * blue)

            // Use dark checkmark for light backgrounds, white for dark backgrounds
            if (brightness > 186) {
                viewCheckmark.setColorFilter(Color.parseColor("#424242"))
            } else {
                viewCheckmark.setColorFilter(Color.WHITE)
            }
        }
    }
}