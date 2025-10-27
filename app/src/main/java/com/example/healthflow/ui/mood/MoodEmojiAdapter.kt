package com.example.healthflow.ui.mood

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.databinding.ItemMoodEmojiBinding
import com.example.healthflow.models.MoodType

/**
 * Adapter for mood emoji selection
 */
class MoodEmojiAdapter(
    private val moods: List<MoodType>,
    private val onMoodSelected: (MoodType) -> Unit
) : RecyclerView.Adapter<MoodEmojiAdapter.MoodEmojiViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEmojiViewHolder {
        val binding = ItemMoodEmojiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodEmojiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodEmojiViewHolder, position: Int) {
        holder.bind(moods[position], position == selectedPosition)
    }

    override fun getItemCount() = moods.size

    fun clearSelection() {
        val oldPosition = selectedPosition
        selectedPosition = -1
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition)
        }
    }

    inner class MoodEmojiViewHolder(
        private val binding: ItemMoodEmojiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mood: MoodType, isSelected: Boolean) {
            binding.apply {
                tvMoodEmoji.text = mood.emoji
                tvMoodLabel.text = mood.label

                // Update card appearance based on selection
                if (isSelected) {
                    try {
                        cardMood.setCardBackgroundColor(Color.parseColor(mood.color))
                    } catch (e: Exception) {
                        cardMood.setCardBackgroundColor(Color.parseColor("#6BB6FF"))
                    }
                    cardMood.elevation = 8f
                } else {
                    cardMood.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                    cardMood.elevation = 2f
                }

                // Handle click
                root.setOnClickListener {
                    val oldPosition = selectedPosition
                    selectedPosition = adapterPosition

                    if (oldPosition != -1) {
                        notifyItemChanged(oldPosition)
                    }
                    notifyItemChanged(selectedPosition)

                    onMoodSelected(mood)
                }
            }
        }
    }
}