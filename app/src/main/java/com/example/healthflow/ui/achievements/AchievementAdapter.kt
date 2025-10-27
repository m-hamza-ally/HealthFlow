package com.example.healthflow.ui.achievements

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthflow.databinding.ItemAchievementBinding
import com.example.healthflow.models.Achievement
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying achievements
 */
class AchievementAdapter(
    private val achievements: List<Achievement>,
    private val getProgress: (Achievement) -> Int
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount() = achievements.size

    inner class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            binding.apply {
                // Set achievement details
                tvAchievementIcon.text = achievement.icon
                tvAchievementTitle.text = achievement.title
                tvAchievementDesc.text = achievement.description

                if (achievement.isUnlocked) {
                    // Unlocked state
                    viewLockOverlay.visibility = View.GONE
                    tvStatus.text = "✅ Unlocked"
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                    tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"))

                    // Show unlock date
                    achievement.unlockedDate?.let { date ->
                        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        tvProgress.text = "Unlocked on ${sdf.format(Date(date))}"
                        tvProgress.setTextColor(Color.parseColor("#4CAF50"))
                    }
                } else {
                    // Locked state
                    viewLockOverlay.visibility = View.VISIBLE
                    tvStatus.text = "🔒 Locked"
                    tvStatus.setTextColor(Color.parseColor("#9E9E9E"))
                    tvStatus.setBackgroundColor(Color.parseColor("#F5F5F5"))

                    // Show progress
                    val currentProgress = getProgress(achievement)
                    val percentage = ((currentProgress.toFloat() / achievement.requirement.toFloat()) * 100).toInt()
                    tvProgress.text = "Progress: $currentProgress / ${achievement.requirement} (${percentage}%)"
                    tvProgress.setTextColor(Color.parseColor("#6BB6FF"))
                }
            }
        }
    }
}