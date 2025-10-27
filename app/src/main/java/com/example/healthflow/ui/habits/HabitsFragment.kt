package com.example.healthflow.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthflow.data.PreferencesManager
import com.example.healthflow.databinding.FragmentHabitsBinding
import com.example.healthflow.models.Habit
import com.example.healthflow.ui.dialogs.AddEditHabitDialog
import com.example.healthflow.ui.dialogs.HabitOptionsDialog

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefsManager: PreferencesManager
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager.getInstance(requireContext())

        setupRecyclerView()
        setupFab()
        loadHabits()
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            onHabitComplete = { habit ->
                toggleHabitCompletion(habit)
            },
            onHabitOptions = { habit ->
                showHabitOptions(habit)
            }
        )

        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }

    private fun loadHabits() {
        val habits = prefsManager.getHabits()

        if (habits.isEmpty()) {
            binding.rvHabits.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvHabits.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            habitAdapter.submitList(habits)
        }
    }

    private fun toggleHabitCompletion(habit: Habit) {
        prefsManager.toggleHabitCompletion(habit.id)
        loadHabits()
    }

    private fun showAddHabitDialog() {
        val dialog = AddEditHabitDialog(
            habit = null,
            onSave = { habit ->
                prefsManager.addHabit(habit)
                loadHabits()
            }
        )
        dialog.show(childFragmentManager, "AddHabitDialog")
    }

    private fun showHabitOptions(habit: Habit) {
        val dialog = HabitOptionsDialog(
            habit = habit,
            onEdit = {
                showEditHabitDialog(habit)
            },
            onDelete = {
                deleteHabit(habit)
            }
        )
        dialog.show(childFragmentManager, "HabitOptionsDialog")
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialog = AddEditHabitDialog(
            habit = habit,
            onSave = { updatedHabit ->
                prefsManager.updateHabit(updatedHabit)
                loadHabits()
            }
        )
        dialog.show(childFragmentManager, "EditHabitDialog")
    }

    private fun deleteHabit(habit: Habit) {
        prefsManager.deleteHabit(habit.id)
        loadHabits()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}