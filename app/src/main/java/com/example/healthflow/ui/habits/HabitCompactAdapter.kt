package com.example.healthflow.ui.habits

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.databinding.ItemHabitCompactBinding
import com.example.healthflow.models.Habit

/**
 * Adapter for displaying habits in compact format (Dashboard)
 */
class HabitCompactAdapter(
    private val onHabitClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitCompactAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitCompactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HabitViewHolder(
        private val binding: ItemHabitCompactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                // Set habit name
                tvHabitName.text = habit.name

                // Set habit icon
                tvHabitIcon.text = habit.icon

                // Set icon background color
                val background = tvHabitIcon.background as GradientDrawable
                try {
                    background.setColor(Color.parseColor(habit.color))
                } catch (e: Exception) {
                    // Fallback color if parsing fails
                    background.setColor(Color.parseColor("#6BB6FF"))
                }

                // Set checkbox state WITHOUT triggering listener
                checkboxComplete.setOnCheckedChangeListener(null) // Remove listener first
                checkboxComplete.isChecked = habit.isCompletedToday()

                // Handle checkbox click ONLY
                checkboxComplete.setOnCheckedChangeListener { _, _ ->
                    onHabitClick(habit)
                }

                // Handle card click - toggle checkbox
                root.setOnClickListener {
                    checkboxComplete.toggle() // This will trigger the checkbox listener
                }
            }
        }
    }

    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}