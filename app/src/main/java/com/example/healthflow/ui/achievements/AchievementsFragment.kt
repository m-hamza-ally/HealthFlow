package com.example.healthflow.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthflow.data.PreferencesManager
import com.example.healthflow.databinding.FragmentAchievementsBinding
import com.example.healthflow.models.Achievement
import com.example.healthflow.models.AchievementCategory
import com.example.healthflow.models.AchievementsList

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefsManager: PreferencesManager
    private val achievements = AchievementsList.achievements.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager.getInstance(requireContext())

        checkAchievements()
        setupRecyclerViews()
        updateSummary()
    }

    private fun checkAchievements() {
        val habits = prefsManager.getHabits()
        val moods = prefsManager.getMoodEntries()
        val steps = prefsManager.getTodaySteps()

        // Check habit achievements
        val habitCount = habits.size
        unlockAchievementIf("first_habit", habitCount >= 1)
        unlockAchievementIf("habit_master", habitCount >= 5)
        unlockAchievementIf("habit_champion", habitCount >= 10)

        // Check streak achievements
        val maxStreak = habits.maxOfOrNull { it.getStreak() } ?: 0
        unlockAchievementIf("streak_3", maxStreak >= 3)
        unlockAchievementIf("streak_7", maxStreak >= 7)
        unlockAchievementIf("streak_30", maxStreak >= 30)
        unlockAchievementIf("streak_100", maxStreak >= 100)

        // Check mood achievements
        val moodCount = moods.size
        unlockAchievementIf("first_mood", moodCount >= 1)
        unlockAchievementIf("mood_50", moodCount >= 50)

        // Check consecutive mood days
        val consecutiveDays = calculateConsecutiveMoodDays(moods)
        unlockAchievementIf("mood_week", consecutiveDays >= 7)

        // Check step achievements
        unlockAchievementIf("steps_1k", steps >= 1000)
        unlockAchievementIf("steps_5k", steps >= 5000)
        unlockAchievementIf("steps_10k", steps >= 10000)
    }

    private fun unlockAchievementIf(achievementId: String, condition: Boolean) {
        val achievement = achievements.find { it.id == achievementId }
        if (achievement != null && !achievement.isUnlocked && condition) {
            achievement.isUnlocked = true
            achievement.unlockedDate = System.currentTimeMillis()
        }
    }

    private fun calculateConsecutiveMoodDays(moods: List<com.example.healthflow.models.MoodEntry>): Int {
        if (moods.isEmpty()) return 0

        val dateKeys = moods.map { it.getDateKey() }.distinct().sorted().reversed()
        var consecutive = 0
        var expectedDate = getCurrentDateKey()

        for (dateKey in dateKeys) {
            if (dateKey == expectedDate) {
                consecutive++
                expectedDate = getPreviousDateKey(expectedDate)
            } else {
                break
            }
        }

        return consecutive
    }

    private fun getCurrentDateKey(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    private fun getPreviousDateKey(dateKey: String): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val date = sdf.parse(dateKey) ?: return dateKey
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        return sdf.format(calendar.time)
    }

    private fun setupRecyclerViews() {
        // Habit achievements
        val habitAchievements = achievements.filter { it.category == AchievementCategory.HABITS }
        val habitAdapter = AchievementAdapter(habitAchievements) { achievement ->
            calculateProgress(achievement)
        }
        binding.rvHabitAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }

        // Streak achievements
        val streakAchievements = achievements.filter { it.category == AchievementCategory.STREAKS }
        val streakAdapter = AchievementAdapter(streakAchievements) { achievement ->
            calculateProgress(achievement)
        }
        binding.rvStreakAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = streakAdapter
        }

        // Mood achievements
        val moodAchievements = achievements.filter { it.category == AchievementCategory.MOOD }
        val moodAdapter = AchievementAdapter(moodAchievements) { achievement ->
            calculateProgress(achievement)
        }
        binding.rvMoodAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
        }

        // Step achievements
        val stepAchievements = achievements.filter { it.category == AchievementCategory.STEPS }
        val stepAdapter = AchievementAdapter(stepAchievements) { achievement ->
            calculateProgress(achievement)
        }
        binding.rvStepAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stepAdapter
        }
    }

    private fun calculateProgress(achievement: Achievement): Int {
        return when (achievement.category) {
            AchievementCategory.HABITS -> {
                prefsManager.getHabits().size
            }
            AchievementCategory.STREAKS -> {
                prefsManager.getHabits().maxOfOrNull { it.getStreak() } ?: 0
            }
            AchievementCategory.MOOD -> {
                if (achievement.id == "mood_week") {
                    calculateConsecutiveMoodDays(prefsManager.getMoodEntries())
                } else {
                    prefsManager.getMoodEntries().size
                }
            }
            AchievementCategory.STEPS -> {
                prefsManager.getTodaySteps()
            }
        }
    }

    private fun updateSummary() {
        val unlockedCount = achievements.count { it.isUnlocked }
        val totalCount = achievements.size
        binding.tvUnlockedCount.text = "$unlockedCount / $totalCount"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}