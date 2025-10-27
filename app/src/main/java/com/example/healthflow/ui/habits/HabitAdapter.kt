package com.example.healthflow.ui.habits

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.databinding.ItemHabitBinding
import com.example.healthflow.models.Habit

/**
 * Adapter for displaying habits in full format (Habits Screen)
 */
class HabitAdapter(
    private val onHabitComplete: (Habit) -> Unit,
    private val onHabitOptions: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
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
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                // Set habit details
                tvHabitName.text = habit.name
                tvHabitIcon.text = habit.icon

                // Set icon background color
                val background = tvHabitIcon.background as GradientDrawable
                try {
                    background.setColor(Color.parseColor(habit.color))
                } catch (e: Exception) {
                    background.setColor(Color.parseColor("#6BB6FF"))
                }

                // Set streak
                val streak = habit.getStreak()
                tvStreak.text = if (streak > 0) {
                    "🔥 $streak day streak"
                } else {
                    "Start your streak!"
                }

                // Set completion status
                val isCompleted = habit.isCompletedToday()
                if (isCompleted) {
                    tvCompletionStatus.text = "✅ Completed today"
                    tvCompletionStatus.setTextColor(Color.parseColor("#7ED957"))
                    btnComplete.text = "Undo"
                    btnComplete.icon = null
                } else {
                    tvCompletionStatus.text = "Not completed today"
                    tvCompletionStatus.setTextColor(Color.parseColor("#8E8EA9"))
                    btnComplete.text = "Mark Complete"
                    btnComplete.setIconResource(com.example.healthflow.R.drawable.ic_check)
                }

                // Handle complete button click
                btnComplete.setOnClickListener {
                    onHabitComplete(habit)
                }

                // Handle more options click
                btnMore.setOnClickListener {
                    onHabitOptions(habit)
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