package com.example.healthflow.models

/**
 * Achievement data model
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val requirement: Int,
    val category: AchievementCategory,
    var isUnlocked: Boolean = false,
    var unlockedDate: Long? = null
)

enum class AchievementCategory {
    HABITS,
    STREAKS,
    MOOD,
    STEPS
}

object AchievementsList {
    val achievements = listOf(
        // Habit Achievements
        Achievement(
            id = "first_habit",
            title = "Getting Started",
            description = "Create your first habit",
            icon = "🌱",
            requirement = 1,
            category = AchievementCategory.HABITS
        ),
        Achievement(
            id = "habit_master",
            title = "Habit Master",
            description = "Create 5 different habits",
            icon = "⭐",
            requirement = 5,
            category = AchievementCategory.HABITS
        ),
        Achievement(
            id = "habit_champion",
            title = "Habit Champion",
            description = "Create 10 different habits",
            icon = "🏆",
            requirement = 10,
            category = AchievementCategory.HABITS
        ),

        // Streak Achievements
        Achievement(
            id = "streak_3",
            title = "Three in a Row",
            description = "Maintain a 3-day streak",
            icon = "🔥",
            requirement = 3,
            category = AchievementCategory.STREAKS
        ),
        Achievement(
            id = "streak_7",
            title = "Week Warrior",
            description = "Maintain a 7-day streak",
            icon = "💪",
            requirement = 7,
            category = AchievementCategory.STREAKS
        ),
        Achievement(
            id = "streak_30",
            title = "Monthly Marvel",
            description = "Maintain a 30-day streak",
            icon = "🎯",
            requirement = 30,
            category = AchievementCategory.STREAKS
        ),
        Achievement(
            id = "streak_100",
            title = "Century Club",
            description = "Maintain a 100-day streak",
            icon = "💎",
            requirement = 100,
            category = AchievementCategory.STREAKS
        ),

        // Mood Achievements
        Achievement(
            id = "first_mood",
            title = "Mood Tracker",
            description = "Log your first mood",
            icon = "😊",
            requirement = 1,
            category = AchievementCategory.MOOD
        ),
        Achievement(
            id = "mood_week",
            title = "Emotional Awareness",
            description = "Log moods for 7 consecutive days",
            icon = "🧠",
            requirement = 7,
            category = AchievementCategory.MOOD
        ),
        Achievement(
            id = "mood_50",
            title = "Mood Master",
            description = "Log 50 mood entries",
            icon = "🎭",
            requirement = 50,
            category = AchievementCategory.MOOD
        ),

        // Step Achievements
        Achievement(
            id = "steps_1k",
            title = "First Steps",
            description = "Walk 1,000 steps in a day",
            icon = "👣",
            requirement = 1000,
            category = AchievementCategory.STEPS
        ),
        Achievement(
            id = "steps_5k",
            title = "Active Walker",
            description = "Walk 5,000 steps in a day",
            icon = "🚶",
            requirement = 5000,
            category = AchievementCategory.STEPS
        ),
        Achievement(
            id = "steps_10k",
            title = "Step Champion",
            description = "Walk 10,000 steps in a day",
            icon = "🏃",
            requirement = 10000,
            category = AchievementCategory.STEPS
        )
    )
}