package com.example.healthflow.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.databinding.ItemMoodEntryBinding
import com.example.healthflow.models.MoodEntry

/**
 * Adapter for displaying mood history
 */
class MoodHistoryAdapter(
    private val onDelete: (MoodEntry) -> Unit
) : ListAdapter<MoodEntry, MoodHistoryAdapter.MoodEntryViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodEntryViewHolder {
        val binding = ItemMoodEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoodEntryViewHolder(
        private val binding: ItemMoodEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry) {
            binding.apply {
                // Set mood emoji and label
                tvMoodEmoji.text = moodEntry.mood.emoji
                tvMoodLabel.text = moodEntry.mood.label

                // Set date and time
                tvDateTime.text = "${moodEntry.getFormattedDate()} • ${moodEntry.getFormattedTime()}"

                // Set note
                if (moodEntry.note.isNotEmpty()) {
                    tvNote.visibility = View.VISIBLE
                    tvNote.text = moodEntry.note
                } else {
                    tvNote.visibility = View.GONE
                }

                // Handle delete button
                btnDelete.setOnClickListener {
                    onDelete(moodEntry)
                }
            }
        }
    }

    class MoodDiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
        override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
            return oldItem == newItem
        }
    }
}