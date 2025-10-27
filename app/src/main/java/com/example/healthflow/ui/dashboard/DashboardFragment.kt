package com.example.healthflow.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthflow.R
import com.example.healthflow.data.PreferencesManager
import com.example.healthflow.databinding.FragmentDashboardBinding
import com.example.healthflow.models.Habit
import com.example.healthflow.ui.habits.HabitCompactAdapter
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.fragment.findNavController

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefsManager: PreferencesManager
    private lateinit var habitAdapter: HabitCompactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager.getInstance(requireContext())

        setupGreeting()
        setupRecyclerView()
        setupAchievementsButton()
        loadDashboardData()
    }

    private fun setupGreeting() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greetingText = when (hour) {
            in 0..11 -> "Good Morning! ☀️"
            in 12..16 -> "Good Afternoon! 🌤️"
            in 17..20 -> "Good Evening! 🌆"
            else -> "Good Night! 🌙"
        }

        binding.tvGreeting.text = greetingText
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitCompactAdapter { habit ->
            toggleHabitCompletion(habit)
        }

        binding.rvTodayHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun setupAchievementsButton() {
        binding.btnAchievements.setOnClickListener {
            findNavController().navigate(R.id.achievementsFragment)
        }
    }

    private fun loadDashboardData() {
        val habits = prefsManager.getHabits()

        if (habits.isEmpty()) {
            binding.rvTodayHabits.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
            updateProgressCard(0, 0)
        } else {
            binding.rvTodayHabits.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE

            habitAdapter.submitList(habits)

            val completedCount = habits.count { it.isCompletedToday() }
            val totalCount = habits.size
            updateProgressCard(completedCount, totalCount)
        }
    }

    private fun updateProgressCard(completedCount: Int, totalCount: Int) {
        val percentage = if (totalCount > 0) {
            (completedCount.toFloat() / totalCount.toFloat() * 100).toInt()
        } else {
            0
        }

        // Update circular progress
        binding.circularProgress.setProgress(percentage.toFloat(), animate = true)

        // Update percentage text
        binding.tvProgressPercentage.text = "$percentage%"

        // Update completion info
        binding.tvCompletionInfo.text = "$completedCount of $totalCount habits completed"
    }

    private fun toggleHabitCompletion(habit: Habit) {
        prefsManager.toggleHabitCompletion(habit.id)
        loadDashboardData() // Refresh data
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData() // Refresh when returning to fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}